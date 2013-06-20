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

public class Visibility implements ErrorsInterface, Required, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private boolean required = true;

    private String getRequiredMessage;

    private org.orcid.jaxb.model.message.Visibility visibility;
    
    public static Visibility valueOf(org.orcid.jaxb.model.message.Visibility visibility) {
        Visibility v = new Visibility();
        v.setVisibility(visibility);
      return v;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public org.orcid.jaxb.model.message.Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(org.orcid.jaxb.model.message.Visibility visibility) {
        this.visibility = visibility;
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

}
