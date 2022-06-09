package org.orcid.internal.util;

import org.springframework.http.HttpStatus;

import javax.xml.bind.annotation.XmlElement;

public class EmailResponse {

    @XmlElement(name = "orcid")
    private String orcid;
    @XmlElement(name = "email")
    private String email;
    @XmlElement(name = "status")
    private HttpStatus status;

    public EmailResponse(String orcid, String email, HttpStatus status) {
        this.orcid = orcid;
        this.email = email;
        this.status = status;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }
}
