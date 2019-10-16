package org.orcid.core.utils.v3;

import org.apache.commons.lang.StringUtils;
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
        
    public static int compareTo(FuzzyDate a, FuzzyDate b) {
    	StringBuilder dateString = new StringBuilder();
        dateString.append(a.getYear() != null ? a.getYear().getValue() : "0000");
        dateString.append(a.getMonth() != null ? StringUtils.leftPad(a.getMonth().getValue(), 2, "0") : "00");
        dateString.append(a.getDay() != null ? StringUtils.leftPad(a.getDay().getValue(), 2, "0") : "00");

        StringBuilder otherDateString = new StringBuilder();
        otherDateString.append(b.getYear() != null ? b.getYear().getValue() : "0000");
        otherDateString.append(b.getMonth() != null ? StringUtils.leftPad(b.getMonth().getValue(), 2, "0"): "00");
        otherDateString.append(b.getDay() != null ? StringUtils.leftPad(b.getDay().getValue(), 2, "0") : "00");

        return dateString.toString().compareTo(otherDateString.toString());
    }
    
}
