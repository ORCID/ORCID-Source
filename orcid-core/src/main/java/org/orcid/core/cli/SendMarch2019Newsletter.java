package org.orcid.core.cli;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.LocaleUtils;
import org.orcid.core.email.trickle.producer.EmailQueueProducer;
import org.orcid.core.email.trickle.producer.EmailTrickleItem;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.TemplateManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.EmailMessage;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.jaxb.model.common.AvailableLocales;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SendMarch2019Newsletter {
    
    /**
     * Size of email address batches to be fetched from DB (not to be sent email, which uses trickle mechanism
     */
    private static final int BATCH_SIZE = 1000;
    
    private String fromAddress = "updates@comms.orcid.org";
    
    private EmailDao emailDaoReadOnly;
    
    private NotificationManager notificationManager;
    
    private EmailFrequencyDao emailFrequencyDaoReadOnly;
    
    private OrcidUrlManager orcidUrlManager;
    
    private EncryptionManager encryptionManager;
    
    private TemplateManager templateManager;
    
    private MessageSource messages;
    
    private EmailQueueProducer emailQueueProducer;
    
    @SuppressWarnings("resource")
    private void init() {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        emailDaoReadOnly = (EmailDao) context.getBean("emailDaoReadOnly");
        emailFrequencyDaoReadOnly = (EmailFrequencyDao) context.getBean("emailFrequencyDaoReadOnly");
        notificationManager = (NotificationManager) context.getBean("notificationManagerV3");
        encryptionManager = (EncryptionManager) context.getBean("encryptionManager");
        orcidUrlManager = (OrcidUrlManager) context.getBean("orcidUrlManager");
        templateManager = (TemplateManager) context.getBean("templateManager");
        messages = (MessageSource) context.getBean("messageSource");
        emailQueueProducer = (EmailQueueProducer) context.getBean("emailQueueProducer");
    }
    
    private void send() {
        int offset = 0;
        List<EmailEntity> emails = emailDaoReadOnly.getMarch2019QuarterlyEmailRecipients(offset, BATCH_SIZE);
        while (!emails.isEmpty()) {
            sendToEmails(emails);
            offset += BATCH_SIZE;
            emails = emailDaoReadOnly.getMarch2019QuarterlyEmailRecipients(offset, BATCH_SIZE);
        }
    }
    
    private void sendToEmails(List<EmailEntity> emails) {
        for (EmailEntity email : emails) {
            EmailMessage emailMessage = getEmailMessage(email);
            EmailTrickleItem item = new EmailTrickleItem();
            item.setEmailMessage(emailMessage);
            item.setOrcid(email.getProfile().getId());
            item.setSuccessType(ProfileEventType.MARCH_2019_SENT);
            item.setSkippedType(ProfileEventType.MARCH_2019_SKIPPED);
            item.setFailureType(ProfileEventType.MARCH_2019_FAILED);
            emailQueueProducer.queueEmail(item);
        }
    }
    
    private EmailMessage getEmailMessage(EmailEntity email) {
        Locale locale = getUserLocaleFromProfileEntity(email.getProfile());
        String emailName = notificationManager.deriveEmailFriendlyName(email.getProfile());
        Map<String, Object> params = new HashMap<>();
        params.put("locale", locale);
        params.put("messages", messages);
        params.put("messageArgs", new Object[0]);
        params.put("emailName", emailName);
        params.put("baseUri", orcidUrlManager.getBaseUrl());
        params.put("unsubscribeLink", getUnsubscribeLink(email.getProfile().getId()));        
        
        String subject = messages.getMessage("email.march_2019.subject", null, locale);
        String bodyText = templateManager.processTemplate("march_2019.ftl", params, locale);
        String bodyHtml = templateManager.processTemplate("march_2019_html.ftl", params, locale);
        
        EmailMessage emailMessage = new EmailMessage();
        emailMessage.setSubject(subject);
        emailMessage.setBodyText(bodyText);
        emailMessage.setBodyHtml(bodyHtml);
        emailMessage.setFrom(fromAddress);
        emailMessage.setTo(email.getEmail());
        return emailMessage;
    }

    private Locale getUserLocaleFromProfileEntity(ProfileEntity profile) {
        String locale = profile.getLocale();
        if (locale != null) {
            AvailableLocales loc = AvailableLocales.valueOf(locale);
            return LocaleUtils.toLocale(loc.value());
        }
        
        return LocaleUtils.toLocale("en");
    }
    
    protected String getUnsubscribeLink(String orcidId) {
        EmailFrequencyEntity entity = emailFrequencyDaoReadOnly.findByOrcid(orcidId);
        String uuid = entity.getId();
        String encryptedId = encryptionManager.encryptForExternalUse(uuid);
        String base64EncodedId = Base64.encodeBase64URLSafeString(encryptedId.getBytes());
        return orcidUrlManager.getBaseUrl() + "/unsubscribe/" + base64EncodedId;
    }

    public static void main(String[] args) {
        SendMarch2019Newsletter sender = new SendMarch2019Newsletter();
        if (args.length > 0) {
            sender.fromAddress = args[0];
        }
        sender.init();
        sender.send();
    }

}
