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

import org.orcid.frontend.web.forms.validate.FieldMatch;
import org.orcid.frontend.web.forms.validate.TextPattern;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.pojo.ajaxForm.Text;

@FieldMatch.List( { @FieldMatch(first = "password", second = "retypedPassword", message = "password_one_time_reset.password_donesnt_match") })
public class PasswordTypeAndConfirmForm {

    private Text password;
    private Text retypedPassword;

    @TextPattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = "password_one_time_reset.password_regex_error")
    public Text getPassword() {
        return password;
    }

    public void setPassword(Text password) {
        this.password = password;
    }

    public Text getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(Text retypedPassword) {
        this.retypedPassword = retypedPassword;
    }
}
