package org.orcid.core.adapter.v3.impl.mapstruct;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV3;
import org.orcid.core.adapter.mapstruct.JSONWorkExternalIdentifiersMapperV3;
import org.orcid.core.adapter.mapstruct.OrgMapperV3;
import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbResearchResourceAdapter;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;


@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        VisibilityMapperV3.class,
        OrgMapperV3.class,
        FuzzyDateMapperV3.class,
        JSONWorkExternalIdentifiersMapperV3.class
    }
)
public abstract class JpaJaxbResearchResourceAdapterImpl implements JpaJaxbResearchResourceAdapter {

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "proposal.title.title.content", target = "title")
    @Mapping(source = "proposal.title.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "proposal.title.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "proposal.externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "proposal.url.value", target = "url")
    @Mapping(source = "proposal.startDate", target = "startDate")
    @Mapping(source = "proposal.endDate", target = "endDate")
    @Mapping(source = "proposal.hosts.organization", target = "hosts")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract ResearchResourceEntity toEntity(ResearchResource researchResource);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "proposal.title.title.content", target = "title")
    @Mapping(source = "proposal.title.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "proposal.title.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "proposal.externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "proposal.url.value", target = "url")
    @Mapping(source = "proposal.startDate", target = "startDate")
    @Mapping(source = "proposal.endDate", target = "endDate")
    @Mapping(source = "proposal.hosts.organization", target = "hosts")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract ResearchResourceEntity toEntity(ResearchResource researchResource, @MappingTarget ResearchResourceEntity existing);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "proposal.title.title.content")
    @Mapping(source = "translatedTitle", target = "proposal.title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "proposal.title.translatedTitle.languageCode")
    @Mapping(source = "externalIdentifiersJson", target = "proposal.externalIdentifiers")
    @Mapping(source = "url", target = "proposal.url.value")
    @Mapping(source = "startDate", target = "proposal.startDate")
    @Mapping(source = "endDate", target = "proposal.endDate")
    @Mapping(source = "hosts", target = "proposal.hosts.organization")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract ResearchResource toModel(ResearchResourceEntity entity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "proposal.title.title.content")
    @Mapping(source = "translatedTitle", target = "proposal.title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "proposal.title.translatedTitle.languageCode")
    @Mapping(source = "externalIdentifiersJson", target = "proposal.externalIdentifiers")
    @Mapping(source = "url", target = "proposal.url.value")
    @Mapping(source = "startDate", target = "proposal.startDate")
    @Mapping(source = "endDate", target = "proposal.endDate")
    @Mapping(source = "hosts", target = "proposal.hosts.organization")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract ResearchResourceSummary toSummary(ResearchResourceEntity entity);

    @Override
    public abstract List<ResearchResource> toModels(Collection<ResearchResourceEntity> entities);

    @Override
    public abstract List<ResearchResourceSummary> toSummaries(Collection<ResearchResourceEntity> entities);
}