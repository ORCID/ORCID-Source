package org.orcid.pojo;

import java.util.List;
import java.util.Map;

public class EmailFrequencyOptions {
    
    private Map<String, String> emailFrequencies;
    
    private List<String> emailFrequencyKeys;

    public Map<String, String> getEmailFrequencies() {
        return emailFrequencies;
    }

    public void setEmailFrequencies(Map<String, String> emailFrequencies) {
        this.emailFrequencies = emailFrequencies;
    }

    public List<String> getEmailFrequencyKeys() {
        return emailFrequencyKeys;
    }

    public void setEmailFrequencyKeys(List<String> emailFrequencyKeys) {
        this.emailFrequencyKeys = emailFrequencyKeys;
    }

}
