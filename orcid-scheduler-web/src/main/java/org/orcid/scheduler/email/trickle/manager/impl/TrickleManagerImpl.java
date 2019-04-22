package org.orcid.scheduler.email.trickle.manager.impl;

import java.util.Arrays;
import java.util.Date;

import javax.annotation.Resource;

import org.orcid.core.email.trickle.producer.EmailTrickleItem;
import org.orcid.core.manager.impl.MailGunManager;
import org.orcid.core.manager.v3.EmailMessage;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.dao.EmailScheduleDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.scheduler.email.trickle.TrickleTooHeavyException;
import org.orcid.scheduler.email.trickle.manager.TrickleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class TrickleManagerImpl implements TrickleManager {

    private static final Logger LOG = LoggerFactory.getLogger(TrickleManagerImpl.class);

    @Resource(name = "emailScheduleDaoReadOnly")
    private EmailScheduleDao emailScheduleDaoReadOnly;

    @Resource
    private EmailScheduleDao emailScheduleDao;

    @Resource(name = "emailFrequencyDaoReadOnly")
    private EmailFrequencyDao emailFrequencyDaoReadOnly;

    @Resource(name = "profileDaoReadOnly")
    private ProfileDao profileDaoReadOnly;

    @Resource
    private GenericDao<ProfileEventEntity, Long> profileEventDao;

    @Resource
    private MailGunManager mailGunManager;

    @Override
    public synchronized void attemptSend(EmailTrickleItem item) {
        Long scheduleId = emailScheduleDaoReadOnly.getValidScheduleId();
        if (scheduleId == -1L) {
            throw new TrickleTooHeavyException();
        }

        if (notAlreadySent(item)) {
            if (emailPreferencesAllowSend(item.getOrcid())) {
                Date now = new Date();
                LOG.info("Cleared to send at {}", now);
                EmailMessage emailMessage = item.getEmailMessage();
                LOG.info("Sending email from {} to {} with subject {}", new Object[] { emailMessage.getFrom(), emailMessage.getTo(), emailMessage.getSubject() });
                if (send(emailMessage, item.isMarketingMail())) {
                    LOG.info("Email sent to {}", emailMessage.getTo());
                    profileEventDao.merge(getProfileEventEntity(item.getSuccessType(), item.getOrcid()));
                    emailScheduleDao.updateLatestSent(scheduleId, now);
                } else {
                    profileEventDao.merge(getProfileEventEntity(item.getFailureType(), item.getOrcid()));
                }
            } else {
                profileEventDao.merge(getProfileEventEntity(item.getSkippedType(), item.getOrcid()));
            }
        } else {
            LOG.info("Email already sent to {}", item.getEmailMessage().getTo());
        }
    }

    private boolean send(EmailMessage emailMessage, boolean marketing) {
        if (marketing) {
            return mailGunManager.sendMarketingEmail(emailMessage.getFrom(), emailMessage.getTo(), emailMessage.getSubject(), emailMessage.getBodyText(),
                    emailMessage.getBodyHtml());
        } else {
            return mailGunManager.sendEmail(emailMessage.getFrom(), emailMessage.getTo(), emailMessage.getSubject(), emailMessage.getBodyText(),
                    emailMessage.getBodyHtml());
        }
    }

    private ProfileEventEntity getProfileEventEntity(ProfileEventType type, String orcid) {
        ProfileEventEntity event = new ProfileEventEntity();
        event.setDateCreated(new Date());
        event.setLastModified(new Date());
        event.setOrcid(orcid);
        event.setType(type);
        return event;
    }

    private boolean emailPreferencesAllowSend(String orcid) {
        return emailFrequencyDaoReadOnly.findByOrcid(orcid).getSendQuarterlyTips();
    }

    private boolean notAlreadySent(EmailTrickleItem item) {
        return profileDaoReadOnly.getProfileEvents(item.getOrcid(), Arrays.asList(item.getSuccessType(), item.getFailureType(), item.getSkippedType())).isEmpty();
    }

}
