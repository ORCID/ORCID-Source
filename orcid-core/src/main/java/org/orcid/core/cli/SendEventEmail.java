/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 
 * @author rcpeters
 * 
 */
public class SendEventEmail {

    private ProfileDao profileDao;
    
    private OrcidProfileManager orcidProfileManager;

    private MailGunManager mailGunManager;

    private TemplateManager templateManager;

    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    private OrcidUrlManager orcidUrlManager;

    private NotificationManager notificationManager;
    
    private TransactionTemplate transactionTemplate;
    
    private static Logger LOG = LoggerFactory.getLogger(SendEventEmail.class);
    
    private static final int CHUNK_SIZE = 1000;

    @Option(name = "-testSendToOrcids", usage = "Send validation email to following ORCID Ids")
    private String orcs;

    @Option(name = "-run", usage = "Runs sendEmailByEvent, which loops through all orcids who haven't been verified and not sent the crossref email and sends the crossref email")
    private String run;

    public static void main(String... args) {
        SendEventEmail se = new SendEventEmail();
        CmdLineParser parser = new CmdLineParser(se);
        if (args == null) {
            parser.printUsage(System.err);
        }
        try {
            parser.parseArgument(args);
            se.execute();
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }

    }

    private void execute() {
        init();
        if (run != null && run.equalsIgnoreCase("true")) {
            sendEmailByEvent();
        } else if (orcs != null) {
            for (String orc : orcs.split(" ")) {
                OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orc);
                sendEmail(orcidProfile);
            }
        }
    }

    private ProfileEventType sendEmail(OrcidProfile orcidProfile) {
        
        boolean primaryNotNull = orcidProfile.getOrcidBio() != null && orcidProfile.getOrcidBio().getContactDetails() != null
                && orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() != null
                && orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue() != null;
  
        boolean needsVerification = primaryNotNull 
                && !orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().isVerified() 
                && orcidProfile.getType().equals(OrcidType.USER) ;
        
        if (needsVerification) {
            try {
            String email = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
            String emailFriendlyName = notificationManager.deriveEmailFriendlyName(orcidProfile);
            Map<String, Object> templateParams = new HashMap<String, Object>();
            templateParams.put("emailName", emailFriendlyName);
            String verificationUrl = null;
            try {
                verificationUrl = notificationManager.createVerificationUrl(email, new URI(orcidUrlManager.getBaseUrl()));
            } catch (URISyntaxException e) {
                LOG.debug("SendEventEmail exception", e);
            }
            templateParams.put("verificationUrl", verificationUrl);
            templateParams.put("orcid", orcidProfile.getOrcid().getValue());
            templateParams.put("baseUri", orcidUrlManager.getBaseUrl());
            String text = templateManager.processTemplate("verification_email_w_crossref.ftl", templateParams);
            String html = templateManager.processTemplate("verification_email_w_crossref_html.ftl", templateParams);
            mailGunManager.sendSimpleVerfiyEmail("support@verify.orcid.org", email, "Please verify your email", text, html);
            } catch (Exception e) {
                LOG.error("Exception trying to send email to: " +orcidProfile.getOrcidId(), e);
                return ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_FAIL;
            }
            return ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_SENT;
        }
        return ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_SKIPPED;
    }

    private void sendEmailByEvent() {
        long startTime = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        List<String> orcids = Collections.EMPTY_LIST;
        int doneCount = 0;
        do {
            orcids = profileDao.findByEventType(CHUNK_SIZE, ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_CHECK, null, true);
            for (final String orcid : orcids) {
                LOG.info("Migrating emails for profile: {}", orcid);
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
                        profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING_CHECK));
                        profileEventDao.persist(new ProfileEventEntity(orcid, sendEmail(orcidProfile)));
                    }
                });
                doneCount++;
            }
            LOG.info("Sending crossref verify emails on number: {}", doneCount);
        } while (!orcids.isEmpty());
        long endTime = System.currentTimeMillis();
        String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
        LOG.info("Sending crossref verify emails: doneCount={}, timeTaken={} (H:m:s.S)", doneCount, timeTaken);
    }

    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        profileDao = (ProfileDao) context.getBean("profileDao");
        profileEventDao = (GenericDao<ProfileEventEntity, Long>) context.getBean("profileEventDao");
        mailGunManager = (MailGunManager) context.getBean("mailGunManager");
        orcidProfileManager = (OrcidProfileManager) context.getBean("orcidProfileManager");
        transactionTemplate = (TransactionTemplate) context.getBean("transactionTemplate");
        notificationManager = (NotificationManager) context.getBean("notificationManager");
        templateManager = (TemplateManager) context.getBean("templateManager");
        orcidUrlManager = (OrcidUrlManager) context.getBean("orcidUrlManager");
    }

}
