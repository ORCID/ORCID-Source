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

import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * 
 * @author Angel Montenegro
 *
 */
public class OrcidDeprecatedException extends OrcidApiException {
    private static final long serialVersionUID = 1L;
	
    public OrcidDeprecatedException(String msg) {
        super(msg, Status.MOVED_PERMANENTLY.getStatusCode());
    }

    public OrcidDeprecatedException(String msg, Throwable t) {
        super(msg, Status.MOVED_PERMANENTLY.getStatusCode(), t);
    }
}
