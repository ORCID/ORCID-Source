package org.orcid.core.adapter.impl.mapstruct;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbKeywordAdapter;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV2;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;


@Mapper(
    componentModel = "spring", 
    uses = {SourceMapperV2.class, VisibilityMapperV2.class}
)
public abstract class JpaJaxbKeywordAdapterImpl implements JpaJaxbKeywordAdapter {


    @Override
    public ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword) {
        if (keyword == null) {
            return null;
        }
        
        ProfileKeywordEntity result = mapToEntity(keyword);
        
        // Preserve original logic: default display index to 0 for new entities
        if (result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }
        
        return result;
    }

    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "content", target = "keywordName")
    // Orika mapped these using fieldBToA (Database -> API only)
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    protected abstract ProfileKeywordEntity mapToEntity(Keyword keyword);


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
        
        // Let MapStruct generate the loop
        List<Keyword> keywordList = toKeywordListInternal(entities);
        
        // Wrap the list in the JAXB Keywords object
        Keywords keywords = new Keywords();
        keywords.setKeywords(keywordList);
        return keywords;
    }

    protected abstract List<Keyword> toKeywordListInternal(Collection<ProfileKeywordEntity> entities);


    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "content", target = "keywordName")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    public abstract ProfileKeywordEntity toProfileKeywordEntity(Keyword keyword, @MappingTarget ProfileKeywordEntity existing);
}