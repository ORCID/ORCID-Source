package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;

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

    public boolean compare(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OtherNamesForm other = (OtherNamesForm) obj;

         if (otherNames != null && other.getOtherNames() != null && otherNames.size() != other.getOtherNames().size()) {
            return false;
        } else {
             for (int i = 0; i < otherNames.size(); i++) {
                 if (!otherNames.get(i).compare(other.getOtherNames().get(i))) {
                     return false;
                 }
             }
         }
        return true;
    }
}
