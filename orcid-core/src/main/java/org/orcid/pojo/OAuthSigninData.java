package org.orcid.pojo;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class OAuthSigninData {

    private String providerId;

    private String accountId;

    private boolean unsupportedInstitution;

    private String institutionContactEmail;

    private boolean headerCheckFailed;

    private String linkType;

    private String firstName;

    private String lastName;    

    private String email;
    
    private String providerIdEncoded;
    
    private String accountIdEncoded;
    
    private String firstNameEncoded;
    
    private String lastNameEncoded; 
    
    private String emailEncoded;

    public String getProviderId() {
        return providerId;
    }
    
    public void setProviderId(String providerId) {
        this.providerId = providerId;
        try {
            setProviderIdEncoded(providerId != null ? URLEncoder.encode(providerId, "UTF-8") : "");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
        try {
            setAccountIdEncoded(accountId != null ? URLEncoder.encode(accountId, "UTF-8") : "");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isUnsupportedInstitution() {
        return unsupportedInstitution;
    }

    public void setUnsupportedInstitution(boolean unsupportedInstitution) {
        this.unsupportedInstitution = unsupportedInstitution;
    }

    public String getInstitutionContactEmail() {
        return institutionContactEmail;
    }

    public void setInstitutionContactEmail(String institutionContactEmail) {
        this.institutionContactEmail = institutionContactEmail;
    }

    public boolean isHeaderCheckFailed() {
        return headerCheckFailed;
    }

    public void setHeaderCheckFailed(boolean headerCheckFailed) {
        this.headerCheckFailed = headerCheckFailed;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
        try {
            setFirstNameEncoded(firstName != null ? URLEncoder.encode(firstName, "UTF-8") : "");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
        try {
            setLastNameEncoded(lastName != null ? URLEncoder.encode(lastName, "UTF-8") : "");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        try {
            setEmailEncoded(email != null ? URLEncoder.encode(email, "UTF-8") : "");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String getProviderIdEncoded() {
        return providerIdEncoded;
    }

    public void setProviderIdEncoded(String providerIdEncoded) {
        this.providerIdEncoded = providerIdEncoded;
    }

    public String getAccountIdEncoded() {
        return accountIdEncoded;
    }

    public void setAccountIdEncoded(String accountIdEncoded) {
        this.accountIdEncoded = accountIdEncoded;
    }

    public String getFirstNameEncoded() {
        return firstNameEncoded;
    }

    public void setFirstNameEncoded(String firstNameEncoded) {
        this.firstNameEncoded = firstNameEncoded;
    }

    public String getLastNameEncoded() {
        return lastNameEncoded;
    }

    public void setLastNameEncoded(String lastNameEncoded) {
        this.lastNameEncoded = lastNameEncoded;
    }

    public String getEmailEncoded() {
        return emailEncoded;
    }

    public void setEmailEncoded(String emailEncoded) {
        this.emailEncoded = emailEncoded;
    }

}
