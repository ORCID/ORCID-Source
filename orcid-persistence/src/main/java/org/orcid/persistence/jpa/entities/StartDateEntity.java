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

import org.orcid.jaxb.model.message.FuzzyDate;

@Embeddable
public class StartDateEntity extends FuzzyDateEntity {

    public StartDateEntity(FuzzyDate fuzzyDate) {
        super(fuzzyDate);
    }

    public StartDateEntity() {
    }

    public StartDateEntity(Integer year, Integer month, Integer day) {
        super(year, month, day);
    }

    @Override
    @Column(name = "start_year")
    public Integer getYear() {
        return super.getYear();
    }

    @Override
    public void setYear(Integer year) {
        super.setYear(year);
    }

    @Override
    @Column(name = "start_month")
    public Integer getMonth() {
        return super.getMonth();
    }

    @Override
    public void setMonth(Integer month) {
        super.setMonth(month);
    }

    @Override
    @Column(name = "start_day")
    public Integer getDay() {
        return super.getDay();
    }

    @Override
    public void setDay(Integer day) {
        super.setDay(day);
    }

}