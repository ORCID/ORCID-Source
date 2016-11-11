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
package org.orcid.core.manager.read_only;

import java.util.Collection;
import java.util.Map;

import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.record_rc3.Emails;
import org.orcid.persistence.jpa.entities.EmailEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface EmailManagerReadOnly {

    boolean emailExists(String email);

    void updateEmails(String orcid, Collection<Email> emails);

    void addEmail(String orcid, Email email);
    
    void addEmail(String orcid, EmailEntity email);

    void removeEmail(String orcid, String email);

    void removeEmail(String orcid, String email, boolean removeIfPrimary);
    
    Map<String, String> findIdByEmail(String email);
    
    void addSourceToEmail(String email, String sourceId);
    
    boolean verifyEmail(String email);
    
    boolean isPrimaryEmailVerified(String orcid);
    
    boolean verifyPrimaryEmail(String orcid);
    
    boolean moveEmailToOtherAccount(String email, String origin, String destination);
    
    Emails getEmails(String orcid, long lastModified);
    
    Emails getPublicEmails(String orcid, long lastModified);
    
    boolean haveAnyEmailVerified(String orcid);
    
    org.orcid.pojo.ajaxForm.Emails getEmailsAsForm(String orcid);
    
    boolean verifySetCurrentAndPrimary(String orcid, String email);
}
