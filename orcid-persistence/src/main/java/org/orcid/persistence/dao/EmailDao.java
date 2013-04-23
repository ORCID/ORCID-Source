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
package org.orcid.persistence.dao;

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

    void updateEmail(String orcid, String email, boolean isCurrent, Visibility visibility);

    void updatePrimary(String orcid, String primaryEmail);

    void addEmail(String orcid, String email, Visibility visibility, String sourceOrcid);

    void removeEmail(String orcid, String email);

}
