package org.orcid.pojo;

import java.util.List;

public class RemoveEmailsRequest {
    private String orcid;
    private List<String> emailsToRemove;

    public RemoveEmailsRequest() { }

    public RemoveEmailsRequest(
            String orcid,
            List<String> emailsToRemove
    ) {
        this.orcid = orcid;
        this.emailsToRemove = emailsToRemove;
    }

    public String getOrcid() {
        return orcid;
    }

    public List<String> getEmailsToRemove() {
        return emailsToRemove;
    }

}