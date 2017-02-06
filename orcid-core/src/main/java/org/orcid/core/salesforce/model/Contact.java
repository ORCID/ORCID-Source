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

import java.io.Serializable;

/**
 * 
 * @author Will Simpson
 *
 */
public class Contact implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String accountId;
    private String name;
    private String email;
    private String role;
    private String orcid;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isMainContact() {
        return ContactRoleType.MAIN_CONTACT.value().equals(role);
    }

    public boolean isTechnicalContact() {
        return ContactRoleType.TECHNICAL_CONTACT.value().equals(role);
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Override
    public String toString() {
        return "Contact [id=" + id + ", accountId=" + accountId + ", name=" + name + ", email=" + email + ", role=" + role + ", orcid=" + orcid + "]";
    }

}
