package org.orcid.core.security.aop;

import java.util.HashMap;
import java.util.Map;

import org.orcid.core.exception.ApplicationException;

/**
 * @author Angel Montenegro
 * */
public class LockedException extends ApplicationException {
    private static final long serialVersionUID = -6900432299998784418L;
    private String orcid;

    public LockedException() {
        
    }
    
    public LockedException(String msg) {
        super(msg);
    }

    public LockedException(String msg, String orcid) {
        super(msg);
        this.orcid=orcid;        
        Map<String, String> params = new HashMap<String, String>();
        params.put("orcid", orcid);
        this.params = params;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
        Map<String, String> params = new HashMap<String, String>();
        params.put("orcid", orcid);
        this.params = params;
    }
}