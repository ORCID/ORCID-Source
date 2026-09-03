package org.orcid.core.adapter.mapstruct.v3.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.mapstruct.ContributorsRolesAndSequencesMapperV3;
import org.orcid.core.adapter.mapstruct.FuzzyDateMapperV3;
import org.orcid.core.adapter.mapstruct.JSONWorkExternalIdentifiersMapperV3;
import org.orcid.core.adapter.mapstruct.SourceMapperV3;
import org.orcid.core.adapter.mapstruct.VisibilityMapperV3;
import org.orcid.core.adapter.mapstruct.WorkContributorsMapperV3;
import org.orcid.core.adapter.v3.JpaJaxbWorkAdapter;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.Day;
import org.orcid.jaxb.model.v3.release.common.Month;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Subtitle;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.MinimizedExtendedWorkEntity;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.WorkExtended;
import org.orcid.pojo.WorkSummaryExtended;

@Mapper(
    componentModel = "spring",
    uses = {
        SourceMapperV3.class,
        VisibilityMapperV3.class,
        FuzzyDateMapperV3.class
    }
)
public abstract class JpaJaxbWorkAdapterImpl implements JpaJaxbWorkAdapter {

    @Autowired
    protected JSONWorkExternalIdentifiersMapperV3 extIdMapper;
    
    @Autowired
    protected WorkContributorsMapperV3 contributorsMapper;
    
    @Autowired
    protected ContributorsRolesAndSequencesMapperV3 contributorsRolesMapper;

    // ========================================================================
    // Safe Explicit Conversion Helpers
    // ========================================================================

