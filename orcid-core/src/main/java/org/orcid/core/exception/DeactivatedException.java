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