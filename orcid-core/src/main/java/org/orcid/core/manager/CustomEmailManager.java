/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager;

import java.util.List;

import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;

public interface CustomEmailManager {

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
    CustomEmailEntity getCustomEmail(String clientDetailsId, EmailType emailType);
    
    /**
     * Creates a custom email on database
     * @param clientDetailsId
     * @param emailType
     * @param sender
     * @param subject
     * @param content
     * @param isHtml
     * @return true if it was able to create the custom email      
     * */
    boolean createCustomEmail(String clientDetailsId, EmailType emailType, String sender, String subject, String content, boolean isHtml);
    
    /**
     * Updated an existing custom email
     * @param clientDetailsId
     * @param emailType
     * @param sender
     * @param subject
     * @param content
     * @param isHtml
     * @return true if it was able to update the custom email
     * */
    boolean updateCustomEmail(String clientDetailsId, EmailType emailType, String sender, String subject, String content, boolean isHtml);
    
    /**
     * Deletes a custom email
     * @param clientDetailsId
     * @param emailType 
     * @return true if it was able to delete the custom email
     * */
    boolean deleteCustomEmail(String clientDetailsId, EmailType emailType);

}
