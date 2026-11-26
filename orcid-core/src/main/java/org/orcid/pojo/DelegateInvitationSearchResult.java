package org.orcid.pojo;

public class DelegateInvitationSearchResult {

    private String status;
    private String lookupType;
    private String emailAddress;
    private String orcid;
    private String publicName;
    private String highlightedAffiliation;
    private String publicRecordUrl;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLookupType() {
        return lookupType;
    }

    public void setLookupType(String lookupType) {
        this.lookupType = lookupType;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getPublicName() {
        return publicName;
    }

    public void setPublicName(String publicName) {
        this.publicName = publicName;
    }

    public String getHighlightedAffiliation() {
        return highlightedAffiliation;
    }

    public void setHighlightedAffiliation(String highlightedAffiliation) {
        this.highlightedAffiliation = highlightedAffiliation;
    }

    public String getPublicRecordUrl() {
        return publicRecordUrl;
    }

    public void setPublicRecordUrl(String publicRecordUrl) {
        this.publicRecordUrl = publicRecordUrl;
    }
}