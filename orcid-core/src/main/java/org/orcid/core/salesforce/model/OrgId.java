package org.orcid.core.salesforce.model;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrgId {
    private String id;
    private String accountId;
    private String orgIdValue;
    private String orgIdType;
    private Boolean inactive;
    private Boolean primaryIdForType;
    private String notes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getOrgIdValue() {
        return orgIdValue;
    }

    public void setOrgIdValue(String orgIdValue) {
        this.orgIdValue = orgIdValue;
    }

    public String getOrgIdType() {
        return orgIdType;
    }

    public void setOrgIdType(String orgIdType) {
        this.orgIdType = orgIdType;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public Boolean getPrimaryIdForType() {
        return primaryIdForType;
    }

    public void setPrimaryIdForType(Boolean primaryIdForType) {
        this.primaryIdForType = primaryIdForType;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "OrgId [id=" + id + ", accountId=" + accountId + ", orgIdValue=" + orgIdValue + ", orgIdType=" + orgIdType + ", inactive=" + inactive
                + ", primaryIdForType=" + primaryIdForType + ", notes=" + notes + "]";
    }

}
