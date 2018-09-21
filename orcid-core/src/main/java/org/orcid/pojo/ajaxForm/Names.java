package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.core.security.visibility.OrcidVisibilityDefaults;

public class Names implements ErrorsInterface, Serializable{


	private static final long serialVersionUID = 1L;
    private Name effective = null;
    private Name real = null;
    private List<String> errors = new ArrayList<String>();
    
    public static Names valueOf(org.orcid.jaxb.model.v3.rc1.record.Name current, org.orcid.jaxb.model.v3.rc1.record.Name effective) {
    	Names names = new Names();
        if (current != null) {
        	names.setEffective(Name.valueOf(current)); 
        }
        if (effective != null) {
        	names.setReal(Name.valueOf(effective)); 
        }
        return names;
    }
    
	public Name getEffective() {
		return this.effective;
	}

	public void setEffective(Name current) {
		this.effective = current;
	}

	public Name getReal() {
		return real;
	}

	public void setReal(Name real) {
		this.real = real;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public List<String> getErrors() {
		return errors;
	}
	@Override
	public void setErrors(List<String> errors) {
		this.errors = errors;
	}



}
