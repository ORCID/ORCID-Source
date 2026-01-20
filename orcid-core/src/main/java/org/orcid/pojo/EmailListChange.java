package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;


// Used for populating security email templates when changes are made to a person's email list
public class EmailListChange {
    // Used when emails are removed using the UI
    private List<org.orcid.jaxb.model.v3.release.record.Email> removedEmails = new ArrayList<>();

    // Used when new emails are added to a record using the UI
    private List<org.orcid.pojo.ajaxForm.Email> addedEmails = new ArrayList<>();

    public List<org.orcid.jaxb.model.v3.release.record.Email> getRemovedEmails() {
        return removedEmails;
    }

    public void setRemovedEmails(List<org.orcid.jaxb.model.v3.release.record.Email> removedEmails) {
        this.removedEmails = removedEmails;
    }

    public List<org.orcid.pojo.ajaxForm.Email> getAddedEmails() {
        return addedEmails;
    }

    public void setAddedEmails(List<org.orcid.pojo.ajaxForm.Email> addedEmails) {
        this.addedEmails = addedEmails;
    }
}
