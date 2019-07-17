package org.orcid.core.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.message.Locale;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author Will Simpson
 * 
 */
public class SendBadOrgsEmail {

    private static Logger LOG = LoggerFactory.getLogger(SendBadOrgsEmail.class);
    private static final String FROM_ADDRESS = "\"Laure Haak, Executive Director, ORCID\" <laure@notify.orcid.org>";
    private static final String SUBJECT = "Affiliation bug in ORCID record";

    private TransactionTemplate transactionTemplate;
    private ProfileDao profileDao;
    private AffiliationsManager affiliationsManager;
    private ProfileFundingManager profileFundingManager;
    private LocaleManager localeManager;
    private TemplateManager templateManager;
    private MessageSource messageSource;
    private OrcidUrlManager orcidUrlManager;
    private MailGunManager mailGunManager;
    private NotificationManager notificationManager;
    @Option(name = "-f", usage = "Path to file containing ORCIDs to check and send")
    private File fileToLoad;
    @Option(name = "-o", usage = "ORCID to check and send")
    private String singleOrcidToProcess;
    @Option(name = "-d", usage = "Dry run only (default is false)")
    private boolean dryRun;
    @Option(name = "-b", usage = "Show email body in console output (default is false)")
    private boolean showEmailBody;
    @Option(name = "-l", usage = "Lenient mode (default is false)")
    private boolean lenientMode;
    @Option(name = "-c", usage = "Continue to next record if there is an error (default = stop on error)")
    private boolean continueOnError;
    private int doneCount;
    private int errorCount;

    public static void main(String[] args) throws IOException {
        SendBadOrgsEmail sendBadOrgsEmail = new SendBadOrgsEmail();
        CmdLineParser parser = new CmdLineParser(sendBadOrgsEmail);
        try {
            parser.parseArgument(args);
            sendBadOrgsEmail.validateArgs(parser);
            sendBadOrgsEmail.init();
            sendBadOrgsEmail.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
            System.exit(1);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            System.exit(2);
        }
        System.exit(0);
    }

    private void validateArgs(CmdLineParser parser) throws CmdLineException {
        if (NullUtils.allNull(fileToLoad, singleOrcidToProcess)) {
            throw new CmdLineException(parser, "At least one of -f | -o must be specificed");
        }
    }

    public void execute() throws IOException {
        if (fileToLoad != null) {
            processFile();
        }
        if (singleOrcidToProcess != null) {
            processOrcid(singleOrcidToProcess);
        }
    }

