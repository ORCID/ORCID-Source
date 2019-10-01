package org.orcid.pojo.ajaxForm;

import java.util.ArrayList;
import java.util.List;

public class EditEmail implements ErrorsInterface {
    
    private String original;
    
    private String edited;
    
    private List<String> errors = new ArrayList<String>();

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getEdited() {
        return edited;
    }

    public void setEdited(String edited) {
        this.edited = edited;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
}
