package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

public class ExternalIdentifier extends org.orcid.jaxb.model.message.ExternalIdentifier implements ErrorsInterface {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
