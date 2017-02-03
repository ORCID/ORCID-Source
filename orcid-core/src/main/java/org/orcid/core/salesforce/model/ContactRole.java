/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.salesforce.model;

/**
 * 
 * @author Will Simpson
 *
 */
public class ContactRole {
    private String accountId;
    private String contactId;
    private ContactRoleType role;

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

    public ContactRoleType getRole() {
        return role;
    }

    public void setRole(ContactRoleType role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "ContactRole [accountId=" + accountId + ", contactId=" + contactId + ", role=" + role + "]";
    }

}
