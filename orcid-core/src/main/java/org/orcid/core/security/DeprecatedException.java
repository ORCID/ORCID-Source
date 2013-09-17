/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.security;

import org.orcid.core.exception.ApplicationException;

/**
 * 
 * @author Angel Montenegro
 *
 */
public class DeprecatedException extends ApplicationException {
	private static final long serialVersionUID = 1L;
	private String primary; 
	private String deprecated;
	
    public DeprecatedException(String msg) {
        super(msg);
    }

    public DeprecatedException(String msg, Throwable t) {
        super(msg, t);
    }
    
    public DeprecatedException(String msg, String primary, String deprecated) {       
        super(msg);
        this.primary = primary;
        this.deprecated = deprecated;
    }

    public DeprecatedException(String msg, Throwable t, String primary, String deprecated) {       
        super(msg, t);
        this.primary = primary;
        this.deprecated = deprecated;
    }
    
    public String getPrimary() {
        return primary;
    }

    public void setPrimary(String primary) {
        this.primary = primary;
    }

    public String getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(String deprecated) {
        this.deprecated = deprecated;
    }        
}
