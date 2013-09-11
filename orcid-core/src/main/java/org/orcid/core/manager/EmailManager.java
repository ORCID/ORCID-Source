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

import java.util.Collection;

import org.orcid.jaxb.model.message.Email;
import org.orcid.persistence.jpa.entities.EmailEntity;

/**
 * 
 * @author Will Simpson
 *
 */
public interface EmailManager {

    boolean emailExists(String email);

    void updateEmails(String orcid, Collection<Email> emails);

    void addEmail(String orcid, Email email);
    
    void addEmail(String orcid, EmailEntity email);

    void removeEmail(String orcid, String email);

    void removeEmail(String orcid, String email, boolean removeIfPrimary);
}