    private void processFile() throws IOException {
        long startTime = System.currentTimeMillis();
        try (BufferedReader br = new BufferedReader(new FileReader(fileToLoad))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                if (StringUtils.isNotBlank(line)) {
                    processOrcid(line.trim());
                }
            }
            long endTime = System.currentTimeMillis();
            String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
            LOG.info("Finished sending bad org emails: doneCount={}, errorCount={}, timeTaken={} (H:m:s.S)", new Object[] { doneCount, errorCount, timeTaken });
        }
    }

    private void processOrcid(final String orcid) {
        LOG.info("Checking record: {}", orcid);
        try {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    ProfileEntity profile = profileDao.find(orcid);
                    final Locale locale = calculateLocale(profile);
                    Set<String> orgDescriptions = new TreeSet<>();
                    List<OrgAffiliationRelationEntity> badAffs = processAffs(profile, locale, orgDescriptions);
                    List<ProfileFundingEntity> badFundings = processFundings(profile, locale, orgDescriptions);
                    if (!badAffs.isEmpty() || !badFundings.isEmpty()) {
                        sendEmail(profile, locale, badAffs, badFundings, orgDescriptions);
                    }
                }
            });
        } catch (RuntimeException e) {
            errorCount++;
            if (continueOnError) {
                LOG.error("Error checking and sending record: orcid={}", orcid, e);
                return;
            } else {
                throw e;
            }
        }
        doneCount++;
    }

    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
        profileDao = (ProfileDao) context.getBean("profileDao");
        affiliationsManager = (AffiliationsManager) context.getBean("affiliationsManager");
        profileFundingManager = (ProfileFundingManager) context.getBean("profileFundingManager");
        localeManager = (LocaleManager) context.getBean("localeManager");
        templateManager = (TemplateManager) context.getBean("templateManager");
        messageSource = (MessageSource) context.getBean("messageSource");
        orcidUrlManager = (OrcidUrlManager) context.getBean("orcidUrlManager");
        mailGunManager = (MailGunManager) context.getBean("mailGunManager");
        notificationManager = (NotificationManager) context.getBean("notificationManager");
    }

    private String createOrgDescription(OrgEntity org, Locale locale) {
        String orgCountry = localeManager.resolveMessage(CountryIsoEntity.class.getName() + "." + org.getCountry(), locale);
        return Arrays.asList(new String[] { org.getName(), org.getCity(), org.getRegion(), orgCountry }).stream().filter(e -> e != null)
                .collect(Collectors.joining(", "));
    }

    private Locale calculateLocale(ProfileEntity profile) {
        Locale locale = (profile.getLocale() == null) ? null : Locale.valueOf(profile.getLocale());
        if (locale == null) {
            locale = Locale.EN;
        }
        final Locale finalLocale = locale;
        return finalLocale;
    }

    private List<OrgAffiliationRelationEntity> processAffs(ProfileEntity profile, final Locale locale, Set<String> orgDescriptions) {
        List<OrgAffiliationRelationEntity> badAffs = profile.getOrgAffiliationRelations().stream().filter(e -> isBadOrg(e.getOrg(), e.getDateCreated()))
                .collect(Collectors.toList());
        badAffs.forEach(a -> {
            String orgDescription = createOrgDescription(a.getOrg(), locale);
            orgDescriptions.add(orgDescription);
            LOG.info("Found bad affiliation: orcid={}, affiliation id={}, visibility={}, orgDescription={}",
                    new Object[] { profile.getId(), a.getId(), a.getVisibility(), orgDescription });
            if (!dryRun) {
                affiliationsManager.updateVisibility(profile.getId(), a.getId(), org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
            }
        });
        return badAffs;
    }

    private List<ProfileFundingEntity> processFundings(ProfileEntity profile, final Locale locale, Set<String> orgDescriptions) {
        List<ProfileFundingEntity> badFundings = profile.getProfileFunding().stream().filter(e -> isBadOrg(e.getOrg(), e.getDateCreated())).collect(Collectors.toList());
        badFundings.forEach(a -> {
            String orgDescription = createOrgDescription(a.getOrg(), locale);
            orgDescriptions.add(orgDescription);
            LOG.info("Found bad funding: orcid={}, funding id={}, visibility={}, orgDescription={}",
                    new Object[] { profile.getId(), a.getId(), a.getVisibility(), orgDescription });
            if (!dryRun) {
                profileFundingManager.updateProfileFundingVisibility(profile.getId(), a.getId(), org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
            }
        });
        return badFundings;
    }

    private boolean isBadOrg(OrgEntity org, Date activityDateCreated) {
        boolean wasModified = org.getLastModified().after(org.getDateCreated());
        if (wasModified) {
            if (lenientMode) {
                OrgDisambiguatedEntity orgDisambiguated = org.getOrgDisambiguated();
                if (orgDisambiguated != null) {
                    long justAfterOrgDisambiguatedCreated = orgDisambiguated.getDateCreated().getTime() + 60000;
                    long justBeforeOrgDisambiguatedCreated = orgDisambiguated.getDateCreated().getTime() - 60000;
                    if (org.getDateCreated().getTime() > justAfterOrgDisambiguatedCreated) {
                        // The org was created well after the disambiguated org,
                        // so it is probably not the original org created for
                        // the disambiguated org
                        return true;
                    }
                    if (org.getDateCreated().getTime() < justBeforeOrgDisambiguatedCreated) {
                        // The org was created well before the disambiguated
                        // org, so can't be the original one.
                        return true;
                    }
                    // Likely to be the original one, and if anything is
                    // different, we could manually revert?
                    if (needsReverting(org, orgDisambiguated)) {
                        LOG.info("Found org to revert to disambiguated info: orcid={}, org id={}, disambiguated org id={}",
                                new Object[] { org.getId(), orgDisambiguated.getId() });
                        // The org will be bad once we revert it, if the user
                        // saw the modified version.
                        return activityDateCreated.after(org.getLastModified());
                    } else {
                        // Likely the original org, it matches the
                        // disambiguated org, yet appears to have been modified.
                        // This probably means it was changed to something
                        // different, then changed back! Have to flag as bad.
                        return true;
                    }
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }

    private boolean needsReverting(OrgEntity org, OrgDisambiguatedEntity orgDisambiguated) {
        boolean needsRevertingToDisambiguatedInfo = false;
        if (!StringUtils.equals(org.getName(), orgDisambiguated.getName())) {
            needsRevertingToDisambiguatedInfo = true;
        } else if (!StringUtils.equals(org.getCity(), orgDisambiguated.getCity())) {
            needsRevertingToDisambiguatedInfo = true;
        } else if (!StringUtils.equals(org.getRegion(), orgDisambiguated.getRegion())) {
            needsRevertingToDisambiguatedInfo = true;
        } else if (!org.getCountry().equals(orgDisambiguated.getCountry())) {
            needsRevertingToDisambiguatedInfo = true;
        }
        return needsRevertingToDisambiguatedInfo;
    }

    private Map<String, Object> createTemplateParams(String orcid, String emailName, Locale locale, Set<String> orgDescriptions) {
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("messages", messageSource);
        templateParams.put("messageArgs", new Object[0]);
        templateParams.put("orcidId", orcid);
        templateParams.put("emailName", emailName);
        templateParams.put("locale", LocaleUtils.toLocale(locale.value()));
        templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
        templateParams.put("baseUriHttp", orcidUrlManager.getBaseUriHttp());
        templateParams.put("subject", SUBJECT);
        templateParams.put("orgDescriptions", orgDescriptions);
        return templateParams;
    }

    private void sendEmail(ProfileEntity profile, final Locale locale, List<OrgAffiliationRelationEntity> badAffs, List<ProfileFundingEntity> badFundings,
            Set<String> orgDescriptions) {
        LOG.info("Sending bad orgs email: orcid={}, num bad affs={}, num bad fundings={}, claimed={}, deactivated={}, deprecated={}, locked={}",
                new Object[] { profile.getId(), badAffs.size(), badFundings.size(), profile.getClaimed(), profile.getDeactivationDate() != null,
                        profile.getDeprecatedDate() != null, profile.getRecordLocked() });
        String emailName = notificationManager.deriveEmailFriendlyName(profile.getId());
        Map<String, Object> templateParams = createTemplateParams(profile.getId(), emailName, locale, orgDescriptions);
        // Generate body from template
        String body = templateManager.processTemplate("bad_orgs_email.ftl", templateParams);
        // Generate html from template
        String html = templateManager.processTemplate("bad_orgs_email_html.ftl", templateParams);
        if (showEmailBody) {
            LOG.info("text email={}", body);
            LOG.info("html email={}", html);
        }
        if (!dryRun) {
            // Update the profile for re-index and cache refresh
            profileDao.updateLastModifiedDateAndIndexingStatus(profile.getId(), IndexingStatus.REINDEX);
            profileDao.flush();
            // Send the email
            boolean mailSent = mailGunManager.sendEmail(FROM_ADDRESS, profile.getPrimaryEmail().getEmail(), SUBJECT, body, html);
            if (!mailSent) {
                throw new RuntimeException("Failed to send email, orcid=" + profile.getId());
            }
        }
    }

}
