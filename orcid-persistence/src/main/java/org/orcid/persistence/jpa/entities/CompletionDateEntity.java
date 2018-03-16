package org.orcid.persistence.jpa.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.orcid.jaxb.model.message.FuzzyDate;

@Embeddable
public class CompletionDateEntity extends FuzzyDateEntity implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public CompletionDateEntity(FuzzyDate fuzzyDate) {
        super(fuzzyDate);
    }

    public CompletionDateEntity() {
    }

    public CompletionDateEntity(Integer year, Integer month) {
        super(year, month, null);
    }
    
    public CompletionDateEntity(Integer year, Integer month, Integer day) {
        super(year, month, day);
    }

    @Override
    @Column(name = "completion_year")
    public Integer getYear() {
        return super.getYear();
    }

    @Override
    public void setYear(Integer year) {
        super.setYear(year);
    }

    @Override
    @Column(name = "completion_month")
    public Integer getMonth() {
        return super.getMonth();
    }

    @Override
    public void setMonth(Integer month) {
        super.setMonth(month);
    }

    @Override
    @Column(name = "completion_day")
    public Integer getDay() {
        return super.getDay();
    }

    @Override
    public void setDay(Integer day) {
        super.setDay(day);
    } 
}