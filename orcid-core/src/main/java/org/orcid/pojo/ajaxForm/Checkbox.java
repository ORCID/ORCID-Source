package org.orcid.pojo.ajaxForm;

import java.util.ArrayList;
import java.util.List;

public class Checkbox implements ErrorsInterface, Required {

    private List<String> errors = new ArrayList<String>();
    private boolean value;
    private boolean required = true;
    private String getRequiredMessage;

    public Checkbox() {
        
    }
    
    public Checkbox(Boolean value) {
        this.value = value;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
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

    public static Checkbox valueOf(Boolean value) {
        return new Checkbox(value);
    }    
}
