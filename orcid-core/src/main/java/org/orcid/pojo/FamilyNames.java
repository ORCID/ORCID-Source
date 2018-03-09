package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class FamilyNames extends org.orcid.jaxb.model.message.GivenNames implements ErrorsInterface {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    public FamilyNames() {

    }

    public FamilyNames(org.orcid.jaxb.model.message.GivenNames givenNames) {
        this.setContent(givenNames.getContent());
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