    protected WorkType mapStringToWorkType(String type) {
        if (type == null) return null;
        try {
            return WorkType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    protected String mapWorkTypeToString(WorkType type) {
        return type == null ? null : type.name();
    }

    protected Title mapStringToTitle(String title) {
        if (title == null) return null;
        Title t = new Title();
        t.setContent(title);
        return t;
    }

    protected String mapTitleToString(Title title) {
        return title == null ? null : title.getContent();
    }

    protected String map(Subtitle subtitle) {
        return subtitle == null || StringUtils.isBlank(subtitle.getContent()) ? null : subtitle.getContent().trim();
    }

    protected Subtitle mapSubtitle(String subtitle) {
        if (StringUtils.isBlank(subtitle)) {
            return null;
        }
        Subtitle result = new Subtitle();
        result.setContent(subtitle.trim());
        return result;
    }

    protected Url mapStringToUrl(String url) {
        return StringUtils.isBlank(url) ? null : new Url(url.trim());
    }

    protected String mapUrlToString(Url url) {
        return url == null || StringUtils.isBlank(url.getValue()) ? null : url.getValue().trim();
    }

    protected org.orcid.jaxb.model.v3.release.common.Country mapStringToCountry(String country) {
        if (country == null) return null;
        try {
            return new org.orcid.jaxb.model.v3.release.common.Country(org.orcid.jaxb.model.common.Iso3166Country.valueOf(country));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    protected String mapCountryToString(org.orcid.jaxb.model.v3.release.common.Country country) {
        return (country == null || country.getValue() == null) ? null : country.getValue().name();
    }

    protected PublicationDate mapPublicationDate(PublicationDateEntity entity) {
        if (entity == null) {
            return null;
        }
        PublicationDate result = new PublicationDate();
        if (entity.getYear() != null) {
            result.setYear(new Year(entity.getYear()));
        }
        if (entity.getMonth() != null) {
            result.setMonth(new Month(entity.getMonth()));
        }
        if (entity.getDay() != null) {
            result.setDay(new Day(entity.getDay()));
        }
        return result;
    }

    // ========================================================================
    // API -> Database (Creation & Update)
    // ========================================================================

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "workTitle.title.content", target = "title")
    @Mapping(source = "workTitle.subtitle", target = "subtitle")
    @Mapping(source = "workTitle.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "workTitle.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(target = "journalTitle", expression = "java( mapTitleToString(work.getJournalTitle()) )")
    @Mapping(source = "shortDescription", target = "description")
    @Mapping(source = "workCitation.citation", target = "citation")
    @Mapping(source = "workCitation.workCitationType", target = "citationType")
    @Mapping(target = "workType", expression = "java( mapWorkTypeToString(work.getWorkType()) )")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(target = "externalIdentifiersJson", expression = "java( extIdMapper.convertTo(work.getWorkExternalIdentifiers()) )")
    @Mapping(target = "workUrl", expression = "java( mapUrlToString(work.getUrl()) )")
    @Mapping(target = "contributorsJson", expression = "java( contributorsMapper.convertTo(work.getWorkContributors()) )")
    @Mapping(source = "languageCode", target = "languageCode")
    @Mapping(target = "iso2Country", expression = "java( mapCountryToString(work.getCountry()) )")
    @Mapping(source = "visibility", target = "visibility")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract WorkEntity toWorkEntity(Work work);

    @Override
    @Mapping(source = "putCode", target = "id")
    @Mapping(source = "workTitle.title.content", target = "title")
    @Mapping(source = "workTitle.subtitle", target = "subtitle")
    @Mapping(source = "workTitle.translatedTitle.content", target = "translatedTitle")
    @Mapping(source = "workTitle.translatedTitle.languageCode", target = "translatedTitleLanguageCode")
    @Mapping(target = "journalTitle", expression = "java( mapTitleToString(work.getJournalTitle()) )")
    @Mapping(source = "shortDescription", target = "description")
    @Mapping(source = "workCitation.citation", target = "citation")
    @Mapping(source = "workCitation.workCitationType", target = "citationType")
    @Mapping(target = "workType", expression = "java( mapWorkTypeToString(work.getWorkType()) )")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(target = "externalIdentifiersJson", expression = "java( extIdMapper.convertTo(work.getWorkExternalIdentifiers()) )")
    @Mapping(target = "workUrl", expression = "java( mapUrlToString(work.getUrl()) )")
    @Mapping(target = "contributorsJson", expression = "java( contributorsMapper.convertTo(work.getWorkContributors()) )")
    @Mapping(source = "languageCode", target = "languageCode")
    @Mapping(target = "iso2Country", expression = "java( mapCountryToString(work.getCountry()) )")
    @Mapping(source = "visibility", target = "visibility")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    public abstract WorkEntity toWorkEntity(Work work, @MappingTarget WorkEntity existing);

    // ========================================================================
    // Database -> API (Retrieval)
    // ========================================================================

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "workTitle.title.content")
    @Mapping(source = "subtitle", target = "workTitle.subtitle")
    @Mapping(source = "translatedTitle", target = "workTitle.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "workTitle.translatedTitle.languageCode")
    @Mapping(target = "journalTitle", expression = "java( mapStringToTitle(workEntity.getJournalTitle()) )")
    @Mapping(source = "description", target = "shortDescription")
    @Mapping(source = "citation", target = "workCitation.citation")
    @Mapping(source = "citationType", target = "workCitation.workCitationType")
    @Mapping(target = "workType", expression = "java( mapStringToWorkType(workEntity.getWorkType()) )")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(target = "workExternalIdentifiers", expression = "java( extIdMapper.convertFrom(workEntity.getExternalIdentifiersJson()) )")
    @Mapping(target = "url", expression = "java( mapStringToUrl(workEntity.getWorkUrl()) )")
    @Mapping(target = "workContributors", expression = "java( contributorsMapper.convertFrom(workEntity.getContributorsJson()) )")
    @Mapping(source = "languageCode", target = "languageCode")
    @Mapping(target = "country", expression = "java( mapStringToCountry(workEntity.getIso2Country()) )")
    @Mapping(source = "visibility", target = "visibility")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract Work toWork(WorkEntity workEntity);

    @AfterMapping
    protected void removeEmptyCitation(@MappingTarget Work work) {
        removeEmptyOptionalTitleFields(work);
        if (work.getWorkCitation() != null
                && work.getWorkCitation().getCitation() == null
                && work.getWorkCitation().getWorkCitationType() == null) {
            work.setWorkCitation(null);
        }
    }

    @AfterMapping
    protected void removeEmptyCitation(@MappingTarget WorkExtended work) {
        removeEmptyOptionalTitleFields(work);
        if (work.getWorkCitation() != null
                && work.getWorkCitation().getCitation() == null
                && work.getWorkCitation().getWorkCitationType() == null) {
            work.setWorkCitation(null);
        }
    }

    private void removeEmptyOptionalTitleFields(Work work) {
        if (work.getWorkTitle() == null) {
            return;
        }
        if (work.getWorkTitle().getSubtitle() != null && work.getWorkTitle().getSubtitle().getContent() == null) {
            work.getWorkTitle().setSubtitle(null);
        }
        if (work.getWorkTitle().getTranslatedTitle() != null
                && work.getWorkTitle().getTranslatedTitle().getContent() == null
                && work.getWorkTitle().getTranslatedTitle().getLanguageCode() == null) {
            work.getWorkTitle().setTranslatedTitle(null);
        }
    }

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "workTitle.title.content")
    @Mapping(source = "subtitle", target = "workTitle.subtitle")
    @Mapping(source = "translatedTitle", target = "workTitle.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "workTitle.translatedTitle.languageCode")
    @Mapping(target = "journalTitle", expression = "java( mapStringToTitle(workEntity.getJournalTitle()) )")
    @Mapping(source = "description", target = "shortDescription")
    @Mapping(source = "citation", target = "workCitation.citation")
    @Mapping(source = "citationType", target = "workCitation.workCitationType")
    @Mapping(target = "workType", expression = "java( mapStringToWorkType(workEntity.getWorkType()) )")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(target = "workExternalIdentifiers", expression = "java( extIdMapper.convertFrom(workEntity.getExternalIdentifiersJson()) )")
    @Mapping(target = "url", expression = "java( mapStringToUrl(workEntity.getWorkUrl()) )")
    @Mapping(target = "workContributors", ignore = true) // CRITICAL: Explicitly ignored to match Orika and pass equality checks![cite: 9]
    @Mapping(source = "languageCode", target = "languageCode")
    @Mapping(target = "country", expression = "java( mapStringToCountry(workEntity.getIso2Country()) )")
    @Mapping(source = "visibility", target = "visibility")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    @Mapping(target = "contributorsGroupedByOrcid", expression = "java( workEntity.getTopContributorsJson() != null && !workEntity.getTopContributorsJson().isEmpty() && contributorsRolesMapper != null ? contributorsRolesMapper.getContributorsRolesAndSequencesList(workEntity.getTopContributorsJson()) : null )")
    public abstract WorkExtended toWorkExtended(WorkEntity workEntity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "title.title.content")
    @Mapping(source = "subtitle", target = "title.subtitle")
    @Mapping(source = "translatedTitle", target = "title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "title.translatedTitle.languageCode")
    @Mapping(target = "journalTitle", expression = "java( mapStringToTitle(workEntity.getJournalTitle()) )")
    @Mapping(target = "type", expression = "java( mapStringToWorkType(workEntity.getWorkType()) )")
    @Mapping(source = "publicationDate", target = "publicationDate")
    @Mapping(target = "externalIdentifiers", expression = "java( extIdMapper.convertFrom(workEntity.getExternalIdentifiersJson()) )")
    @Mapping(target = "url", expression = "java( mapStringToUrl(workEntity.getWorkUrl()) )")
    @Mapping(source = "visibility", target = "visibility")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract WorkSummary toWorkSummary(WorkEntity workEntity);

    @Override
    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "title.title.content")
    @Mapping(source = "subtitle", target = "title.subtitle")
    @Mapping(source = "translatedTitle", target = "title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "title.translatedTitle.languageCode")
    @Mapping(target = "journalTitle", expression = "java( mapStringToTitle(minimizedWorkEntity.getJournalTitle()) )")
    @Mapping(target = "type", expression = "java( mapStringToWorkType(minimizedWorkEntity.getWorkType()) )")
    @Mapping(target = "publicationDate", expression = "java( mapPublicationDate(minimizedWorkEntity.getPublicationDate()) )")
    @Mapping(target = "externalIdentifiers", expression = "java( extIdMapper.convertFrom(minimizedWorkEntity.getExternalIdentifiersJson()) )")
    @Mapping(target = "url", expression = "java( mapStringToUrl(minimizedWorkEntity.getWorkUrl()) )")
    @Mapping(source = "visibility", target = "visibility")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract WorkSummary toWorkSummary(MinimizedWorkEntity minimizedWorkEntity);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "workTitle.title.content")
    @Mapping(source = "subtitle", target = "workTitle.subtitle")
    @Mapping(source = "translatedTitle", target = "workTitle.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "workTitle.translatedTitle.languageCode")
    @Mapping(target = "journalTitle", expression = "java( mapStringToTitle(minimizedWorkEntity.getJournalTitle()) )")
    @Mapping(source = "description", target = "shortDescription")
    @Mapping(target = "workType", expression = "java( mapStringToWorkType(minimizedWorkEntity.getWorkType()) )")
    @Mapping(target = "publicationDate", expression = "java( mapPublicationDate(minimizedWorkEntity.getPublicationDate()) )")
    @Mapping(target = "workExternalIdentifiers", expression = "java( extIdMapper.convertFrom(minimizedWorkEntity.getExternalIdentifiersJson()) )")
    @Mapping(target = "url", expression = "java( mapStringToUrl(minimizedWorkEntity.getWorkUrl()) )")
    @Mapping(source = "visibility", target = "visibility")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract Work toWorkFromMinimized(MinimizedWorkEntity minimizedWorkEntity);

    @Mapping(source = "id", target = "putCode")
    @Mapping(source = "title", target = "title.title.content")
    @Mapping(source = "subtitle", target = "title.subtitle")
    @Mapping(source = "translatedTitle", target = "title.translatedTitle.content")
    @Mapping(source = "translatedTitleLanguageCode", target = "title.translatedTitle.languageCode")
    @Mapping(target = "journalTitle", expression = "java( mapStringToTitle(minimizedExtendedWorkEntity.getJournalTitle()) )")
    @Mapping(target = "type", expression = "java( mapStringToWorkType(minimizedExtendedWorkEntity.getWorkType()) )")
    @Mapping(target = "publicationDate", expression = "java( mapPublicationDate(minimizedExtendedWorkEntity.getPublicationDate()) )")
    @Mapping(target = "externalIdentifiers", expression = "java( extIdMapper.convertFrom(minimizedExtendedWorkEntity.getExternalIdentifiersJson()) )")
    @Mapping(target = "url", expression = "java( mapStringToUrl(minimizedExtendedWorkEntity.getWorkUrl()) )")
    @Mapping(source = "visibility", target = "visibility")
    @Mapping(source = "dateCreated", target = "createdDate.value")
    @Mapping(source = "lastModified", target = "lastModifiedDate.value")
    @Mapping(source = ".", target = "source")
    public abstract WorkSummaryExtended toWorkSummaryExtended(MinimizedExtendedWorkEntity minimizedExtendedWorkEntity);

    // ========================================================================
    // Collection Mappings
    // ========================================================================

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