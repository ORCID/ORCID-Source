package org.orcid.pojo;

import java.util.Date;

public class ProfileDeprecationRequest {
    private ProfileDetails deprecatedAccount;
    private ProfileDetails primaryAccount;
    private Date deprecatedDate;    
    
    public ProfileDeprecationRequest(){
        this.deprecatedAccount = new ProfileDetails();
        this.primaryAccount = new ProfileDetails();
    }
       
    public ProfileDetails getDeprecatedAccount() {
        return deprecatedAccount;
    }
    public void setDeprecatedAccount(ProfileDetails deprecatedAccount) {
        this.deprecatedAccount = deprecatedAccount;
    }
    
    public ProfileDetails getPrimaryAccount() {
        return primaryAccount;
    }
    public void setPrimaryAccount(ProfileDetails primaryAccount) {
        this.primaryAccount = primaryAccount;
    }
    
    public Date getDeprecatedDate() {
        return deprecatedDate;
    }
    public void setDeprecatedDate(Date deprecatedDate) {
        this.deprecatedDate = deprecatedDate;
    }           
}
