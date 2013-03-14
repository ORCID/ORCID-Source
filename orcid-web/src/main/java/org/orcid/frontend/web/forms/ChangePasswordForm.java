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

import javax.validation.constraints.Pattern;

import org.orcid.frontend.web.forms.validate.FieldMatch;
import org.orcid.password.constants.OrcidPasswordConstants;

@FieldMatch.List( { @FieldMatch(first = "password", second = "retypedPassword", message = "New password values don’t match. Please try again") })
public class ChangePasswordForm {

    private String password;

    private String retypedPassword;

    private String oldPassword;

    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)
    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }

    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)
    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

}
