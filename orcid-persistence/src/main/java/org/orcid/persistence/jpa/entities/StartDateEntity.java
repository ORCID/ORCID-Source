package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.orcid.jaxb.model.message.FuzzyDate;

@Embeddable
public class StartDateEntity extends FuzzyDateEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public StartDateEntity(FuzzyDate fuzzyDate) {
        super(fuzzyDate);
    }

    public StartDateEntity() {
    }

    public StartDateEntity(Integer year, Integer month) {
        super(year, month, null);
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