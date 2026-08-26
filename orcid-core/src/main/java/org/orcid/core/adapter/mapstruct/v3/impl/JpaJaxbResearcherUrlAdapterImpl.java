package org.orcid.core.adapter.mapstruct.v3.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbResearcherUrlAdapter;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        VisibilityMapperV3.class
    }
)
public abstract class JpaJaxbResearcherUrlAdapterImpl implements JpaJaxbResearcherUrlAdapter {

    @Override
    public ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl) {
        if (researcherUrl == null) {
            return null;
        }

        ResearcherUrlEntity result = mapToEntity(researcherUrl);

        if (result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }

        return result;
    }

    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "urlName", target = "urlName")
    @Mapping(source = "url.value", target = "url")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    protected abstract ResearcherUrlEntity mapToEntity(ResearcherUrl researcherUrl);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "urlName", target = "urlName")
    @Mapping(source = "url.value", target = "url")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "displayIndex", ignore = true)
    public abstract ResearcherUrlEntity toResearcherUrlEntity(ResearcherUrl researcherUrl, @MappingTarget ResearcherUrlEntity existing);

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

        List<ResearcherUrl> list = toResearcherUrlListInternal(entities);
        ResearcherUrls researcherUrls = new ResearcherUrls();
        researcherUrls.setResearcherUrls(list);
        return researcherUrls;
    }

    protected abstract List<ResearcherUrl> toResearcherUrlListInternal(Collection<ResearcherUrlEntity> entities);
}