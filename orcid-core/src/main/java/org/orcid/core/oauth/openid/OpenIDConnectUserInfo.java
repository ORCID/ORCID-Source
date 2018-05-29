package org.orcid.core.oauth.openid;

import org.orcid.jaxb.model.v3.rc1.record.Person;
import org.orcid.jaxb.model.v3.rc1.record.PersonalDetails;
import org.springframework.beans.factory.annotation.Value;

public class OpenIDConnectUserInfo {
    
    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    private String sub;
    private String name;
    private String family_name;
    private String given_name;
    
    public OpenIDConnectUserInfo(String orcid, Person person, String path) {
        this.id = path+"/"+orcid;
        this.sub = orcid;
        if (person.getName() != null){
            if (person.getName().getCreditName() != null){
                this.name = person.getName().getCreditName().getContent();
            }
            if (person.getName().getFamilyName() != null){
                this.family_name = person.getName().getFamilyName().getContent();
            }
            if (person.getName().getGivenNames() != null){
                this.given_name = person.getName().getGivenNames().getContent();
            }            
        }
    }
    public OpenIDConnectUserInfo(String orcid, PersonalDetails person, String path) {
        this.id = path+"/"+orcid;
        this.sub = orcid;
        if (person.getName() != null){
            if (person.getName().getCreditName() != null){
                this.name = person.getName().getCreditName().getContent();
            }
            if (person.getName().getFamilyName() != null){
                this.family_name = person.getName().getFamilyName().getContent();
            }
            if (person.getName().getGivenNames() != null){
                this.given_name = person.getName().getGivenNames().getContent();
            }            
        }
    }
    
    public String getName() {
        return name;
    }
    public String getSub() {
        return sub;
    }
    public String getFamily_name() {
        return family_name;
    }
    public String getGiven_name() {
        return given_name;
    }
    
}
