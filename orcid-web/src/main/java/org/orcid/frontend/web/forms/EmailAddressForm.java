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
package org.orcid.frontend.web.forms;

import org.hibernate.validator.constraints.NotBlank;

/**
 * 
 * @author jamesb
 * 
 */
public class EmailAddressForm {

    private String userEmailAddress;

    @NotBlank(message = "Please enter your email address")
    @org.hibernate.validator.constraints.Email
    public String getUserEmailAddress() {
        if (userEmailAddress!= null) 
            return userEmailAddress.trim();
        return null;
    }

    public void setUserEmailAddress(String userEmailAddress) {
        this.userEmailAddress = userEmailAddress;
    }

}
