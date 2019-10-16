package org.orcid.core.utils;

import org.apache.commons.lang.StringUtils;
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

	public static int compareTo(org.orcid.jaxb.model.common_v2.FuzzyDate a,
			org.orcid.jaxb.model.common_v2.FuzzyDate b) {
		StringBuilder dateString = new StringBuilder();
		dateString.append(a.getYear() != null ? a.getYear().getValue() : "0000");
		dateString.append(a.getMonth() != null ? StringUtils.leftPad(a.getMonth().getValue(), 2, "0") : "00");
		dateString.append(a.getDay() != null ? StringUtils.leftPad(a.getDay().getValue(), 2, "0") : "00");

		StringBuilder otherDateString = new StringBuilder();
		otherDateString.append(b.getYear() != null ? b.getYear().getValue() : "0000");
		otherDateString.append(b.getMonth() != null ? StringUtils.leftPad(b.getMonth().getValue(), 2, "0") : "00");
		otherDateString.append(b.getDay() != null ? StringUtils.leftPad(b.getDay().getValue(), 2, "0") : "00");

		return dateString.toString().compareTo(otherDateString.toString());
	}
}
