package org.orcid.pojo;

public class UnsubscribeData {
   
    private String emailAddress;
    
    private EmailFrequencyOptions emailFrequencyOptions;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public EmailFrequencyOptions getEmailFrequencyOptions() {
        return emailFrequencyOptions;
    }

    public void setEmailFrequencyOptions(EmailFrequencyOptions emailFrequencyOptions) {
        this.emailFrequencyOptions = emailFrequencyOptions;
    }
    
}
