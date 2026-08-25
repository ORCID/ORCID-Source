package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.MinimizedExtendedWorkEntity;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.WorkExtended;
import org.orcid.pojo.WorkSummaryExtended;
import org.orcid.pojo.ajaxForm.PojoUtil;

@Mapper
public interface WorkMapperV3 {

    WorkMapperV3 INSTANCE = Mappers.getMapper(WorkMapperV3.class);

    default void mapWorkAtoB(Work work, WorkEntity entity) {
        entity.setWorkType(work.getWorkType().name());
        entity.setWorkUrl(work.getUrl() == null ? null : work.getUrl().getValue());
        entity.setIso2Country(work.getCountry() == null ? null : work.getCountry().getValue().toString());
        entity.setJournalTitle(work.getJournalTitle() == null ? null : work.getJournalTitle().getContent());
        entity.setTranslatedTitle((work.getWorkTitle() == null || work.getWorkTitle().getTranslatedTitle() == null) ? null : work.getWorkTitle().getTranslatedTitle().getContent());
        entity.setTranslatedTitleLanguageCode(
                (work.getWorkTitle() == null || work.getWorkTitle().getTranslatedTitle() == null) ? null : work.getWorkTitle().getTranslatedTitle().getLanguageCode());
        entity.setSubtitle((work.getWorkTitle() == null || work.getWorkTitle().getSubtitle() == null) ? null : work.getWorkTitle().getSubtitle().getContent());
        entity.setCitation(work.getWorkCitation() == null ? null : work.getWorkCitation().getCitation());
        entity.setCitationType((work.getWorkCitation() == null || work.getWorkCitation().getWorkCitationType() == null) ? null : work.getWorkCitation().getWorkCitationType().toString());
    }

    default void mapWorkBtoA(WorkEntity entity, Work work) {
        work.setWorkType(WorkType.valueOf(entity.getWorkType()));
        work.setJournalTitle(entity.getJournalTitle() != null && !entity.getJournalTitle().isEmpty() ? new Title(entity.getJournalTitle()) : null);
    }

    default void mapWorkSummaryAtoB(WorkSummary summary, WorkEntity entity) {
        entity.setWorkType(summary.getType().name());
        entity.setJournalTitle(summary.getJournalTitle() != null && summary.getJournalTitle().getContent() != null ? summary.getJournalTitle().getContent() : null);
    }

    default void mapWorkSummaryBtoA(WorkEntity entity, WorkSummary summary) {
        summary.setType(WorkType.valueOf(entity.getWorkType()));
        summary.setJournalTitle(entity.getJournalTitle() != null && !entity.getJournalTitle().isEmpty() ? new Title(entity.getJournalTitle()) : null);
    }

    default void mapWorkSummaryMinimizedAtoB(WorkSummary summary, MinimizedWorkEntity entity) {
        entity.setWorkType(summary.getType().name());
        entity.setJournalTitle(summary.getJournalTitle() != null && summary.getJournalTitle().getContent() != null ? summary.getJournalTitle().getContent() : null);
    }

    default void mapWorkSummaryMinimizedBtoA(MinimizedWorkEntity entity, WorkSummary summary) {
        summary.setType(WorkType.valueOf(entity.getWorkType()));
        summary.setJournalTitle(entity.getJournalTitle() != null && !entity.getJournalTitle().isEmpty() ? new Title(entity.getJournalTitle()) : null);
    }

    default void mapWorkSummaryExtendedMinimizedAtoB(WorkSummaryExtended summary, MinimizedExtendedWorkEntity entity) {
        entity.setWorkType(summary.getType().name());
        entity.setJournalTitle(summary.getJournalTitle() != null && summary.getJournalTitle().getContent() != null ? summary.getJournalTitle().getContent() : null);
    }

    default void mapWorkSummaryExtendedMinimizedBtoA(MinimizedExtendedWorkEntity entity, WorkSummaryExtended summary) {
        summary.setType(WorkType.valueOf(entity.getWorkType()));
        summary.setJournalTitle(entity.getJournalTitle() != null && !entity.getJournalTitle().isEmpty() ? new Title(entity.getJournalTitle()) : null);
    }

    default void mapMinimizedWorkAtoB(Work work, MinimizedWorkEntity entity) {
        entity.setWorkType(work.getWorkType().name());
        entity.setJournalTitle(work.getJournalTitle() != null && work.getJournalTitle().getContent() != null ? work.getJournalTitle().getContent() : null);
    }

    default void mapMinimizedWorkBtoA(MinimizedWorkEntity entity, Work work) {
        work.setWorkType(WorkType.valueOf(entity.getWorkType()));
        work.setJournalTitle(entity.getJournalTitle() != null && !entity.getJournalTitle().isEmpty() ? new Title(entity.getJournalTitle()) : null);
    }

    default void mapWorkExtendedAtoB(WorkExtended work, WorkEntity entity) {
        entity.setWorkType(work.getWorkType().name());
        entity.setWorkUrl(work.getUrl() == null ? null : work.getUrl().getValue());
        entity.setIso2Country(work.getCountry() == null ? null : work.getCountry().getValue().toString());
        entity.setJournalTitle(work.getJournalTitle() == null ? null : work.getJournalTitle().getContent());
        entity.setTranslatedTitle((work.getWorkTitle() == null || work.getWorkTitle().getTranslatedTitle() == null) ? null : work.getWorkTitle().getTranslatedTitle().getContent());
        entity.setTranslatedTitleLanguageCode(
                (work.getWorkTitle() == null || work.getWorkTitle().getTranslatedTitle() == null) ? null : work.getWorkTitle().getTranslatedTitle().getLanguageCode());
        entity.setSubtitle((work.getWorkTitle() == null || work.getWorkTitle().getSubtitle() == null) ? null : work.getWorkTitle().getSubtitle().getContent());
        entity.setCitation(work.getWorkCitation() == null ? null : work.getWorkCitation().getCitation());
        entity.setCitationType((work.getWorkCitation() == null || work.getWorkCitation().getWorkCitationType() == null) ? null : work.getWorkCitation().getWorkCitationType().toString());
    }

    default void mapWorkExtendedBtoA(WorkEntity entity, WorkExtended work, ContributorsRolesAndSequencesMapperV3 contributorsConverter) {
        work.setWorkType(WorkType.valueOf(entity.getWorkType()));
        work.setJournalTitle(entity.getJournalTitle() != null && !entity.getJournalTitle().isEmpty() ? new Title(entity.getJournalTitle()) : null);

        if (!PojoUtil.isEmpty(entity.getTopContributorsJson())) {
            work.setContributorsGroupedByOrcid(contributorsConverter.getContributorsRolesAndSequencesList(entity.getTopContributorsJson()));
        }
    }
}
