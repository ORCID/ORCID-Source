/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.EmailEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface EmailDao extends GenericDao<EmailEntity, String> {

    boolean emailExists(String email);

    EmailEntity findCaseInsensitive(String email);
    
    String findOrcidIdByCaseInsenitiveEmail(String email);

    void updateEmail(String orcid, String email, boolean isCurrent, Visibility visibility);

    void updatePrimary(String orcid, String primaryEmail);

    void addEmail(String orcid, String email, Visibility visibility, String sourceId, String clientSourceId);
    
    void addEmail(String orcid, String email, Visibility visibility, String sourceId, String clientSourceId, boolean isVerified, boolean isCurrent);

    void removeEmail(String orcid, String email);
    
    void removeEmail(String orcid, String email, boolean removeIfPrimary);
    
    @SuppressWarnings("rawtypes")
    List findIdByCaseInsensitiveEmail(List<String> emails);
    
    void addSourceToEmail(String sourceId, String email);
    
    boolean verifyEmail(String email);
    
    boolean isPrimaryEmailVerified(String orcid);
    
    boolean verifyPrimaryEmail(String orcid);
    
    boolean moveEmailToOtherAccountAsNonPrimary(String email, String origin, String destination);
    
    List<EmailEntity> findByOrcid(String orcid);
    
    List<EmailEntity> findByOrcid(String orcid, org.orcid.jaxb.model.common_rc3.Visibility visibility);
    
    boolean verifySetCurrentAndPrimary(String orcid, String email);
    
    /***
     * Indicates if the given email address could be auto deprecated given the
     * ORCID rules. See
     * https://trello.com/c/ouHyr0mp/3144-implement-new-auto-deprecate-workflow-
     * for-members-unclaimed-ids
     * 
     * @param email
     *            Email address
     * @return true if the email exists, the owner is not claimed and the
     *         client source of the record allows auto deprecating records
     */
    boolean isAutoDeprecateEnableForEmail(String email);
}
