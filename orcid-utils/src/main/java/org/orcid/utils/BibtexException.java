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
package org.orcid.utils;

public class BibtexException extends Exception {

    private static final long serialVersionUID = 1L;

    public BibtexException() {
    }

    public BibtexException(String message) {
        super(message);
    }

    public BibtexException(Throwable cause) {
        super(cause);
    }

    public BibtexException(String message, Throwable cause) {
        super(message, cause);
    }

}
