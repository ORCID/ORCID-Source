package org.orcid.core.utils;

import org.orcid.jaxb.model.message.Day;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.Month;
import org.orcid.jaxb.model.message.Year;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.FuzzyDateEntity;
import org.orcid.persistence.jpa.entities.StartDateEntity;

public class FuzzyDateUtils {
    
    public static EndDateEntity getEndDateEntity(FuzzyDate fuzzyDate) {
        EndDateEntity endDateEntity = new EndDateEntity();
        populateFromFuzzyDate(endDateEntity, fuzzyDate);
        return endDateEntity;
    }
    
    public static StartDateEntity getStartDateEntity(FuzzyDate fuzzyDate) {
        StartDateEntity startDateEntity = new StartDateEntity();
        populateFromFuzzyDate(startDateEntity, fuzzyDate);
        return startDateEntity;
    }

    private static void populateFromFuzzyDate(FuzzyDateEntity fuzzyDateEntity, FuzzyDate fuzzyDate) {
        if (fuzzyDate != null) {
            Year Year = fuzzyDate.getYear();
            if (Year != null) {
                fuzzyDateEntity.setYear(Integer.valueOf(Year.getValue()));
            }
            Month month = fuzzyDate.getMonth();
            if (month != null) {
                fuzzyDateEntity.setMonth(Integer.valueOf(month.getValue()));
            }
            Day day = fuzzyDate.getDay();
            if (day != null) {
                fuzzyDateEntity.setDay(Integer.valueOf(day.getValue()));
            }
        }
    }
    
}
