package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;

public interface CustomEmailManager {

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
     * @param subject
     * @param content
     * @return true if it was able to create the custom email      
     * */
    boolean createCustomEmail(String clientDetailsId, EmailType emailType, String subject, String content);
    
    /**
     * Updated an existing custom email
     * @param clientDetailsId
     * @param emailType
     * @param subject
     * @param content
     * @return true if it was able to update the custom email
     * */
    boolean updateCustomEmail(String clientDetailsId, EmailType emailType, String subject, String content);
    
    /**
     * Deletes a custom email
     * @param clientDetailsId
     * @param emailType 
     * @return true if it was able to delete the custom email
     * */
    boolean deleteCustomEmail(String clientDetailsId, EmailType emailType);

}
