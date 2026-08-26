package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.JpaJaxbResearcherUrlAdapter;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV2;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

@Mapper(
    componentModel = "spring", 
    uses = {SourceMapperV2.class, VisibilityMapperV2.class}
)
public abstract class JpaJaxbResearcherUrlAdapterImpl implements JpaJaxbResearcherUrlAdapter {

    @Override
    public ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl) {
        if (researcherUrl == null) {
            return null;
        }

        ResearcherUrlEntity result = mapToEntity(researcherUrl);

        // Default display index to 0 for new entities
        if (result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }

        return result;
    }

    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "urlName", target = "urlName")
    @Mapping(source = "url.value", target = "url")
    // Security & Auditing ignore guards
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    protected abstract ResearcherUrlEntity mapToEntity(ResearcherUrl researcherUrl);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "urlName", target = "urlName")
    @Mapping(source = "url", target = "url.value")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract ResearcherUrl toResearcherUrl(ResearcherUrlEntity entity);

    @Override
    public ResearcherUrls toResearcherUrlList(Collection<ResearcherUrlEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<ResearcherUrl> researchUrlList = toResearcherUrlListInternal(entities);

        ResearcherUrls researchUrls = new ResearcherUrls();
        researchUrls.setResearcherUrls(researchUrlList);
        return researchUrls;
    }

    protected abstract List<ResearcherUrl> toResearcherUrlListInternal(Collection<ResearcherUrlEntity> entities);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "urlName", target = "urlName")
    @Mapping(source = "url.value", target = "url")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    public abstract ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl, @MappingTarget ResearcherUrlEntity existing);
}