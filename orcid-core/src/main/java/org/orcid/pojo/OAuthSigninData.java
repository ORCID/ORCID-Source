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

    public String getProviderId() {
        return providerId;
    }
    
    public String getProviderIdEncoded() throws UnsupportedEncodingException {
        return providerId != null ? URLEncoder.encode(providerId, "UTF-8") : "";
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getAccountIdEncoded() throws UnsupportedEncodingException {
        return accountId != null ? URLEncoder.encode(accountId, "UTF-8") : "";
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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
    
    public String getFirstEncoded() throws UnsupportedEncodingException {
        return firstName != null ? URLEncoder.encode(firstName, "UTF-8") : "";
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    
    public String getLastNameEncoded() throws UnsupportedEncodingException {
        return lastName != null ? URLEncoder.encode(lastName, "UTF-8") : "";
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getEmailEncoded() throws UnsupportedEncodingException {
        return URLEncoder.encode(email, "UTF-8");
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

}
