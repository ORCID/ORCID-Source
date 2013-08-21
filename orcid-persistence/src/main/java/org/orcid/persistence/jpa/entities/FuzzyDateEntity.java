/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.orcid.jaxb.model.message.Day;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.Month;
import org.orcid.jaxb.model.message.Year;

@Embeddable
public class FuzzyDateEntity implements Comparable<FuzzyDateEntity> {

    private Integer year;

    private Integer month;

    private Integer day;

    public FuzzyDateEntity() {
    }

    public FuzzyDateEntity(Integer year, Integer month, Integer day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public FuzzyDateEntity(FuzzyDate fuzzyDate) {
        if (fuzzyDate != null) {
            Year Year = fuzzyDate.getYear();
            if (Year != null) {
                setYear(Integer.valueOf(Year.getValue()));
            }
            Month month = fuzzyDate.getMonth();
            if (month != null) {
                setMonth(Integer.valueOf(month.getValue()));
            }
            Day day = fuzzyDate.getDay();
            if (day != null) {
                setDay(Integer.valueOf(day.getValue()));
            }
        }
    }

    @Column(name = "fuzzy_year")
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Column(name = "fuzzy_month")
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Column(name = "fuzzy_day")
    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    @Override
    public int compareTo(FuzzyDateEntity other) {
        int result = 0;

        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }

        // Compare years
        if (other.getYear() == null) {
            if (this.year == null) {
                result = 0;
            } else {
                return 1;
            }
        } else if (this.year == null) {
            return -1;
        } else {
            result = this.year.compareTo(other.getYear());
            if (result != 0)
                return result;
        }

        // Compare months
        if (other.getMonth() == null) {
            if (this.month == null) {
                result = 0;
            } else {
                return 1;
            }
        } else if (this.month == null) {
            return -1;
        } else {
            result = this.month.compareTo(other.getMonth());
            if (result != 0)
                return result;
        }

        // Compare days
        if (other.getDay() == null) {
            if (this.day == null) {
                result = 0;
            } else {
                return 1;
            }
        } else if (this.day == null) {
            return -1;
        } else {
            result = this.day.compareTo(other.getDay());
        }
        return result;
    }
}