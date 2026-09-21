package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface EmailFrequencyDao extends GenericDao<EmailFrequencyEntity, String> {
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    EmailFrequencyEntity findByOrcid(String orcid);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateSendChangeNotifications(String orcid, SendEmailFrequency frequency);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateSendAdministrativeChangeNotifications(String orcid, SendEmailFrequency frequency);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateSendMemberUpdateRequests(String orcid, SendEmailFrequency frequency);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateSendQuarterlyTips(String orcid, boolean enabled);
}
