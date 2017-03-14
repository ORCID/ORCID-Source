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
package org.orcid.persistence.jpa.entities;

/**
 * 
 * @author Will Simpson
 * 
 */
public enum ProfileEventType {

    CLAIM_REMINDER_SENT, 
    EMAIL_VERIFY_CROSSREF_MARKETING_FAIL, EMAIL_VERIFY_CROSSREF_MARKETING_SENT, EMAIL_VERIFY_CROSSREF_MARKETING_SKIPPED, 
    POLICY_UPDATE_2014_03_SENT, POLICY_UPDATE_2014_03_FAIL, POLICY_UPDATE_2014_03_SKIPPED,
    SERVICE_ANNOUNCEMENT_SENT_1_FOR_2015, SERVICE_ANNOUNCEMENT_FAIL_1_FOR_2015, SERVICE_ANNOUNCEMENT_SKIPPED_1_FOR_2015, 
    //Indicates that the account is deprecated
    PROFILE_DEPRECATED, 
    //Indicates that an account has been deprecated and you are the primary account
    PROFILE_DEPRECATION,
    //Indicates that the delegation process of an account was started by an admin
    ADMIN_PROFILE_DELEGATION_REQUEST,
    
    
    // Verified_Required_Announcement_2017
    VERIFIED_REQUIRED_SKIPPED_2017, VERIFIED_REQUIRED_HAS_VALIDATED_2017, VERIFIED_REQUIRED_SENT_2017, VERIFIED_REQUIRED_FAIL_2017;
}
