package org.orcid.core.adapter.mapstruct.v3.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV3;
import org.orcid.core.adapter.mapstruct.JSONWorkExternalIdentifiersMapperV3;
import org.orcid.core.adapter.mapstruct.OrgMapperV3;
import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.UrlMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbResearchResourceAdapter;
import org.orcid.jaxb.model.v3.release.common.Organization;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.ResearchResource;
import org.orcid.jaxb.model.v3.release.record.ResearchResourceHosts;
import org.orcid.jaxb.model.v3.release.record.ResearchResourceItem;
import org.orcid.jaxb.model.v3.release.record.summary.ResearchResourceSummary;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.orcid.persistence.jpa.entities.ResearchResourceItemEntity;

/**
 * MapStruct implementation for JpaJaxbResearchResourceAdapter in V3.
 */
@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        VisibilityMapperV3.class,
        OrgMapperV3.class,
        FuzzyDateMapperV3.class,
        JSONWorkExternalIdentifiersMapperV3.class
        , UrlMapperV3.class
    }
)
public abstract class JpaJaxbResearchResourceAdapterImpl implements JpaJaxbResearchResourceAdapter {

    @Autowired
    protected OrgMapperV3 orgMapperV3;

    @Autowired
    protected JSONWorkExternalIdentifiersMapperV3 jsonWorkExternalIdentifiersMapperV3;

    // ========================================================================
    // API -> Database (Creation & Update)
    // ========================================================================

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "proposal.title.title.content", target = "title")
    @Mapping(source = "proposal.title.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "proposal.title.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "proposal.externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "proposal.url", target = "url")
    @Mapping(source = "proposal.startDate", target = "startDate")
    @Mapping(source = "proposal.endDate", target = "endDate")
    @Mapping(source = "proposal.hosts", target = "hosts")
    @Mapping(source = "resourceItems", target = "resourceItems")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract ResearchResourceEntity toEntity(ResearchResource researchResource);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "proposal.title.title.content", target = "title")
    @Mapping(source = "proposal.title.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "proposal.title.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "proposal.externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "proposal.url", target = "url")
    @Mapping(source = "proposal.startDate", target = "startDate")
    @Mapping(source = "proposal.endDate", target = "endDate")
    @Mapping(source = "proposal.hosts", target = "hosts")
    @Mapping(source = "resourceItems", target = "resourceItems")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract ResearchResourceEntity toEntity(ResearchResource researchResource, @MappingTarget ResearchResourceEntity existing);

    @AfterMapping
    protected void linkResourceItems(@MappingTarget ResearchResourceEntity entity) {
        if (entity.getResourceItems() != null) {
            for (ResearchResourceItemEntity item : entity.getResourceItems()) {
                item.setResearchResourceEntity(entity);
            }
        }
    }

    // ========================================================================
    // Database -> API (Retrieval)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "proposal.title.title.content")
    @Mapping(source = "translatedTitle", target = "proposal.title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "proposal.title.translatedTitle.languageCode")
    @Mapping(source = "externalIdentifiersJson", target = "proposal.externalIdentifiers")
    @Mapping(source = "url", target = "proposal.url")
    @Mapping(source = "startDate", target = "proposal.startDate")
    @Mapping(source = "endDate", target = "proposal.endDate")
    @Mapping(source = "hosts", target = "proposal.hosts")
    @Mapping(source = "resourceItems", target = "resourceItems")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract ResearchResource toModel(ResearchResourceEntity entity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "proposal.title.title.content")
    @Mapping(source = "translatedTitle", target = "proposal.title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "proposal.title.translatedTitle.languageCode")
    @Mapping(source = "externalIdentifiersJson", target = "proposal.externalIdentifiers")
    @Mapping(source = "url", target = "proposal.url")
    @Mapping(source = "startDate", target = "proposal.startDate")
    @Mapping(source = "endDate", target = "proposal.endDate")
    @Mapping(source = "hosts", target = "proposal.hosts")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract ResearchResourceSummary toSummary(ResearchResourceEntity entity);

    // ========================================================================
    // Custom Helper Mappings: Hosts (ResearchResourceHosts <-> List<OrgEntity>)
    // ========================================================================

    protected List<OrgEntity> mapHostsToEntities(ResearchResourceHosts hosts) {
        if (hosts == null || hosts.getOrganization() == null) {
            return null;
        }
        List<OrgEntity> orgEntities = new ArrayList<>();
        for (Organization org : hosts.getOrganization()) {
            OrgEntity entity = orgMapperV3.convertTo(org);
            if (entity != null) {
                orgEntities.add(entity);
            }
        }
        return orgEntities;
    }

    protected ResearchResourceHosts mapEntitiesToHosts(List<OrgEntity> hosts) {
        if (hosts == null) {
            return null;
        }
        ResearchResourceHosts resourceHosts = new ResearchResourceHosts();
        for (OrgEntity entity : hosts) {
            Organization org = orgMapperV3.convertFrom(entity);
            if (org != null) {
                resourceHosts.getOrganization().add(org);
            }
        }
        return resourceHosts;
    }

    // ========================================================================
    // Custom Helper Mappings: Resource Item
    // ========================================================================

    @Mapping(source = "externalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "url", target = "url")
    @Mapping(target = "researchResourceEntity", ignore = true)
    public abstract ResearchResourceItemEntity toItemEntity(ResearchResourceItem item);

    @Mapping(source = "resourceName", target = "resourceName")
    @Mapping(source = "resourceType", target = "resourceType")
    @Mapping(source = "hosts", target = "hosts")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    public abstract ResearchResourceItem toItem(ResearchResourceItemEntity entity);

    // ========================================================================
    // Collection Mappings
    // ========================================================================

    @Override
    public abstract List<ResearchResource> toModels(Collection<ResearchResourceEntity> entities);

    @Override
    public abstract List<ResearchResourceSummary> toSummaries(Collection<ResearchResourceEntity> entities);
}