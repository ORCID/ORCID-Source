package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.record.OtherName;
import org.orcid.jaxb.model.v3.rc1.record.OtherNames;

public class OtherNamesForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private List<OtherNameForm> otherNames = new ArrayList<OtherNameForm>();
    
    private Visibility visibility;

    public static OtherNamesForm valueOf(OtherNames otherNames) {
        OtherNamesForm on = new OtherNamesForm();
        if (otherNames ==  null) {
            on.setVisibility(new Visibility());
            return on;
        }
        if (otherNames.getOtherNames() != null) {
            for (OtherName otherName : otherNames.getOtherNames()) {
                on.getOtherNames().add(OtherNameForm.valueOf(otherName));
            }
                
        }        
        return on;
    }
    
    public OtherNames toOtherNames() {
        OtherNames otherNames = new OtherNames();
        List<OtherName> onList = new ArrayList<OtherName>();
        if(this.otherNames != null && !this.otherNames.isEmpty()) {
            for(OtherNameForm otherNameForm : this.otherNames) {
                onList.add(otherNameForm.toOtherName());
            }
        }
        
        otherNames.setOtherNames(onList);
        return otherNames;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<OtherNameForm> getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(List<OtherNameForm> otherNames) {
        this.otherNames = otherNames;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }        
}
