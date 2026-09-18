package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.orcid.jaxb.model.common_v2.Day;
import org.orcid.jaxb.model.common_v2.FuzzyDate;
import org.orcid.jaxb.model.common_v2.Month;
import org.orcid.jaxb.model.common_v2.Year;
import org.orcid.persistence.jpa.entities.CompletionDateEntity;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;

@Mapper(componentModel = "spring")
public interface FuzzyDateMapperV2 {

    @Mapping(source = "year.value", target = "year")
    @Mapping(source = "month.value", target = "month")
    @Mapping(source = "day.value", target = "day")
    StartDateEntity toStartDateEntity(FuzzyDate fuzzyDate);

    @Mapping(source = "year.value", target = "year")
    @Mapping(source = "month.value", target = "month")
    @Mapping(source = "day.value", target = "day")
    EndDateEntity toEndDateEntity(FuzzyDate fuzzyDate);

    @Mapping(source = "year.value", target = "year")
    @Mapping(source = "month.value", target = "month")
    @Mapping(source = "day.value", target = "day")
    PublicationDateEntity toPublicationDateEntity(FuzzyDate fuzzyDate);

    @Mapping(source = "year.value", target = "year")
    @Mapping(source = "month.value", target = "month")
    @Mapping(source = "day.value", target = "day")
    CompletionDateEntity toCompletionDateEntity(FuzzyDate fuzzyDate);



    @Mapping(source = "year", target = "year")
    @Mapping(source = "month", target = "month")
    @Mapping(source = "day", target = "day")
    FuzzyDate toFuzzyDate(StartDateEntity entity);

    @Mapping(source = "year", target = "year")
    @Mapping(source = "month", target = "month")
    @Mapping(source = "day", target = "day")
    FuzzyDate toFuzzyDate(EndDateEntity entity);

    @Mapping(source = "year", target = "year")
    @Mapping(source = "month", target = "month")
    @Mapping(source = "day", target = "day")
    FuzzyDate toFuzzyDate(PublicationDateEntity entity);

    @Mapping(source = "year", target = "year")
    @Mapping(source = "month", target = "month")
    @Mapping(source = "day", target = "day")
    FuzzyDate toFuzzyDate(CompletionDateEntity entity);


    
    default Year mapYear(Integer year) {
        return year == null ? null : new Year(year);
    }
    
    default Month mapMonth(Integer month) {
        return month == null ? null : new Month(month);
    }
    
    default Day mapDay(Integer day) {
        return day == null ? null : new Day(day);
    }
    
    default Integer map(Year year) {
        return (year == null || year.getValue() == null) ? null : Integer.valueOf(year.getValue());
    }
    
    default Integer map(Month month) {
        return (month == null || month.getValue() == null) ? null : Integer.valueOf(month.getValue());
    }
    
    default Integer map(Day day) {
        return (day == null || day.getValue() == null) ? null : Integer.valueOf(day.getValue());
    }
}