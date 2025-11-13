package org.orcid.pojo.ajaxForm;

import org.orcid.core.togglz.Features;
import org.orcid.persistence.jpa.entities.ProfileEmailDomainEntity;

import java.util.ArrayList;
import java.util.List;

public class Emails implements ErrorsInterface {
    private List<Email> emails = null;
    private List<ProfileEmailDomain> emailDomains = null;
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    public static Emails valueOf(org.orcid.jaxb.model.v3.release.record.Emails e, List<ProfileEmailDomainEntity> domains) {
        Emails emails = new Emails();
        if (e != null && !e.getEmails().isEmpty()) {
            emails.setEmails(new ArrayList<Email>());
            for (org.orcid.jaxb.model.v3.release.record.Email v3Email : e.getEmails()) {
                emails.getEmails().add(Email.valueOf(v3Email));
            }
        }
        if (domains != null && !domains.isEmpty() && Features.EMAIL_DOMAINS.isActive()) {
            emails.setEmailDomains(new ArrayList<ProfileEmailDomain>());
            for (ProfileEmailDomainEntity domain : domains) {
                emails.getEmailDomains().add(ProfileEmailDomain.valueOf(domain));
            }
        }
        return emails;
    }
    
    public org.orcid.jaxb.model.v3.release.record.Emails toV3Emails() {
        org.orcid.jaxb.model.v3.release.record.Emails v3Emails = new org.orcid.jaxb.model.v3.release.record.Emails();
        if (emails != null && !emails.isEmpty()) {
            for(Email email : emails) {
                v3Emails.getEmails().add(email.toV3Email());
            }
        }
        return v3Emails;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<Email> getEmails() {
        return emails;
    }

    public void setEmails(List<Email> emails) {
        this.emails = emails;
    }

    public List<ProfileEmailDomain> getEmailDomains() { return emailDomains; }

    public void setEmailDomains(List<ProfileEmailDomain> emailDomains) { this.emailDomains = emailDomains;}
}
