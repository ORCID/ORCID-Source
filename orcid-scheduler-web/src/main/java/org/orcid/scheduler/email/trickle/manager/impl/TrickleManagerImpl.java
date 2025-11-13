package org.orcid.scheduler.email.trickle.manager.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.email.trickle.producer.EmailTrickleItem;
import org.orcid.core.manager.v3.EmailMessage;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.dao.EmailScheduleDao;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;
import org.orcid.persistence.jpa.entities.ProfileEventType;
import org.orcid.scheduler.email.trickle.TrickleTooHeavyException;
import org.orcid.scheduler.email.trickle.manager.TrickleManager;
import org.orcid.utils.email.MailGunManager;
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
    private ProfileEventDao profileEventDao;

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;

    @Resource
    private MailGunManager mailGunManager;

    @Override
    public synchronized void attemptSend(EmailTrickleItem item) {
        Long scheduleId = emailScheduleDaoReadOnly.getValidScheduleId();
        if (scheduleId == -1L) {
            throw new TrickleTooHeavyException();
        }

        if (notAlreadySent(item)) {
            if (canStillSend(item.getOrcid(), item.getEmailMessage().getTo())) {
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
            LOG.info("Attempt already made to send email to {}", item.getEmailMessage().getTo());
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
        event.setOrcid(orcid);
        event.setType(type);
        return event;
    }

    private boolean canStillSend(String orcid, String email) {
        return emailStillExists(email) && emailPreferencesAllowSend(orcid) && profileStillActive(orcid);
    }

    private boolean profileStillActive(String orcid) {
        ProfileEntity profileEntity = profileDaoReadOnly.find(orcid);
        return profileEntity.getDeactivationDate() == null && profileEntity.getDeprecatedDate() == null && profileEntity.isAccountNonLocked();
    }

    private boolean emailStillExists(String email) {
        return emailManagerReadOnly.emailExists(email);
    }

    private boolean emailPreferencesAllowSend(String orcid) {
        return emailFrequencyDaoReadOnly.findByOrcid(orcid).getSendQuarterlyTips();
    }

    private boolean notAlreadySent(EmailTrickleItem item) {
        List<ProfileEventEntity> events = profileDaoReadOnly.getProfileEvents(item.getOrcid(),
                Arrays.asList(item.getSuccessType(), item.getFailureType(), item.getSkippedType()));
        if (!events.isEmpty()) {
            LOG.info("Found email event for {}: {} ({})", new Object[] { item.getOrcid(), events.get(0).getType().name(), events.get(0).getDateCreated() });
            return false;
        }
        return true;
    }

}
