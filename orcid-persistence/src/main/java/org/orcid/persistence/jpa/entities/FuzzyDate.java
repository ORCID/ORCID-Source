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

@Embeddable
public class FuzzyDate implements Comparable<FuzzyDate> {

    private Integer year;

    private Integer month;

    private Integer day;

    public FuzzyDate() {
    }

    public FuzzyDate(Integer year, Integer month, Integer day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    @Column(name = "publication_year")
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Column(name = "publication_month")
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @Column(name = "publication_day")
    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    @Override
    public int compareTo(FuzzyDate other) {
        int result = 0;

        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }

        //Compare years
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

        //Compare months
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

        //Compare days
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