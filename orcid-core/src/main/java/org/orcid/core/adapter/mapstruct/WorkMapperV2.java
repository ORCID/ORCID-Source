package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.jpa.entities.MinimizedWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

@Mapper
public interface WorkMapperV2 {

    WorkMapperV2 INSTANCE = Mappers.getMapper(WorkMapperV2.class);

    default void mapWorkAtoB(Work work, WorkEntity entity) {
        if (WorkType.DISSERTATION.equals(work.getWorkType())) {
            entity.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
        } else {
            entity.setWorkType(work.getWorkType().name());
        }
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
        WorkType resolvedType = resolveWorkType(entity.getWorkType());
        if (WorkType.REVIEW.equals(resolvedType)) {
            work.setWorkType(WorkType.OTHER);
        } else {
            work.setWorkType(resolvedType);
        }
    }

    default void mapWorkSummaryAtoB(WorkSummary summary, WorkEntity entity) {
        if (WorkType.DISSERTATION.equals(summary.getType())) {
            entity.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
        } else {
            entity.setWorkType(summary.getType().name());
        }
    }

    default void mapWorkSummaryBtoA(WorkEntity entity, WorkSummary summary) {
        summary.setType(resolveWorkType(entity.getWorkType()));
    }

    default void mapWorkSummaryToMinimizedAtoB(WorkSummary summary, MinimizedWorkEntity entity) {
        if (WorkType.DISSERTATION.equals(summary.getType())) {
            entity.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
        } else {
            entity.setWorkType(summary.getType().name());
        }
    }

    default void mapWorkSummaryToMinimizedBtoA(MinimizedWorkEntity entity, WorkSummary summary) {
        summary.setType(resolveWorkType(entity.getWorkType()));
    }

    default void mapMinimizedWorkAtoB(Work work, MinimizedWorkEntity entity) {
        if (WorkType.DISSERTATION.equals(work.getWorkType())) {
            entity.setWorkType(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name());
        } else {
            entity.setWorkType(work.getWorkType().name());
        }
    }

    default void mapMinimizedWorkBtoA(MinimizedWorkEntity entity, Work work) {
        work.setWorkType(resolveWorkType(entity.getWorkType()));
    }

    default WorkType resolveWorkType(String name) {
        if (org.orcid.jaxb.model.common.WorkType.SOFTWARE.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.PREPRINT.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.PHYSICAL_OBJECT.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.ANNOTATION.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.DATA_MANAGEMENT_PLAN.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.CONFERENCE_OUTPUT.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.CONFERENCE_PRESENTATION.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.CONFERENCE_PROCEEDINGS.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.TRANSCRIPTION.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.BLOG_POST.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.DESIGN.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.IMAGE.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.MOVING_IMAGE.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.MUSICAL_COMPOSITION.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.SOUND.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.CARTOGRAPHIC_MATERIAL.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.CLINICAL_STUDY.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.LEARNING_OBJECT.name().equals(name)
                || org.orcid.jaxb.model.common.WorkType.PUBLIC_SPEECH.name().equals(name)) {
            return WorkType.OTHER;
        }

        if (org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name().equals(name)) {
            return WorkType.DISSERTATION;
        }

        return WorkType.valueOf(name);
    }
}
