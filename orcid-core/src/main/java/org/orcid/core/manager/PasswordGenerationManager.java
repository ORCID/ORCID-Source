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
package org.orcid.core.manager;

/**
 * Interface to generate a password (can be used for reset or simply as a way to create a user password without them providing one at registration)
 * Should return a password that conforms to the constants defined by  
 * @See 
 * @author jamesb
 *
 */
public interface PasswordGenerationManager {

    char[] createNewPassword();

}
