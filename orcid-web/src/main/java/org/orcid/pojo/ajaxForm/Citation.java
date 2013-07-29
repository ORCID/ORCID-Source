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
import java.util.List;

import org.orcid.jaxb.model.message.CitationType;


public class Citation implements ErrorsInterface, Required, Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<String> errors = new ArrayList<String>();
    private Text citation;
    private Text citationType;
    private boolean required = true;
    private String getRequiredMessage;

    public static Citation valueOf(org.orcid.jaxb.model.message.Citation citation) {
        Citation c  = new Citation();
            if (citation.getCitation() !=null) {
                Text cText = new Text();
                cText.setValue(citation.getCitation());
               c.setCitation(cText);
            }
            if (citation.getWorkCitationType() != null) {
                Text ctText = new Text();
                ctText.setValue(citation.getWorkCitationType().value());
                c.setCitationType(ctText);
            }
            return c;
    }
    
    public org.orcid.jaxb.model.message.Citation toCitiation() {
        org.orcid.jaxb.model.message.Citation c = new org.orcid.jaxb.model.message.Citation();
        if (this.getCitation() != null)
            c.setCitation(this.getCitation().getValue());
        if (!PojoUtil.isEmpty(this.getCitationType()))
            c.setWorkCitationType(CitationType.fromValue(this.getCitationType().getValue()));
        return c;
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

    public Text getCitation() {
        return citation;
    }

    public void setCitation(Text citation) {
        this.citation = citation;
    }

    public Text getCitationType() {
        return citationType;
    }

    public void setCitationType(Text citationType) {
        this.citationType = citationType;
    }

 
}
