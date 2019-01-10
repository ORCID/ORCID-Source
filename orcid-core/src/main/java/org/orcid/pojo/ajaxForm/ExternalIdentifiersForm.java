package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers;

public class ExternalIdentifiersForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    private List<ExternalIdentifierForm> externalIdentifiers = new ArrayList<ExternalIdentifierForm>();
    private Visibility visibility;

    public static ExternalIdentifiersForm valueOf(PersonExternalIdentifiers extIds) {
        if(extIds == null) {
            return null;
        }
        ExternalIdentifiersForm form = new ExternalIdentifiersForm();
        for(PersonExternalIdentifier extId : extIds.getExternalIdentifiers()) {
            form.getExternalIdentifiers().add(ExternalIdentifierForm.valueOf(extId));
        }
            
        return form;
    }
    
    public PersonExternalIdentifiers toPersonExternalIdentifiers() {
        if(externalIdentifiers == null) {
            return null;
        }
        
        PersonExternalIdentifiers result = new PersonExternalIdentifiers();
        result.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>());
        for(ExternalIdentifierForm form: externalIdentifiers) {
            result.getExternalIdentifiers().add(form.toPersonExternalIdentifier());
        }
        
        return result;
    }
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<ExternalIdentifierForm> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<ExternalIdentifierForm> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }
    
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
}
