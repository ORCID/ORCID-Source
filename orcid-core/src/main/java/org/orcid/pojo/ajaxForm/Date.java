/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.orcid.jaxb.model.message.CreatedDate;
import org.orcid.jaxb.model.message.Day;
import org.orcid.jaxb.model.message.FuzzyDate;
import org.orcid.jaxb.model.message.LastModifiedDate;
import org.orcid.jaxb.model.message.Month;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.Year;

public class Date implements ErrorsInterface, Required, Serializable {

    private static final long serialVersionUID = -1379185374840409915L;
    private List<String> errors = new ArrayList<String>();
    private String month;
    private String day;
    private String year;

    private boolean required = true;
    private String getRequiredMessage;
    
    private transient DatatypeFactory datatypeFactory = null;

    public Date() {
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            // We're in serious trouble and can't carry on
            throw new IllegalStateException("Cannot create new DatatypeFactory");
        }    
    }
    
    public static Date valueOf(FuzzyDate fuzzyDate) {
        Date d = new Date();
        if (fuzzyDate.getDay() != null && fuzzyDate.getDay().getValue() !=null)
            d.setDay(fuzzyDate.getDay().getValue());
        if (fuzzyDate.getMonth() != null && fuzzyDate.getMonth().getValue() !=null)
            d.setMonth(fuzzyDate.getMonth().getValue());
        if (fuzzyDate.getYear() != null && fuzzyDate.getYear().getValue() !=null)
            d.setYear(fuzzyDate.getYear().getValue());
        return d;
    }
    
    public static Date valueOf(org.orcid.jaxb.model.common_rc4.FuzzyDate fuzzyDate) {
        Date d = new Date();
        if (fuzzyDate.getDay() != null && fuzzyDate.getDay().getValue() !=null)
            d.setDay(fuzzyDate.getDay().getValue());
        if (fuzzyDate.getMonth() != null && fuzzyDate.getMonth().getValue() !=null)
            d.setMonth(fuzzyDate.getMonth().getValue());
        if (fuzzyDate.getYear() != null && fuzzyDate.getYear().getValue() !=null)
            d.setYear(fuzzyDate.getYear().getValue());
        return d;
    }

    public FuzzyDate toFuzzyDate() {
        PublicationDate pd = new PublicationDate();
        if (!PojoUtil.isEmpty(this.getDay()))
            pd.setDay(new Day(new Integer(this.getDay())));
        if (!PojoUtil.isEmpty(this.getMonth()))
            pd.setMonth(new Month(new Integer(this.getMonth())));
        if (!PojoUtil.isEmpty(this.getYear()))
            pd.setYear(new Year(new Integer(this.getYear())));
        return pd;
    }
    
    public static Date valueOf(java.util.Date date) {
        Date newDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        newDate.setDay(Integer.toString(cal.get(Calendar.DAY_OF_MONTH)));
        newDate.setMonth(Integer.toString(cal.get(Calendar.MONTH) + 1));
        newDate.setYear(Integer.toString(cal.get(Calendar.YEAR)));
        return newDate;
    }

    public static Date valueOf(org.orcid.jaxb.model.common_rc4.CreatedDate date) {
        Date newDate = new Date();
        if (date != null && date.getValue() != null)
            return Date.valueOf(date.getValue().toGregorianCalendar().getTime());
        return newDate;
    }

    public static Date valueOf(org.orcid.jaxb.model.common_rc4.LastModifiedDate date) {
        Date newDate = new Date();
        if (date != null && date.getValue() != null)
            return Date.valueOf(date.getValue().toGregorianCalendar().getTime());
        return newDate;
    }
    
    public static Date valueOf(CreatedDate date) {
        Date newDate = new Date();
        if (date != null && date.getValue() != null)
            return Date.valueOf(date.getValue().toGregorianCalendar().getTime());
        return newDate;
    }

    public static Date valueOf(LastModifiedDate date) {
        Date newDate = new Date();
        if (date != null && date.getValue() != null)
            return Date.valueOf(date.getValue().toGregorianCalendar().getTime());
        return newDate;
    }
    
    public java.util.Date toJavaDate() {
        Calendar gc = toCalendar();
        return gc.getTime();
    }

    public GregorianCalendar toCalendar() {
        GregorianCalendar gc = new GregorianCalendar();
        if (!PojoUtil.isEmpty(this.getDay()))
            gc.set(Calendar.DAY_OF_MONTH, Integer.parseInt(this.getDay()));
        if (!PojoUtil.isEmpty(this.getMonth()))
            gc.set(Calendar.MONTH, Integer.parseInt(this.getMonth()) - 1);
        if (!PojoUtil.isEmpty(this.getYear()))
            gc.set(Calendar.YEAR, Integer.parseInt(this.getYear()));
        return gc;
    }
    
    public LastModifiedDate toLastModifiedDate() {
        GregorianCalendar cal = toCalendar();
        LastModifiedDate lastModifiedDate = new LastModifiedDate();
        lastModifiedDate.setValue(datatypeFactory.newXMLGregorianCalendar(cal));
        return lastModifiedDate;
    }
    
    public CreatedDate toCreatedDate() {
        GregorianCalendar cal = toCalendar();
        CreatedDate createdDate = new CreatedDate();
        createdDate.setValue(datatypeFactory.newXMLGregorianCalendar(cal));
        return createdDate;
    }
  
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getGetRequiredMessage() {
        return getRequiredMessage;
    }

    public void setGetRequiredMessage(String getRequiredMessage) {
        this.getRequiredMessage = getRequiredMessage;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((day == null) ? 0 : day.hashCode());
        result = prime * result + ((month == null) ? 0 : month.hashCode());
        result = prime * result + ((year == null) ? 0 : year.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Date other = (Date) obj;
        if (day == null) {
            if (other.day != null)
                return false;
        } else if (!day.equals(other.day))
            return false;
        if (month == null) {
            if (other.month != null)
                return false;
        } else if (!month.equals(other.month))
            return false;
        if (year == null) {
            if (other.year != null)
                return false;
        } else if (!year.equals(other.year))
            return false;
        return true;
    }        
}
