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
public class OrcidSearchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrcidSearchException() {
    }

    public OrcidSearchException(String message) {
        super(message);
    }

    public OrcidSearchException(Throwable cause) {
        super(cause);
    }

    public OrcidSearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrcidSearchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
