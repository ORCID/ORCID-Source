package org.orcid.pojo;

public class DelegateInvitationSearchRequest {

    private String lookupType;
    private String value;

    public String getLookupType() {
        return lookupType;
    }

    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}