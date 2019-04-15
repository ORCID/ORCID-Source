package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.v3.JpaJaxbKeywordAdapter;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
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
