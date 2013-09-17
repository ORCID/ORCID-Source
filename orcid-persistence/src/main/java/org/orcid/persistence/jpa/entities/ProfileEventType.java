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
public enum ProfileEventType {

    CLAIM_REMINDER_SENT, 
    EMAIL_VERIFY_CROSSREF_MARKETING_FAIL, EMAIL_VERIFY_CROSSREF_MARKETING_SENT, EMAIL_VERIFY_CROSSREF_MARKETING_SKIPPED, 
    //Indicates that the account is deprecated
    PROFILE_DEPRECATED, 
    //Indicates that an account has been deprecated and you are the primary account
    PROFILE_DEPRECATION;    
}
