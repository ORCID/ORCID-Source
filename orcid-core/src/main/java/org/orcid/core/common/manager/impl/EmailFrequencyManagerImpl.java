package org.orcid.core.common.manager.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.core.profile.history.ProfileHistoryEventType;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class EmailFrequencyManagerImpl implements EmailFrequencyManager {

    private static final Logger LOG = LoggerFactory.getLogger(EmailFrequencyManagerImpl.class);
    
    @Resource(name = "emailFrequencyDao")
    private EmailFrequencyDao emailFrequencyDao;
    
    @Resource(name = "emailFrequencyDaoReadOnly")
    private EmailFrequencyDao emailFrequencyDaoReadOnly;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private ProfileHistoryEventManager profileHistoryEventManager;
    
    @Override
    public Map<String, String> getEmailFrequency(String orcid) {
        Map<String, String> result = new HashMap<String, String>();
        try {
            EmailFrequencyEntity entity = emailFrequencyDaoReadOnly.findByOrcid(orcid);
            result.put("send_administrative_change_notifications", String.valueOf(entity.getSendAdministrativeChangeNotifications()));
            result.put("send_change_notifications", String.valueOf(entity.getSendChangeNotifications()));
            result.put("send_member_update_requests", String.valueOf(entity.getSendMemberUpdateRequests()));
            result.put("send_quarterly_tips", String.valueOf(entity.getSendQuarterlyTips()));
        } catch(Exception e) {
            LOG.warn("Couldn't find email_frequency for {} using profile_entity settings", orcid);
            ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
            Float emailFrequencyDays = profileEntity.getSendEmailFrequencyDays();
            if(profileEntity.getSendAdministrativeChangeNotifications() != null && profileEntity.getSendAdministrativeChangeNotifications()) {
                result.put("send_administrative_change_notifications", String.valueOf(emailFrequencyDays));
            } else {
                result.put("send_administrative_change_notifications", SendEmailFrequency.NEVER.value());
            }
            
            if(profileEntity.getSendChangeNotifications() != null && profileEntity.getSendChangeNotifications()) {
                result.put("send_change_notifications", String.valueOf(emailFrequencyDays));
            } else {
                result.put("send_change_notifications", SendEmailFrequency.NEVER.value());
            }
            
            if(profileEntity.getSendMemberUpdateRequests() != null && profileEntity.getSendMemberUpdateRequests()) {
                result.put("send_member_update_requests", String.valueOf(emailFrequencyDays));
            } else {
                result.put("send_member_update_requests", SendEmailFrequency.NEVER.value());
            }
            
            if(profileEntity.getSendOrcidNews() != null && profileEntity.getSendOrcidNews() && emailFrequencyDays < SendEmailFrequency.NEVER.floatValue()) {
                result.put("send_quarterly_tips", String.valueOf(Boolean.TRUE));
            } else {
                result.put("send_quarterly_tips", String.valueOf(Boolean.FALSE));
            }
        }
        return result;
    }
    
    @Override
    public boolean createOnRegister(String orcid, SendEmailFrequency sendChangeNotifications, SendEmailFrequency sendAdministrativeChangeNotifications,
            SendEmailFrequency sendMemberUpdateRequests, Boolean sendQuarterlyTips) {
        return createEmailFrequency(orcid, sendChangeNotifications, sendAdministrativeChangeNotifications,
                sendMemberUpdateRequests, sendQuarterlyTips, true);
    }
    
    private boolean createEmailFrequency(String orcid, SendEmailFrequency sendChangeNotifications, SendEmailFrequency sendAdministrativeChangeNotifications,
            SendEmailFrequency sendMemberUpdateRequests, Boolean sendQuarterlyTips, boolean trackEvent) {
        EmailFrequencyEntity entity = new EmailFrequencyEntity();
        Date now = new Date();
        entity.setId(UUID.randomUUID().toString());
        entity.setDateCreated(now);
        entity.setLastModified(now);
        entity.setOrcid(orcid);
        entity.setSendAdministrativeChangeNotifications(sendAdministrativeChangeNotifications.floatValue());
        entity.setSendChangeNotifications(sendChangeNotifications.floatValue());
        entity.setSendMemberUpdateRequests(sendMemberUpdateRequests.floatValue());
        entity.setSendQuarterlyTips(sendQuarterlyTips);
        emailFrequencyDao.persist(entity);
        if(trackEvent) {
            profileHistoryEventManager.recordEvent(ProfileHistoryEventType.EMAIL_FREQUENCY_CREATED_ON_REGISTER, orcid, "send_quarterly_tips " + sendQuarterlyTips);
        }
        return true;
    }
    
    @Override
    public boolean updateSendChangeNotifications(String orcid, SendEmailFrequency frequency) {
        boolean result = false;
        if(emailFrequencyExists(orcid)) {
            result = emailFrequencyDao.updateSendChangeNotifications(orcid, frequency);
        } else {
            LOG.warn("Creating email_frequency based on profile_entity settings");
            ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
            Float emailFrequencyDays = profileEntity.getSendEmailFrequencyDays();
            SendEmailFrequency defaultEmailFrequency = SendEmailFrequency.fromValue(emailFrequencyDays);
            SendEmailFrequency sendChangeNotifications = frequency;
            
            SendEmailFrequency sendAdministrativeChangeNotifications;
            if(profileEntity.getSendAdministrativeChangeNotifications() != null && profileEntity.getSendAdministrativeChangeNotifications()) {
                sendAdministrativeChangeNotifications = defaultEmailFrequency;
            } else {
                sendAdministrativeChangeNotifications = SendEmailFrequency.NEVER;
            }            
            
            SendEmailFrequency sendMemberUpdateRequests;
            if(profileEntity.getSendMemberUpdateRequests() != null && profileEntity.getSendMemberUpdateRequests()) {
                sendMemberUpdateRequests = defaultEmailFrequency;
            } else {
                sendMemberUpdateRequests = SendEmailFrequency.NEVER;
            }
            
            Boolean sendQuarterlyTips;
            if(profileEntity.getSendOrcidNews() != null && profileEntity.getSendOrcidNews() && SendEmailFrequency.NEVER.equals(defaultEmailFrequency)) {
                sendQuarterlyTips = true;
            } else {
                sendQuarterlyTips = false;
            }
            
            result = createEmailFrequency(orcid, sendChangeNotifications, sendAdministrativeChangeNotifications, sendMemberUpdateRequests, sendQuarterlyTips, false);
        }        
        
        profileHistoryEventManager.recordEvent(ProfileHistoryEventType.UPDATE_AMEND_NOTIF_FREQ, orcid, "New value: " + frequency.value());
        
        return result;
    }

    @Override
    public boolean updateSendAdministrativeChangeNotifications(String orcid, SendEmailFrequency frequency) {
        boolean result = false;
        if(emailFrequencyExists(orcid)) {
            result = emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, frequency);
        } else {
            LOG.warn("Creating email_frequency based on profile_entity settings");
            ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
            Float emailFrequencyDays = profileEntity.getSendEmailFrequencyDays();
            SendEmailFrequency defaultEmailFrequency = SendEmailFrequency.fromValue(emailFrequencyDays);
            SendEmailFrequency sendAdministrativeChangeNotifications = frequency;
            
            SendEmailFrequency sendChangeNotifications;
            if(profileEntity.getSendChangeNotifications() != null && profileEntity.getSendChangeNotifications()) {
                sendChangeNotifications = defaultEmailFrequency;
            } else {
                sendChangeNotifications = SendEmailFrequency.NEVER;
            }            
            
            SendEmailFrequency sendMemberUpdateRequests;
            if(profileEntity.getSendMemberUpdateRequests() != null && profileEntity.getSendMemberUpdateRequests()) {
                sendMemberUpdateRequests = defaultEmailFrequency;
            } else {
                sendMemberUpdateRequests = SendEmailFrequency.NEVER;
            }
            
            Boolean sendQuarterlyTips;
            if(profileEntity.getSendOrcidNews() != null && profileEntity.getSendOrcidNews() && SendEmailFrequency.NEVER.equals(defaultEmailFrequency)) {
                sendQuarterlyTips = true;
            } else {
                sendQuarterlyTips = false;
            }
            
            result = createEmailFrequency(orcid, sendChangeNotifications, sendAdministrativeChangeNotifications, sendMemberUpdateRequests, sendQuarterlyTips, false);
        }        
        
        profileHistoryEventManager.recordEvent(ProfileHistoryEventType.UPDATE_ADMINISTRATIVE_NOTIF_FREQ, orcid, "New value: " + frequency.value());
        
        return result;
    }

    @Override
    public boolean updateSendMemberUpdateRequests(String orcid, SendEmailFrequency frequency) {
        boolean result = false;
        if(emailFrequencyExists(orcid)) {
            result = emailFrequencyDao.updateSendMemberUpdateRequests(orcid, frequency);
        } else {
            LOG.warn("Creating email_frequency based on profile_entity settings");
            ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
            Float emailFrequencyDays = profileEntity.getSendEmailFrequencyDays();
            SendEmailFrequency defaultEmailFrequency = SendEmailFrequency.fromValue(emailFrequencyDays);
            SendEmailFrequency sendMemberUpdateRequests = frequency;
            
            SendEmailFrequency sendChangeNotifications;
            if(profileEntity.getSendChangeNotifications() != null && profileEntity.getSendChangeNotifications()) {
                sendChangeNotifications = defaultEmailFrequency;
            } else {
                sendChangeNotifications = SendEmailFrequency.NEVER;
            }            
            
            SendEmailFrequency sendAdministrativeChangeNotifications;
            if(profileEntity.getSendAdministrativeChangeNotifications() != null && profileEntity.getSendAdministrativeChangeNotifications()) {
                sendAdministrativeChangeNotifications = defaultEmailFrequency;
            } else {
                sendAdministrativeChangeNotifications = SendEmailFrequency.NEVER;
            }
            
            Boolean sendQuarterlyTips;
            if(profileEntity.getSendOrcidNews() != null && profileEntity.getSendOrcidNews() && SendEmailFrequency.NEVER.equals(defaultEmailFrequency)) {
                sendQuarterlyTips = true;
            } else {
                sendQuarterlyTips = false;
            }
            
            result = createEmailFrequency(orcid, sendChangeNotifications, sendAdministrativeChangeNotifications, sendMemberUpdateRequests, sendQuarterlyTips, false);
        }        
        
        profileHistoryEventManager.recordEvent(ProfileHistoryEventType.UPDATE_MEMBER_PERMISSION_NOTIF_FREQ, orcid, "New value: " + frequency.value());
        
        return result;
    }

    @Override
    public boolean updateSendQuarterlyTips(String orcid, Boolean enabled) {
        boolean result = false;
        if(emailFrequencyExists(orcid)) {
            result = emailFrequencyDao.updateSendQuarterlyTips(orcid, enabled);
        } else {
            LOG.warn("Creating email_frequency based on profile_entity settings");
            ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
            Float emailFrequencyDays = profileEntity.getSendEmailFrequencyDays();
            SendEmailFrequency defaultEmailFrequency = SendEmailFrequency.fromValue(emailFrequencyDays);
            
            SendEmailFrequency sendMemberUpdateRequests;
            if(profileEntity.getSendMemberUpdateRequests() != null && profileEntity.getSendMemberUpdateRequests()) {
                sendMemberUpdateRequests = defaultEmailFrequency;
            } else {
                sendMemberUpdateRequests = SendEmailFrequency.NEVER;
            }
            
            SendEmailFrequency sendChangeNotifications;
            if(profileEntity.getSendChangeNotifications() != null && profileEntity.getSendChangeNotifications()) {
                sendChangeNotifications = defaultEmailFrequency;
            } else {
                sendChangeNotifications = SendEmailFrequency.NEVER;
            }            
            
            SendEmailFrequency sendAdministrativeChangeNotifications;
            if(profileEntity.getSendAdministrativeChangeNotifications() != null && profileEntity.getSendAdministrativeChangeNotifications()) {
                sendAdministrativeChangeNotifications = defaultEmailFrequency;
            } else {
                sendAdministrativeChangeNotifications = SendEmailFrequency.NEVER;
            }
            
            Boolean sendQuarterlyTips = enabled;
            
            result = createEmailFrequency(orcid, sendChangeNotifications, sendAdministrativeChangeNotifications, sendMemberUpdateRequests, sendQuarterlyTips, false);
        }

        profileHistoryEventManager.recordEvent(ProfileHistoryEventType.UPDATE_QUARTERLY_TIPS_NOTIF, orcid, "New value: " + enabled);
        
        return result;
    }
    
    @Override
    public boolean emailFrequencyExists(String orcid) {
        try {
            emailFrequencyDao.findByOrcid(orcid);            
        } catch(NoResultException nre) {
            return false;
        }
        return true;
    }
    
    @Override
    @Transactional
    public boolean update(String orcid, SendEmailFrequency sendChangeNotifications, SendEmailFrequency sendAdministrativeChangeNotifications,
            SendEmailFrequency sendMemberUpdateRequests, Boolean sendQuarterlyTips) {
        EmailFrequencyEntity entity = emailFrequencyDao.findByOrcid(orcid);
        entity.setLastModified(new Date());
        entity.setSendAdministrativeChangeNotifications(sendAdministrativeChangeNotifications.floatValue());
        entity.setSendChangeNotifications(sendChangeNotifications.floatValue());
        entity.setSendMemberUpdateRequests(sendMemberUpdateRequests.floatValue());
        entity.setSendQuarterlyTips(sendQuarterlyTips);
        emailFrequencyDao.merge(entity);
        return true;
    }
    
}
