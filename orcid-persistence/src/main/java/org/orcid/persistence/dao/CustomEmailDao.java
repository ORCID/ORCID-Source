package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;
import org.orcid.persistence.jpa.entities.keys.CustomEmailPk;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public interface CustomEmailDao extends GenericDao<CustomEmailEntity, CustomEmailPk> {
    
    /**
     * Get a list of all custom emails created by a specific client
     * @param clientDetailsId
     * @return a list containing all custom emails associated with a client
     * */
    List<CustomEmailEntity> getCustomEmails(String clientDetailsId);
    
    /**
     * Finds a custom email given his client id and the email type
     * @param clientDetailsId
     * @param emailType
     * @return a CustomEmailEntity object if the email is found, null otherwise
     * */
    CustomEmailEntity findByClientIdAndEmailType(String clientDetailsId, EmailType emailType);
    
    /**
     * Creates a custom email on database
     * @param clientDetailsId
     * @param emailType
     * @param sender
     * @param subject
     * @param content
     * @return true if it was able to create the custom email      
     * */
    boolean createCustomEmail(String clientDetailsId, EmailType emailType, String sender, String subject, String content);
    
    /**
     * Updated an existing custom email
     * @param clientDetailsId
     * @param emailType
     * @param sender
     * @param subject
     * @param content
     * @return true if it was able to update the custom email
     * */
    boolean updateCustomEmail(String clientDetailsId, EmailType emailType, String sender, String subject, String content);
    
    /**
     * Deletes a custom email
     * @param clientDetailsId
     * @param emailType 
     * @return true if it was able to delete the custom email
     * */
    boolean deleteCustomEmail(String clientDetailsId, EmailType emailType);
    
    
    /**
     * Checks if a custom email exists
     * @param clientDetailsId
     * @param emailType
     * @return true if a custom email with id=clientDetailsId and email type=emailType exists
     * */
    boolean exists(String clientDetailsId, EmailType emailType);
}
