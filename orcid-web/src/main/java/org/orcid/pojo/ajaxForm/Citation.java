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
    private String citation;
    private String citationType;
    private boolean required = true;
    private String getRequiredMessage;

    public static Citation valueOf(org.orcid.jaxb.model.message.Citation citation) {
        Citation c  = new Citation();
            if (citation.getCitation() !=null) {
               c.setCitation(citation.getCitation());
            }
            if (citation.getWorkCitationType() != null) {
                c.setCitationType(citation.getWorkCitationType().value());
            }
            return c;
    }
    
    public org.orcid.jaxb.model.message.Citation toCitiation() {
        org.orcid.jaxb.model.message.Citation c = new org.orcid.jaxb.model.message.Citation();
        c.setCitation(this.citation);
        c.setWorkCitationType(CitationType.fromValue(this.citationType));
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

    public String getCitation() {
        return citation;
    }

    public void setCitation(String citation) {
        this.citation = citation;
    }

    public String getCitationType() {
        return citationType;
    }

    public void setCitationType(String citationType) {
        this.citationType = citationType;
    }

}
