package org.orcid.utils.sms;

public class SmsMessage {

    private final String to;
    private final String body;

    public SmsMessage(String to, String body) {
        this.to = to;
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public String getBody() {
        return body;
    }
}
