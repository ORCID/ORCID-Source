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

public class Date implements ErrorsInterface, Required {

    private List<String> errors = new ArrayList<String>();
    private String month;
    private String day;
    private String year;
    
    private boolean required = true;
    private String getRequiredMessage;
    
    public Date() {
        
    }
    
    public Date(org.orcid.jaxb.model.message.PublicationDate publicationDate) {
        if (publicationDate != null) {
            if (publicationDate.getDay() != null)
                this.setDay(publicationDate.getDay().getValue());
            if (publicationDate.getMonth() != null)
                this.setMonth(publicationDate.getMonth().getValue());
            if (publicationDate.getYear() != null)
                this.setYear(publicationDate.getYear().getValue());
        }
    }
    
    public Date(String year, String month, String day) {
        
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
