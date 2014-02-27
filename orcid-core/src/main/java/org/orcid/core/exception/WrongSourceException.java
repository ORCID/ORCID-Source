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
public class WrongSourceException extends IllegalArgumentException {

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MESSAGE = "We have detected changes to activities you are not the source of. This may be do to a out of date list. Please re-get the activities list and repost with only changes to activities your client is the source of.";

    public WrongSourceException() {
        super(DEFAULT_MESSAGE);
    }

    public WrongSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongSourceException(String message) {
        super(message);
    }

    public WrongSourceException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }

}
