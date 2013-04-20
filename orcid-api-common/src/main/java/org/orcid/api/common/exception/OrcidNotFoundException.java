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
package org.orcid.api.common.exception;

import javax.ws.rs.core.Response;

/**
 * Copyright 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 01/03/2012
 */
public class OrcidNotFoundException extends OrcidApiException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OrcidNotFoundException(String message) {
        super(message, Response.Status.NOT_FOUND);
    }

    public OrcidNotFoundException(String message, Throwable t) {
        super(message, Response.Status.NOT_FOUND, t);
    }
}
