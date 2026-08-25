package org.orcid.core.adapter.mapstruct.v3.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbKeywordAdapter;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

/**
 * MapStruct automatically generates the implementation and registers it as a Spring Component.
 */
@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        VisibilityMapperV3.class
    }
)
public abstract class JpaJaxbKeywordAdapterImpl implements JpaJaxbKeywordAdapter {

    @Override
    public ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword) {
        if (keyword == null) {
            return null;
        }

        ProfileKeywordEntity result = mapToEntity(keyword);

        if (result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }

        return result;
    }

    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "content", target = "keywordName")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    protected abstract ProfileKeywordEntity mapToEntity(Keyword keyword);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "content", target = "keywordName")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    public abstract ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword, @MappingTarget ProfileKeywordEntity existing);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "keywordName", target = "content")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract Keyword toKeyword(ProfileKeywordEntity entity);

    @Override
    public Keywords toKeywords(Collection<ProfileKeywordEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<Keyword> list = toKeywordListInternal(entities);
        Keywords keywords = new Keywords();
        keywords.setKeywords(list);
        return keywords;
    }

    protected abstract List<Keyword> toKeywordListInternal(Collection<ProfileKeywordEntity> entities);
}