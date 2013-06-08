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
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.jaxb.model.message.OrcidProfile;
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
    private String baseUri = "https://orcid.org";  //HARDCODED FIX
    private NotificationManager notificationManager;
    private TransactionTemplate transactionTemplate;
    private static Logger LOG = LoggerFactory.getLogger(SendEventEmail.class);
    private static final int CHUNK_SIZE = 1000;

    public static void main(String... args) {
        new SendEventEmail().run();
    }

    private void run() {
        init();
        sendEmailByEvent();
        OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile("0000-0003-0468-350X");
        String email = orcidProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail().getValue();
        String emailFriendlyName = notificationManager.deriveEmailFriendlyName(orcidProfile);
        Map<String, Object> templateParams = new HashMap<String, Object>();
        templateParams.put("emailName", emailFriendlyName);
        String verificationUrl = null;
        try {
            verificationUrl = notificationManager.createVerificationUrl(email, new URI(baseUri));
        } catch (URISyntaxException e) {
            LOG.debug("SendEventEmail exception", e);
        }
        templateParams.put("verificationUrl", verificationUrl);
        templateParams.put("orcid", orcidProfile.getOrcid().getValue());
        templateParams.put("baseUri", baseUri);
        String text = templateManager.processTemplate("verification_email_w_crossref.ftl", templateParams);
        mailGunManager.sendSimpleVerfiyEmail("support@verify.orcid.org","info@rcpeters.com","Please verify your email",text);
    }

    private void sendEmailByEvent() {
        long startTime = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        List<String> orcids = Collections.EMPTY_LIST;
        int doneCount = 0;
        orcids = profileDao.findByEventType(CHUNK_SIZE, ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING, null, true);
        for (final String orcid : orcids) {
            LOG.info("Migrating emails for profile: {}", orcid);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    OrcidProfile orcidProfile = orcidProfileManager.retrieveOrcidProfile(orcid);
                    if (!orcidProfile.isDeactivated()) {
                        profileEventDao.persist(new ProfileEventEntity(orcid, ProfileEventType.EMAIL_VERIFY_CROSSREF_MARKETING));
                    }
                }
            });
            doneCount++;
        }
        long endTime = System.currentTimeMillis();
        String timeTaken = DurationFormatUtils.formatDurationHMS(endTime - startTime);
        LOG.info("Finished migrating emails: doneCount={}, timeTaken={} (H:m:s.S)", doneCount, timeTaken);
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

    }

}
