package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EndDateEntity extends FuzzyDateEntity {

    /**
     * 
     */
    private static final long serialVersionUID = -3305842000377366372L;

    public EndDateEntity() {
    }

    public EndDateEntity(Integer year, Integer month) {
        super(year, month, null);
    }
    
    public EndDateEntity(Integer year, Integer month, Integer day) {
        super(year, month, day);
    }

    @Override
    @Column(name = "end_year")
    public Integer getYear() {
        return super.getYear();
    }

    @Override
    public void setYear(Integer year) {
        super.setYear(year);
    }

    @Override
    @Column(name = "end_month")
    public Integer getMonth() {
        return super.getMonth();
    }

    @Override
    public void setMonth(Integer month) {
        super.setMonth(month);
    }

    @Override
    @Column(name = "end_day")
    public Integer getDay() {
        return super.getDay();
    }

    @Override
    public void setDay(Integer day) {
        super.setDay(day);
    }

}