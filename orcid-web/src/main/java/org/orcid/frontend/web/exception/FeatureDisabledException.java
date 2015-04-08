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
package org.orcid.frontend.web.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * 
 * @author Will Simpson
 *
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FeatureDisabledException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FeatureDisabledException() {
    }

    public FeatureDisabledException(String message) {
        super(message);
    }

    public FeatureDisabledException(Throwable cause) {
        super(cause);
    }

    public FeatureDisabledException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeatureDisabledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
