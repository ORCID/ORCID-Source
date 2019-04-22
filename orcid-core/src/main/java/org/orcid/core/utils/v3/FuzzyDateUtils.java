package org.orcid.core.utils.v3;

import org.orcid.jaxb.model.v3.release.common.Day;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.Month;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.persistence.jpa.entities.EndDateEntity;
import org.orcid.persistence.jpa.entities.FuzzyDateEntity;

public class FuzzyDateUtils {
    
    public static EndDateEntity getEndDateEntity(FuzzyDate fuzzyDate) {
        EndDateEntity endDateEntity = new EndDateEntity();
        populateFromFuzzyDate(endDateEntity, fuzzyDate);
        return endDateEntity;
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
