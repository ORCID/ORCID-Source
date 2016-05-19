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
package org.orcid.core.security.aop;

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
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
}