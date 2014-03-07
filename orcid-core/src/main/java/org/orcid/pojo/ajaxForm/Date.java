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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.orcid.jaxb.model.message.Day;
import org.orcid.jaxb.model.message.FuzzyDate;
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
    
    public java.util.Date toJavaDate() {
        Calendar gc = GregorianCalendar.getInstance();
        if (!PojoUtil.isEmpty(this.getDay()))
            gc.set(Calendar.DAY_OF_MONTH, Integer.parseInt(this.getDay()));
        if (!PojoUtil.isEmpty(this.getMonth()))
            gc.set(Calendar.MONTH, Integer.parseInt(this.getMonth()) - 1);
        if (!PojoUtil.isEmpty(this.getYear()))
            gc.set(Calendar.YEAR, Integer.parseInt(this.getYear()));
        return gc.getTime();
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

}
