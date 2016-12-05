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
package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.JpaJaxbKeywordAdapter;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

import ma.glasnost.orika.MapperFacade;

public class JpaJaxbKeywordAdapterImpl implements JpaJaxbKeywordAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword) {
        if (keyword == null) {
            return null;
        }
        ProfileKeywordEntity result =   mapperFacade.map(keyword, ProfileKeywordEntity.class);
        if(result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }
        
        return result;
    }

    @Override
    public Keyword toKeyword(ProfileKeywordEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapperFacade.map(entity, Keyword.class);
    }

    @Override
    public Keywords toKeywords(Collection<ProfileKeywordEntity> entities) {
        if (entities == null) {
            return null;
        }
        List<Keyword> keywordList = mapperFacade.mapAsList(entities, Keyword.class);
        Keywords keywords = new Keywords();
        keywords.setKeywords(keywordList);
        return keywords;
    }

    @Override
    public ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword, ProfileKeywordEntity existing) {
        if (keyword == null) {
            return null;
        }
        mapperFacade.map(keyword, existing);
        return existing;
    }

}
