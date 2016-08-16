/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.utils.NullUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
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

    private TransactionTemplate transactionTemplate;
    private ProfileDao profileDao;
    private AffiliationsManager affiliationsManager;
    private ProfileFundingManager profileFundingManager;
    @Option(name = "-f", usage = "Path to file containing ORCIDs to check and send")
    private File fileToLoad;
    @Option(name = "-o", usage = "ORCID to check and send")
    private String orcid;
    @Option(name = "-d", usage = "Dry run only (default is false)")
    private boolean dryRun;
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
        if (NullUtils.allNull(fileToLoad, orcid)) {
            throw new CmdLineException(parser, "At least one of -f | -o must be specificed");
        }
    }

    public void execute() throws IOException {
        if (fileToLoad != null) {
            processFile();
        }
        if (orcid != null) {
            processOrcid(orcid);
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
                    List<OrgAffiliationRelationEntity> badAffs = profile.getOrgAffiliationRelations().stream().filter(e -> isBadOrg(e.getOrg(), e.getDateCreated()))
                            .collect(Collectors.toList());
                    badAffs.forEach(a -> {
                        LOG.info("Found bad affiliation: orcid={}, affiliation id={}, visibility={}", new Object[] { orcid, a.getId(), a.getVisibility() });
                        if (!dryRun) {
                            affiliationsManager.updateVisibility(orcid, a.getId(), Visibility.PRIVATE);
                        }
                    });
                    List<ProfileFundingEntity> badFundings = profile.getProfileFunding().stream().filter(e -> isBadOrg(e.getOrg(), e.getDateCreated()))
                            .collect(Collectors.toList());
                    badFundings.forEach(a -> {
                        LOG.info("Found bad funding: orcid={}, funding id={}, visibility={}", new Object[] { orcid, a.getId(), a.getVisibility() });
                        if (!dryRun) {
                            profileFundingManager.updateProfileFundingVisibility(orcid, a.getId(), Visibility.PRIVATE);
                        }
                    });
                    if (!badAffs.isEmpty() || !badFundings.isEmpty()) {
                        // XXX Update the profile for re-index and cache flush
                        LOG.info("Sending bad orgs email: orcid={}, num bad affs={}, num bad fundings={}", new Object[] { orcid, badAffs.size(), badFundings.size() });
                        if (!dryRun) {
                            // XXX Send the email
                        }
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
    }

    public boolean isBadOrg(OrgEntity org, Date activityDateCreated) {
        boolean wasModified = org.getLastModified().after(org.getDateCreated());
        if (wasModified) {
            OrgDisambiguatedEntity orgDisambiguated = org.getOrgDisambiguated();
            if (orgDisambiguated != null) {
                long justAfterOrgDisambiguatedCreated = orgDisambiguated.getDateCreated().getTime() + 60000;
                long justBeforeOrgDisambiguatedCreated = orgDisambiguated.getDateCreated().getTime() - 60000;
                if (org.getDateCreated().getTime() > justAfterOrgDisambiguatedCreated) {
                    // The org was created well after the disambiguated org, so
                    // it is probably not the original org created for the
                    // disambiguated org
                    return true;
                }
                if (org.getDateCreated().getTime() < justBeforeOrgDisambiguatedCreated) {
                    // The org was created well before the disambiguated org, so
                    // can't be the original one.
                    return true;
                }
                // Likely to be the original one, and if the meta-data are the
                // same,
                // it is probably OK. If anything is different, it could be a
                // bad org.
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
                if (needsRevertingToDisambiguatedInfo) {
                    LOG.info("Found org to revert to disambiguated info: orcid={}, org id={}, disambiguated org id={}",
                            new Object[] { org.getId(), orgDisambiguated.getId() });
                    // The org will be bad once we revert it, if the user saw
                    // the modified version.
                    return activityDateCreated.after(org.getLastModified());
                }
                return true;
            } else {
                return true;
            }
        }
        return false;
    }

}
