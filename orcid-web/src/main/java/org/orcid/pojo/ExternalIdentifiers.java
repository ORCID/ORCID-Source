package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

public class ExternalIdentifiers implements ErrorsInterface {
    
    private List<ExternalIdentifier> externalIdentifiers = null;
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<ExternalIdentifier> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<ExternalIdentifier> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

}
