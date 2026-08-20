package org.orcid.core.adapter.v3.impl.mapstruct;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV3;
import org.orcid.core.adapter.mapstruct.JSONWorkExternalIdentifiersMapperV3;
import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.mapstruct.WorkContributorsMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbWorkAdapter;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.MinimizedExtendedWorkEntity;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.WorkExtended;
import org.orcid.pojo.WorkSummaryExtended;

@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        VisibilityMapperV3.class,
        FuzzyDateMapperV3.class,
        JSONWorkExternalIdentifiersMapperV3.class,
        WorkContributorsMapperV3.class
    }
)
public abstract class JpaJaxbWorkAdapterImpl implements JpaJaxbWorkAdapter {

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "workTitle.title.content", target = "title")
    @Mapping(source = "workTitle.subtitle.content", target = "subtitle")
    @Mapping(source = "workTitle.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "workTitle.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "journalTitle.content", target = "journalTitle")
    @Mapping(source = "shortDescription", target = "description")
    @Mapping(source = "workCitation.citation", target = "citation")
    @Mapping(source = "workCitation.workCitationType", target = "citationType")
    @Mapping(source = "workType", target = "workType")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(source = "workExternalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "workContributors", target = "contributorsJson")
    @Mapping(source = "languageCode", target = "languageCode")
    @Mapping(source = "country.value", target = "country")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract WorkEntity toWorkEntity(Work work);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "workTitle.title.content", target = "title")
    @Mapping(source = "workTitle.subtitle.content", target = "subtitle")
    @Mapping(source = "workTitle.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "workTitle.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(source = "journalTitle.content", target = "journalTitle")
    @Mapping(source = "shortDescription", target = "description")
    @Mapping(source = "workCitation.citation", target = "citation")
    @Mapping(source = "workCitation.workCitationType", target = "citationType")
    @Mapping(source = "workType", target = "workType")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(source = "workExternalIdentifiers", target = "externalIdentifiersJson")
    @Mapping(source = "url.value", target = "url")
    @Mapping(source = "workContributors", target = "contributorsJson")
    @Mapping(source = "languageCode", target = "languageCode")
    @Mapping(source = "country.value", target = "country")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract WorkEntity toWorkEntity(Work work, @MappingTarget WorkEntity existing);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "workTitle.title.content")
    @Mapping(source = "subtitle", target = "workTitle.subtitle.content")
    @Mapping(source = "translatedTitle", target = "workTitle.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "workTitle.translatedTitle.languageCode")
    @Mapping(source = "journalTitle", target = "journalTitle.content")
    @Mapping(source = "description", target = "shortDescription")
    @Mapping(source = "citation", target = "workCitation.citation")
    @Mapping(source = "citationType", target = "workCitation.workCitationType")
    @Mapping(source = "workType", target = "workType")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(source = "externalIdentifiersJson", target = "workExternalIdentifiers")
    @Mapping(source = "url", target = "url.value")
    @Mapping(source = "contributorsJson", target = "workContributors")
    @Mapping(source = "languageCode", target = "languageCode")
    @Mapping(source = "country", target = "country.value")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract Work toWork(WorkEntity workEntity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "workTitle.title.content")
    @Mapping(source = "subtitle", target = "workTitle.subtitle.content")
    @Mapping(source = "translatedTitle", target = "workTitle.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "workTitle.translatedTitle.languageCode")
    @Mapping(source = "journalTitle", target = "journalTitle.content")
    @Mapping(source = "description", target = "shortDescription")
    @Mapping(source = "workType", target = "type")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "url", target = "url.value")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract WorkSummary toWorkSummary(WorkEntity workEntity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "workTitle.title.content")
    @Mapping(source = "subtitle", target = "workTitle.subtitle.content")
    @Mapping(source = "translatedTitle", target = "workTitle.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "workTitle.translatedTitle.languageCode")
    @Mapping(source = "journalTitle", target = "journalTitle.content")
    @Mapping(source = "description", target = "shortDescription")
    @Mapping(source = "workType", target = "type")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(source = "externalIdentifiersJson", target = "externalIdentifiers")
    @Mapping(source = "url", target = "url.value")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    public abstract WorkSummary toWorkSummary(MinimizedWorkEntity minimizedWorkEntity);

    @Override
    public abstract WorkExtended toWorkExtended(WorkEntity workEntity);

    @Override
    public abstract List<Work> toWork(Collection<WorkEntity> workEntities);

    @Override
    public abstract List<Work> toMinimizedWork(Collection<MinimizedWorkEntity> minimizedEntities);

    @Override
    public abstract List<WorkSummary> toWorkSummary(Collection<WorkEntity> workEntities);

    @Override
    public abstract List<WorkSummary> toWorkSummaryFromMinimized(Collection<MinimizedWorkEntity> workEntities);

    @Override
    public abstract List<WorkSummary> toWorkSummaryFromMinimized(Collection<MinimizedWorkEntity> workEntities, @Context Map<String, Source> sourceMap);

    @Override
    public abstract List<WorkSummaryExtended> toWorkSummaryExtendedFromMinimized(Collection<MinimizedExtendedWorkEntity> workEntities);
}