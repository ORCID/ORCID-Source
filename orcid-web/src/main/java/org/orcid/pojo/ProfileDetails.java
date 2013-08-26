package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class ProfileDetails implements ErrorsInterface {
    
    private List<String> errors = new ArrayList<String>();
    String orcid;
    String givenNames;
    String familyName;
    
    public ProfileDetails(){
        
    }
    
    public ProfileDetails(String orcid, String givenNames, String familyName){
        this.orcid = orcid;
        this.givenNames = givenNames;
        this.familyName = familyName;
    }
    
    public String getOrcid() {
        return orcid;
    }
    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    public String getGivenNames() {
        return givenNames;
    }
    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }
    public String getFamilyName() {
        return familyName;
    }
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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
