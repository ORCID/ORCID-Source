/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
