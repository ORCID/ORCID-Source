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

@FieldMatch.List( { @FieldMatch(first = "password", second = "retypedPassword", message = "password_one_time_reset.password_donesnt_match") })
public class PasswordTypeAndConfirmForm {

    private String password;
    private String retypedPassword;

    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = "password_one_time_reset.password_regex_error")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }
}
