package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.core.security.visibility.OrcidVisibilityDefaults;

public class Names implements ErrorsInterface, Serializable{


	private static final long serialVersionUID = 1L;
    private NamesForm effective = null;
    private NamesForm real = null;
    private List<String> errors = new ArrayList<String>();
    
    public static Names valueOf(org.orcid.jaxb.model.v3.rc2.record.Name current, org.orcid.jaxb.model.v3.rc2.record.Name effective) {
    	Names names = new Names();
        if (current != null) {
        	names.setEffective(NamesForm.valueOf(current)); 
        }
        if (effective != null) {
        	names.setReal(NamesForm.valueOf(effective)); 
        }
        return names;
    }
    
	public NamesForm getEffective() {
		return this.effective;
	}

	public void setEffective(NamesForm current) {
		this.effective = current;
	}

	public NamesForm getReal() {
		return real;
	}

	public void setReal(NamesForm real) {
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
