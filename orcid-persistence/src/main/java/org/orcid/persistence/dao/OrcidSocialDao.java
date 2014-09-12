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

import org.orcid.persistence.jpa.entities.OrcidSocialEntity;
import org.orcid.persistence.jpa.entities.OrcidSocialType;

public interface OrcidSocialDao {

    void save(String orcid, OrcidSocialType type, String encryptedCredentials);

    void delete(String orcid, OrcidSocialType type);

    boolean isEnabled(String orcid, OrcidSocialType type);

    boolean updateLatestRunDate(String orcid, OrcidSocialType type);

    List<OrcidSocialEntity> getRecordsToTweet();
}
