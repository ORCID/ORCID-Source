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

import java.util.Map;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class OrcidNoResultException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OrcidNoResultException() {
    }

    public OrcidNoResultException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrcidNoResultException(String message) {
        super(message);
    }

    public OrcidNoResultException(Throwable cause) {
        super(cause);
    }

    public OrcidNoResultException(Map<String, String> params) {
        super(params);
    }
}
