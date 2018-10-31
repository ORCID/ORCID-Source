package org.orcid.pojo.ajaxForm;

import java.util.ArrayList;
import java.util.List;

public class Emails implements ErrorsInterface {
    private List<Email> emails = null;
    @SuppressWarnings("unused")
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    public static Emails valueOf(org.orcid.jaxb.model.v3.rc2.record.Emails e) {
        Emails emails = new Emails();
        if (e != null && !e.getEmails().isEmpty()) {
            emails.setEmails(new ArrayList<Email>());
            for (org.orcid.jaxb.model.v3.rc2.record.Email v3Email : e.getEmails()) {
                emails.getEmails().add(Email.valueOf(v3Email));
            }
        }
        return emails;
    }
    
    public org.orcid.jaxb.model.v3.rc2.record.Emails toV3Emails() {
        org.orcid.jaxb.model.v3.rc2.record.Emails v3Emails = new org.orcid.jaxb.model.v3.rc2.record.Emails();
        if(emails != null && !emails.isEmpty()) {
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
}
