package org.orcid.core.exception;

import org.orcid.core.exception.ApplicationException;

public class DeactivatedException extends ApplicationException {
    private static final long serialVersionUID = 5900106949403162953L;
    private String orcid;

    public DeactivatedException() {
        
    }
    
    public DeactivatedException(String msg) {
        super(msg);
    }

    public DeactivatedException(String msg, String orcid) {
        super(msg);
        this.orcid=orcid;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
}