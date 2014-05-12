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

public class Iso2Country implements ErrorsInterface, Required, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private boolean required = true;

    private String getRequiredMessage;

    private org.orcid.jaxb.model.message.Iso3166Country value;
    
    public Iso2Country() {
        setValue(null);
    }
    
    public static Iso2Country valueOf(org.orcid.jaxb.model.message.Iso3166Country country) {
        Iso2Country c = new Iso2Country();
        c.setValue(country);
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

    public org.orcid.jaxb.model.message.Iso3166Country getValue() {
        return value;
    }

    public void setValue(org.orcid.jaxb.model.message.Iso3166Country country) {
        this.value = country;
    }

}
