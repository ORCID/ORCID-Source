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

import org.orcid.jaxb.model.message.Keywords;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

public interface ProfileKeywordManager {
    public List<ProfileKeywordEntity> getProfileKeywors(String orcid);
    public boolean deleteProfileKeyword(String orcid, String keyword);
    public void addProfileKeyword(String orcid, String keyword);
    public void updateProfileKeyword(String orcid, Keywords keywords);
}
