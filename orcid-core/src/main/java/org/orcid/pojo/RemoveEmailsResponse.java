package org.orcid.pojo;

import java.util.List;

public class RemoveEmailsResponse {
    private final String message;
    private final List<String> remainingEmails;

    public RemoveEmailsResponse(String message, List<String> remainingEmails) {
        this.message = message;
        this.remainingEmails = remainingEmails;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getRemainingEmails() {
        return remainingEmails;
    }
}