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
    
    public Visibility() {
        visibility = org.orcid.jaxb.model.message.Visibility.PUBLIC;
    }
    
    public static Visibility valueOf(org.orcid.jaxb.model.message.Visibility visibility) {
        Visibility v = new Visibility();
        v.setVisibility(visibility);
        return v;
    }

    public static Visibility valueOf(org.orcid.jaxb.model.common_rc2.Visibility visibility) {
        Visibility v = new Visibility();
        v.setVisibility(org.orcid.jaxb.model.message.Visibility.fromValue(visibility.value()));
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        result = prime * result + ((getRequiredMessage == null) ? 0 : getRequiredMessage.hashCode());
        result = prime * result + (required ? 1231 : 1237);
        result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
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
        Visibility other = (Visibility) obj;
        if (errors == null) {
            if (other.errors != null)
                return false;
        } else if (!errors.equals(other.errors))
            return false;
        if (getRequiredMessage == null) {
            if (other.getRequiredMessage != null)
                return false;
        } else if (!getRequiredMessage.equals(other.getRequiredMessage))
            return false;
        if (required != other.required)
            return false;
        if (visibility != other.visibility)
            return false;
        return true;
    }    
}
