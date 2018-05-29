package org.orcid.core.salesforce.model;

import java.io.Serializable;

/**
 * 
 * @author Will Simpson
 *
 */
public class ContactRole implements Serializable {

    private static final long serialVersionUID = 1L;

    // The SalesForce Id of the role object itself
    private String id;
    private String accountId;
    private String contactId;
    private Boolean isVotingContact;
    private Boolean isCurrent;
    private ContactRoleType roleType;

    public ContactRole() {
    }

    public ContactRole(ContactRoleType role) {
        this.roleType = role;
    }

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

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public Boolean isVotingContact() {
        return isVotingContact;
    }

    public void setVotingContact(Boolean isVotingContact) {
        this.isVotingContact = isVotingContact;
    }

    public Boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(Boolean isCurrent) {
        this.isCurrent = isCurrent;
    }

    public ContactRoleType getRoleType() {
        return roleType;
    }

    public void setRoleType(ContactRoleType roleType) {
        this.roleType = roleType;
    }

    @Override
    public String toString() {
        return "ContactRole [id=" + id + ", accountId=" + accountId + ", contactId=" + contactId + ", isVotingContact=" + isVotingContact + ", isCurrent=" + isCurrent
                + ", roleType=" + roleType + "]";
    }

}
