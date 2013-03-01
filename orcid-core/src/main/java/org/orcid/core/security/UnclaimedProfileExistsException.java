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

import org.springframework.security.authentication.BadCredentialsException;

/**
 * 
 * @author Will Simpson
 *
 */
public class UnclaimedProfileExistsException extends BadCredentialsException {

    private static final long serialVersionUID = 1L;

    public UnclaimedProfileExistsException(String msg) {
        super(msg);
    }

    public UnclaimedProfileExistsException(String msg, Throwable t) {
        super(msg, t);
    }

}
