package org.orcid.persistence.jpa.entities;

public enum EventType {
    SIGN_IN("Sign-In"),
    NEW_REGISTRATION("New-Registration"),
    AUTHORIZE("Authorize"),
    AUTHORIZE_DENY("Authorize-Deny"),
    REAUTHORIZE("Reauthorize"),
    PUBLIC_PAGE("Public-Page"),
    PAPI("Public-API");

    private final String value;

    EventType(String v) {
        value = v;
    }

    public String getValue() {
        return value;
    }
}