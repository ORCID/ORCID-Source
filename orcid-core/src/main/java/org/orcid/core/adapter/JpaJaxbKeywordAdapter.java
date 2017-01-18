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
package org.orcid.core.adapter;

import java.util.Collection;

import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

public interface JpaJaxbKeywordAdapter {

    ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword);

    Keyword toKeyword(ProfileKeywordEntity entity);

    Keywords toKeywords(Collection<ProfileKeywordEntity> entities);        

    ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword, ProfileKeywordEntity existing);
}
