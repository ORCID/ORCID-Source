package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.core.security.visibility.OrcidVisibilityDefaults;

public class Name extends VisibilityForm implements ErrorsInterface, Serializable{

	private List<String> errors = new ArrayList<String>();
    private static final long serialVersionUID = 1L;
    
    private Text givenNames;
    private Text familyName;
	private Text creditName; 
    
    public static Name valueOf(org.orcid.jaxb.model.v3.rc1.record.Name current) {

    	Name name = new Name();
    	
        if (current.getGivenNames() != null) {
        	name.setGivenNames(Text.valueOf(current.getGivenNames().getContent()));
        }

        if (current.getFamilyName() != null) {
        	name.setFamilyName(Text.valueOf(current.getFamilyName().getContent()));
        }

        if (current.getCreditName() != null) {
        	name.setCreditName(Text.valueOf(current.getCreditName().getContent()));
        }

        if (current.getVisibility() != null) {
        	name.setVisibility(Visibility.valueOf(current.getVisibility()));
        } else {
            org.orcid.jaxb.model.common_v2.Visibility v = org.orcid.jaxb.model.common_v2.Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value());
            name.setVisibility(Visibility.valueOf(v));
        }
        return name;
	}
    
	public Text getGivenNames() {
		return givenNames;
	}
	public void setGivenNames(Text givenNames) {
		this.givenNames = givenNames;
	}
	public Text getFamilyName() {
		return familyName;
	}
	public void setFamilyName(Text familyName) {
		this.familyName = familyName;
	}
	public Text getCreditName() {
		return creditName;
	}
	public void setCreditName(Text creditName) {
		this.creditName = creditName;
	} 
    
    public List<String> getErrors() {
		return errors;
	}
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
