package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UnsubscribeData {
   
    private String emailAddress;
    
    private Map<String, String> emailFrequencies;
    
    private List<String> emailFrequencyKeys;

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Map<String, String> getEmailFrequencies() {
        return emailFrequencies;
    }
    
    public List<String> getEmailFrequencyKeys() {
        return emailFrequencyKeys;
    }

    public void setEmailFrequencyKeys(List<String> emailFrequencyKeys) {
        this.emailFrequencyKeys = emailFrequencyKeys;
    }

    public void setEmailFrequencies(Map<String, String> emailFrequencies) {
        this.emailFrequencies = emailFrequencies;
        setEmailFrequencyKeys(new ArrayList<>(emailFrequencies.keySet()));
    }
    
}
