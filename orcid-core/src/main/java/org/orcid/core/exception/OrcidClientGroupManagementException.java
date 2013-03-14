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
package org.orcid.core.exception;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidClientGroupManagementException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OrcidClientGroupManagementException() {
    }

    public OrcidClientGroupManagementException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrcidClientGroupManagementException(String message) {
        super(message);
    }

    public OrcidClientGroupManagementException(Throwable cause) {
        super(cause);
    }

}
