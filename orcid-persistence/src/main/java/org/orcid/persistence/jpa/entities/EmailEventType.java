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
package org.orcid.persistence.jpa.entities;

/**
 * 
 * @author Will Simpson
 * 
 */
public enum EmailEventType {
    
    VERIFY_EMAIL_7_DAYS_SENT,
    VERIFY_EMAIL_7_DAYS_SENT_SKIPPED /* we are going to skip notifying email address that where already in the system before launching this */
    ;
}
