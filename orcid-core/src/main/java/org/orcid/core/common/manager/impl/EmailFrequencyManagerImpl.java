package org.orcid.core.common.manager.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailFrequencyManagerImpl implements EmailFrequencyManager {

    private static final Logger LOG = LoggerFactory.getLogger(EmailFrequencyManagerImpl.class);
    
    @Resource(name = "emailFrequencyDao")
    private EmailFrequencyDao emailFrequencyDao;
    
    @Resource(name = "emailFrequencyDaoReadOnly")
    private EmailFrequencyDao emailFrequencyDaoReadOnly;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
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
            
            if(profileEntity.getSendOrcidNews() != null && profileEntity.getSendOrcidNews()) {
                result.put("send_quarterly_tips", String.valueOf(emailFrequencyDays));
            } else {
                result.put("send_quarterly_tips", SendEmailFrequency.NEVER.value());
            }
        }
        return result;
    }
    
    @Override
    public boolean createEmailFrequency(String orcid, SendEmailFrequency sendChangeNotifications, SendEmailFrequency sendAdministrativeChangeNotifications,
            SendEmailFrequency sendMemberUpdateRequests, Boolean sendQuarterlyTips) {
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
        return true;
    }

    @Override
    public boolean updateSendChangeNotifications(String orcid, SendEmailFrequency frequency) {
        return emailFrequencyDao.updateSendChangeNotifications(orcid, frequency);        
    }

    @Override
    public boolean updateSendAdministrativeChangeNotifications(String orcid, SendEmailFrequency frequency) {
        return emailFrequencyDao.updateSendAdministrativeChangeNotifications(orcid, frequency);
    }

    @Override
    public boolean updateSendMemberUpdateRequests(String orcid, SendEmailFrequency frequency) {
        return emailFrequencyDao.updateSendMemberUpdateRequests(orcid, frequency);
    }

    @Override
    public boolean updateSendQuarterlyTips(String orcid, Boolean enabled) {
        return emailFrequencyDao.updateSendQuarterlyTips(orcid, enabled);
    }    
}
