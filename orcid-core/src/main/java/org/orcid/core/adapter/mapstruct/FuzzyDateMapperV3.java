package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.v3.release.common.Day;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.Month;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;

@Mapper
public interface FuzzyDateMapperV3 {

    FuzzyDateMapperV3 INSTANCE = Mappers.getMapper(FuzzyDateMapperV3.class);

    default void fuzzyDateToPublicationDateEntity(FuzzyDate fuzzyDate, PublicationDateEntity entity) {
        entity.setYear(fuzzyDate.getYear() != null ? Integer.valueOf(fuzzyDate.getYear().getValue()) : null);
        entity.setMonth(fuzzyDate.getMonth() != null ? Integer.valueOf(fuzzyDate.getMonth().getValue()) : null);
        entity.setDay(fuzzyDate.getDay() != null ? Integer.valueOf(fuzzyDate.getDay().getValue()) : null);
    }

    default void publicationDateEntityToFuzzyDate(PublicationDateEntity entity, FuzzyDate fuzzyDate) {
        fuzzyDate.setYear(entity.getYear() != null ? new Year(entity.getYear()) : null);
        fuzzyDate.setMonth(entity.getMonth() != null ? new Month(entity.getMonth()) : null);
        fuzzyDate.setDay(entity.getDay() != null ? new Day(entity.getDay()) : null);
    }

    default void fuzzyDateToStartDateEntity(FuzzyDate fuzzyDate, StartDateEntity entity) {
        entity.setYear(fuzzyDate.getYear() != null ? Integer.valueOf(fuzzyDate.getYear().getValue()) : null);
        entity.setMonth(fuzzyDate.getMonth() != null ? Integer.valueOf(fuzzyDate.getMonth().getValue()) : null);
        entity.setDay(fuzzyDate.getDay() != null ? Integer.valueOf(fuzzyDate.getDay().getValue()) : null);
    }

    default void startDateEntityToFuzzyDate(StartDateEntity entity, FuzzyDate fuzzyDate) {
        fuzzyDate.setYear(entity.getYear() != null ? new Year(entity.getYear()) : null);
        fuzzyDate.setMonth(entity.getMonth() != null ? new Month(entity.getMonth()) : null);
        fuzzyDate.setDay(entity.getDay() != null ? new Day(entity.getDay()) : null);
    }

    default void fuzzyDateToEndDateEntity(FuzzyDate fuzzyDate, EndDateEntity entity) {
        entity.setYear(fuzzyDate.getYear() != null ? Integer.valueOf(fuzzyDate.getYear().getValue()) : null);
        entity.setMonth(fuzzyDate.getMonth() != null ? Integer.valueOf(fuzzyDate.getMonth().getValue()) : null);
        entity.setDay(fuzzyDate.getDay() != null ? Integer.valueOf(fuzzyDate.getDay().getValue()) : null);
    }

    default void endDateEntityToFuzzyDate(EndDateEntity entity, FuzzyDate fuzzyDate) {
        fuzzyDate.setYear(entity.getYear() != null ? new Year(entity.getYear()) : null);
        fuzzyDate.setMonth(entity.getMonth() != null ? new Month(entity.getMonth()) : null);
        fuzzyDate.setDay(entity.getDay() != null ? new Day(entity.getDay()) : null);
    }
}
