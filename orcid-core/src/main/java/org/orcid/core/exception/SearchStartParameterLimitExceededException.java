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

import javax.ws.rs.core.Response;

public class SearchStartParameterLimitExceededException extends OrcidApiException {

    private static final long serialVersionUID = 7814378059339926519L;
    
    public SearchStartParameterLimitExceededException(String message) {
        super(message, Response.Status.BAD_REQUEST);
    }

    public SearchStartParameterLimitExceededException(String message, Throwable t) {
        super(message, Response.Status.BAD_REQUEST, t);
    }
    
}
