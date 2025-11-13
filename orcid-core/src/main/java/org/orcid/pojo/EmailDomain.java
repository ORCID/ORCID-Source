package org.orcid.pojo;

import org.orcid.persistence.jpa.entities.EmailDomainEntity;

import java.io.Serializable;

public class EmailDomain implements Serializable {
    private String emailDomain;

    private EmailDomainEntity.DomainCategory category;

    private String rorId;

    public String getEmailDomain() {
        return this.emailDomain;
    }

    public void setEmailDomain(String emailDomain) {
        this.emailDomain = emailDomain;
    }

    public EmailDomainEntity.DomainCategory getCategory() {
        return this.category;
    }

    public void setCategory(EmailDomainEntity.DomainCategory category) {
        this.category = category;
    }

    public String getRorId() {
        return this.rorId;
    }

    public void setRorId(String rorId) {
        this.rorId = rorId;
    }
}
