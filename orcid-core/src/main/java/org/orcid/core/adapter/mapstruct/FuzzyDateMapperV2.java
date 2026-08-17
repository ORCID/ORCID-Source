package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.common_v2.Day;
import org.orcid.jaxb.model.common_v2.FuzzyDate;
import org.orcid.jaxb.model.common_v2.Month;
import org.orcid.jaxb.model.common_v2.Year;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;

@Mapper
public interface FuzzyDateMapperV2 {

    FuzzyDateMapperV2 INSTANCE = Mappers.getMapper(FuzzyDateMapperV2.class);

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
