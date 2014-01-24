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
 * This exception will be thrown when the scope provided by the user doesn't match any of the scopes
 * available in org.orcid.jaxb.model.message.ScopePathType enum
 * 
 *  @author Angel Montenegro(amontenegro)
 */
public class OrcidInvalidScopeException extends OrcidApiException {
	
    private static final long serialVersionUID = 1L;

    public OrcidInvalidScopeException(String message) {
        super(message, Response.Status.CONFLICT);
    }

    public OrcidInvalidScopeException(String message, Throwable t) {
        super(message, Response.Status.CONFLICT, t);
    }
}
