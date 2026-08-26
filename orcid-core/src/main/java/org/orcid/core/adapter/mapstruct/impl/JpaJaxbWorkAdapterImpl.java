package org.orcid.core.adapter.mapstruct.impl;

import java.util.Collection;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.JpaJaxbWorkAdapter;
import org.orcid.core.adapter.mapstruct.JSONWorkExternalIdentifiersMapperV2;
import org.orcid.core.adapter.mapstruct.SourceMapperV2;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV2;
import org.orcid.core.adapter.mapstruct.WorkContributorsMapperV2;
import org.orcid.core.adapter.mapstruct.WorkMapperV2;
import org.orcid.jaxb.model.common_v2.PublicationDate;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

/**
 * MapStruct automatically generates the implementation and registers it as a Spring Component.
 */
@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV2.class,
        VisibilityMapperV2.class,
        JSONWorkExternalIdentifiersMapperV2.class,
        WorkContributorsMapperV2.class
    }
)
public abstract class JpaJaxbWorkAdapterImpl implements JpaJaxbWorkAdapter {

    @Autowired
    protected WorkMapperV2 workMapperV2;

    // Work <-> WorkEntity
    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "journalTitle.content", target = "journalTitle")
    @Mapping(source = "workTitle.title.content", target = "title")
    @Mapping(source = "workTitle.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "workTitle.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "workTitle.subtitle.content", target = "subtitle")
    @Mapping(source = "shortDescription", target = "description")
    @Mapping(source = "workCitation.workCitationType", target = "citationType")
    @Mapping(source = "workCitation.citation", target = "citation")
    @Mapping(source = "workExternalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "url.value", target = "workUrl")
    @Mapping(source = "workContributors", target = "contributorsJson")
    @Mapping(source = "languageCode", target = "languageCode")
    @Mapping(source = "country.value", target = "iso2Country")
    // Nested Publication Date relies on internal protected methods below
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "workType", ignore = true) // Handled by WorkMapperV2
    public abstract WorkEntity toWorkEntity(Work work);

    @AfterMapping
    protected void afterToWorkEntity(Work work, @MappingTarget WorkEntity entity) {
        workMapperV2.mapWorkAtoB(work, entity);
    }

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "journalTitle.content", target = "journalTitle")
    @Mapping(source = "workTitle.title.content", target = "title")
    @Mapping(source = "workTitle.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "workTitle.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "workTitle.subtitle.content", target = "subtitle")
    @Mapping(source = "shortDescription", target = "description")
    @Mapping(source = "workCitation.workCitationType", target = "citationType")
    @Mapping(source = "workCitation.citation", target = "citation")
    @Mapping(source = "workExternalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "url.value", target = "workUrl")
    @Mapping(source = "workContributors", target = "contributorsJson")
    @Mapping(source = "languageCode", target = "languageCode")
    @Mapping(source = "country.value", target = "iso2Country")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    @Mapping(target = "workType", ignore = true)
    public abstract WorkEntity toWorkEntity(Work work, @MappingTarget WorkEntity existing);

    @AfterMapping
    protected void afterToWorkEntityUpdate(Work work, @MappingTarget WorkEntity entity) {
        workMapperV2.mapWorkAtoB(work, entity);
    }

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "journalTitle", target = "journalTitle.content")
    @Mapping(source = "title", target = "workTitle.title.content")
    @Mapping(source = "translatedTitle", target = "workTitle.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "workTitle.translatedTitle.languageCode")
    @Mapping(source = "subtitle", target = "workTitle.subtitle.content")
    @Mapping(source = "description", target = "shortDescription")
    @Mapping(source = "citationType", target = "workCitation.workCitationType")
    @Mapping(source = "citation", target = "workCitation.citation")
    @Mapping(source = "externalIdentifiersJson", target = "workExternalIdentifiers")
    @Mapping(source = "workUrl", target = "url.value")
    @Mapping(source = "contributorsJson", target = "workContributors")
    @Mapping(source = "languageCode", target = "languageCode")
    @Mapping(source = "iso2Country", target = "country.value")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(source = ".", target = "source")
    @Mapping(target = "workType", ignore = true) // Handled by WorkMapperV2
    public abstract Work toWork(WorkEntity workEntity);

    @AfterMapping
    protected void afterToWork(WorkEntity entity, @MappingTarget Work work) {
        workMapperV2.mapWorkBtoA(entity, work);
    }

    // WorkEntity -> WorkSummary
    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "title", target = "title.title.content")
    @Mapping(source = "translatedTitle", target = "title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "title.translatedTitle.languageCode")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = ".", target = "source")
    @Mapping(target = "type", ignore = true) // Handled by WorkMapperV2
    public abstract WorkSummary toWorkSummary(WorkEntity workEntity);

    @AfterMapping
    protected void afterToWorkSummary(WorkEntity entity, @MappingTarget WorkSummary summary) {
        workMapperV2.mapWorkSummaryBtoA(entity, summary);
    }

    // MinimizedWorkEntity -> Work
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "journalTitle", target = "journalTitle.content")
    @Mapping(source = "title", target = "workTitle.title.content")
    @Mapping(source = "translatedTitle", target = "workTitle.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "workTitle.translatedTitle.languageCode")
    @Mapping(source = "subtitle", target = "workTitle.subtitle.content")
    @Mapping(source = "description", target = "shortDescription")
    @Mapping(source = "publicationYear", target = "publicationDate.year.value")
    @Mapping(source = "publicationMonth", target = "publicationDate.month.value")
    @Mapping(source = "publicationDay", target = "publicationDate.day.value")
    @Mapping(source = "externalIdentifiersJson", target = "workExternalIdentifiers")
    @Mapping(source = "workUrl", target = "url.value")
    @Mapping(source = ".", target = "source")
    @Mapping(target = "workType", ignore = true) // Handled by WorkMapperV2
    protected abstract Work toWorkFromMinimized(MinimizedWorkEntity entity);

    @AfterMapping
    protected void afterToWorkFromMinimized(MinimizedWorkEntity entity, @MappingTarget Work work) {
        workMapperV2.mapMinimizedWorkBtoA(entity, work);
    }

    // MinimizedWorkEntity -> WorkSummary
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = "title", target = "title.title.content")
    @Mapping(source = "translatedTitle", target = "title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "title.translatedTitle.languageCode")
    @Mapping(source = "publicationYear", target = "publicationDate.year.value")
    @Mapping(source = "publicationMonth", target = "publicationDate.month.value")
    @Mapping(source = "publicationDay", target = "publicationDate.day.value")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = ".", target = "source")
    @Mapping(target = "type", ignore = true) // Handled by WorkMapperV2
    protected abstract WorkSummary toWorkSummaryFromMinimized(MinimizedWorkEntity entity);

    @AfterMapping
    protected void afterToWorkSummaryFromMinimized(MinimizedWorkEntity entity, @MappingTarget WorkSummary summary) {
        workMapperV2.mapWorkSummaryToMinimizedBtoA(entity, summary);
    }

    // Collection Mappings
    @Override
    public abstract List<Work> toWork(Collection<WorkEntity> workEntities);

    @Override
    public abstract List<Work> toMinimizedWork(Collection<MinimizedWorkEntity> minimizedEntities);

    @Override
    public abstract List<WorkSummary> toWorkSummary(Collection<WorkEntity> workEntities);

    @Override
    public abstract List<WorkSummary> toWorkSummaryFromMinimized(Collection<MinimizedWorkEntity> workEntities);


    // Nested Entity Converters
    @Mapping(source = "year.value", target = "year")
    @Mapping(source = "month.value", target = "month")
    @Mapping(source = "day.value", target = "day")
    protected abstract PublicationDateEntity mapPublicationDate(PublicationDate date);

    @Mapping(source = "year", target = "year.value")
    @Mapping(source = "month", target = "month.value")
    @Mapping(source = "day", target = "day.value")
    protected abstract PublicationDate mapPublicationDate(PublicationDateEntity entity);

}