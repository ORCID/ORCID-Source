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

import org.apache.commons.lang.StringUtils;
import org.orcid.frontend.web.forms.validate.FieldMatch;
import org.orcid.frontend.web.forms.validate.IntegerStringCrossField;
import org.orcid.frontend.web.forms.validate.TextPattern;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.pojo.ajaxForm.Text;

@IntegerStringCrossField(indexToIgnoreValidation = 0, theFieldToIgnoreValidation = "securityQuestionAnswer", theFieldToIndex = "securityQuestionId", message = "password_one_time_reset_optional_security_questions.answer_not_null")
@FieldMatch.List( { @FieldMatch(first = "password", second = "retypedPassword", message = "password_one_time_reset.password_donesnt_match") })
public class OneTimeResetPasswordForm {

    private PasswordTypeAndConfirmForm passwordTypeAndConfirmForm;
    private ChangeSecurityQuestionForm changeSecurityQuestionForm;

    public ChangeSecurityQuestionForm getChangeSecurityQuestionForm() {
        return changeSecurityQuestionForm;
    }
 
    public OneTimeResetPasswordForm() {
        passwordTypeAndConfirmForm = new PasswordTypeAndConfirmForm();
        changeSecurityQuestionForm = new ChangeSecurityQuestionForm();
    }

    public PasswordTypeAndConfirmForm getForm() {
        return passwordTypeAndConfirmForm;
    }

    @TextPattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = "password_one_time_reset.password_regex_error")
    public Text getPassword() {
        return passwordTypeAndConfirmForm.getPassword();
    }

    public void setPassword(Text password) {
        passwordTypeAndConfirmForm.setPassword(password);
    }

    public Text getRetypedPassword() {
        return passwordTypeAndConfirmForm.getRetypedPassword();
    }

    public void setRetypedPassword(Text retypedPassword) {
        passwordTypeAndConfirmForm.setRetypedPassword(retypedPassword);
    }

    public int getSecurityQuestionId() {
        return changeSecurityQuestionForm.getSecurityQuestionId();
    }

    public void setSecurityQuestionId(int securityQuestionId) {
        this.changeSecurityQuestionForm.setSecurityQuestionId(securityQuestionId);
    }

    public String getSecurityQuestionAnswer() {
        return changeSecurityQuestionForm.getSecurityQuestionAnswer();
    }

    public void setSecurityQuestionAnswer(String securityAnswer) {
        this.changeSecurityQuestionForm.setSecurityQuestionAnswer(securityAnswer);
    }

    public boolean isSecurityDetailsPopulated() {
        return changeSecurityQuestionForm.getSecurityQuestionId() != null && changeSecurityQuestionForm.getSecurityQuestionId() != 0
                && StringUtils.isNotBlank(changeSecurityQuestionForm.getSecurityQuestionAnswer());
    }

}
