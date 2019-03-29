package org.orcid.core.manager.v3;

import java.io.Serializable;

/**
 * 
 * @author Will Simpson
 * 
 */
public class EmailMessage implements Serializable {

    private static final long serialVersionUID = 1L;
    private String to;
    private String from;
    private String subject;
    private String bodyText;
    private String bodyHtml;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

}
