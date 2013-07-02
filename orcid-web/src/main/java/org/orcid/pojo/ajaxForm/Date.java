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

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.message.Day;
import org.orcid.jaxb.model.message.Month;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.Year;

public class Date implements ErrorsInterface, Required {

    private List<String> errors = new ArrayList<String>();
    private String month;
    private String day;
    private String year;

    private boolean required = true;
    private String getRequiredMessage;

    public static Date valueOf(PublicationDate publicationDate) {
        Date d = new Date();
        if (publicationDate.getDay() != null && publicationDate.getDay().getValue() !=null)
            d.setDay(publicationDate.getDay().getValue());
        if (d.getMonth() != null && publicationDate.getMonth().getValue() !=null)
            d.setMonth(publicationDate.getMonth().getValue());
        if (publicationDate.getYear() != null && publicationDate.getYear().getValue() !=null)
            d.setYear(publicationDate.getYear().getValue());
        return d;
    }

    public PublicationDate toPublicationDate() {
        PublicationDate pd = new PublicationDate();
        if (this.getDay() != null && !this.getDay().trim().isEmpty())
            pd.setDay(new Day(new Integer(this.getDay())));
        if (this.getMonth() != null && !this.getMonth().trim().isEmpty())
            pd.setMonth(new Month(new Integer(this.getMonth())));
        if (this.getYear() != null && !this.getYear().trim().isEmpty())
            pd.setYear(new Year(new Integer(this.getYear())));
        return pd;
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
