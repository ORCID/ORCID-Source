package org.orcid.pojo;

import java.util.Map;

public class Local {

    private String locale;

    private Map<String, String> messages;

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public Map<String, String> getMessages() {
        return messages;
    }

    public void setMessages(Map<String, String> messages) {
        this.messages = messages;
    }

}
