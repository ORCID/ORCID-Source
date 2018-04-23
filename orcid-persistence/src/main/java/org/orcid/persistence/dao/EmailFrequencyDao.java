package org.orcid.persistence.dao;

import org.orcid.persistence.constants.SendEmailFrequency;
import org.orcid.persistence.jpa.entities.EmailFrequencyEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface EmailFrequencyDao extends GenericDao<EmailFrequencyEntity, String> {
    EmailFrequencyEntity findByOrcid(String orcid);
    
    boolean updateSendChangeNotifications(String orcid, SendEmailFrequency frequency);
    
    boolean updateSendAdministrativeChangeNotifications(String orcid, SendEmailFrequency frequency);
    
    boolean updateSendMemberUpdateRequests(String orcid, SendEmailFrequency frequency);
    
    boolean updateSendQuarterlyTips(String orcid, boolean enabled);
}
