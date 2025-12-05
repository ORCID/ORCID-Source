package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

public class EmailListChange {
    private List<String> verifiedEmails = new ArrayList<>();

    private List<org.orcid.jaxb.model.v3.release.record.Email> removedEmails = new ArrayList<>();

    private List<org.orcid.jaxb.model.v3.release.record.Email> addedEmails = new ArrayList<>();

    public List<String> getVerifiedEmails() {
        return verifiedEmails;
    }

    public void setVerifiedEmails(List<String> verifiedEmails) {
        this.verifiedEmails = verifiedEmails;
    }

    public List<org.orcid.jaxb.model.v3.release.record.Email> getRemovedEmails() {
        return removedEmails;
    }

    public void setRemovedEmails(List<org.orcid.jaxb.model.v3.release.record.Email> removedEmails) {
        this.removedEmails = removedEmails;
    }

    public List<org.orcid.jaxb.model.v3.release.record.Email> getAddedEmails() {
        return addedEmails;
    }

    public void setAddedEmails(List<org.orcid.jaxb.model.v3.release.record.Email> addedEmails) {
        this.addedEmails = addedEmails;
    }
}
