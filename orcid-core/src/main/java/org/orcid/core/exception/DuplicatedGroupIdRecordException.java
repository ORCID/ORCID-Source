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
 * 
 * @author Shobhit Tyagi
 * 
 */
public class DuplicatedGroupIdRecordException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public DuplicatedGroupIdRecordException() {
    }

    public DuplicatedGroupIdRecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedGroupIdRecordException(String message) {
        super(message);
    }

    public DuplicatedGroupIdRecordException(Throwable cause) {
        super(cause);
    }

}
