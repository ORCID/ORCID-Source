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

import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.OtherName;

public class Text implements ErrorsInterface, Required, Serializable {
    
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    private String value;
    private boolean required = true;
    private String getRequiredMessage;

    public static Text valueOf(String value) {
        Text t = new Text();
        t.setValue(value);
       return t;
    }

    public Keyword toKeyword() {
        return new Keyword(this.value);
    }

    public OtherName toOtherName() {
        return new OtherName(this.value);
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
    public String toString(){
        return this.value;
    }    
}
