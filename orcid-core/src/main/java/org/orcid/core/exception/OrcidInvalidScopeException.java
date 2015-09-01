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

/**
 * This exception will be thrown when the scope provided by the user doesn't
 * match any of the scopes available in
 * org.orcid.jaxb.model.message.ScopePathType enum
 * 
 * @author Angel Montenegro(amontenegro)
 */
public class OrcidInvalidScopeException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OrcidInvalidScopeException() {
        super();
    }

    public OrcidInvalidScopeException(String message) {
        super(message);
    }
}
