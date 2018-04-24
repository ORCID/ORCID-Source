package org.orcid.core.common.manager.impl;

import java.util.Date;
import java.util.UUID;

import javax.annotation.Resource;

import org.orcid.core.common.manager.EmailFrequencyManager;
import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.dao.EmailFrequencyDao;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;

public class EmailFrequencyManagerImpl implements EmailFrequencyManager {

    @Resource(name = "emailFrequencyDao")
    private EmailFrequencyDao emailFrequencyDao;
    
    @Resource(name = "emailFrequencyDaoReadOnly")
    private EmailFrequencyDao emailFrequencyDaoReadOnly;
    
    
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
