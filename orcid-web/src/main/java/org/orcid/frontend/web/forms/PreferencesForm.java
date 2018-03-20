package org.orcid.frontend.web.forms;

import java.io.Serializable;

/**
 * @author Will Simpson
 */
public class PreferencesForm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String orcid;
    private String sendEmailFrequencyDays;
    
    public PreferencesForm() {
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getSendEmailFrequencyDays() {
        return sendEmailFrequencyDays;
    }

    public void setSendEmailFrequencyDays(String sendEmailFrequencyDays) {
        this.sendEmailFrequencyDays = sendEmailFrequencyDays;
    }    
}
