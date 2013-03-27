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
import org.orcid.frontend.web.forms.validate.FieldMatch;
import org.orcid.password.constants.OrcidPasswordConstants;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Class to wrap a password options form on the Manage Profile Screen
 * 
 * @author jamesb
 * 
 */
@FieldMatch.List( { @FieldMatch(first = "passwordOptionsForm.password", second = "passwordOptionsForm.retypedPassword", message = "The password and confirm password field must match") })
public class ManagePasswordOptionsForm {

    private PasswordOptionsForm passwordOptionsForm;

    public ManagePasswordOptionsForm() {
        super();
        passwordOptionsForm = new PasswordOptionsForm();
    }

    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)
    public String getPassword() {
        return this.getPasswordOptionsForm().getPassword();
    }

    public void setPassword(String password) {
        this.passwordOptionsForm.setPassword(password);
    }

    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = "The confirm password field is invalid.")
    public String getRetypedPassword() {
        return this.getPasswordOptionsForm().getRetypedPassword();
    }

    public void setRetypedPassword(String retypedPassword) {
        this.passwordOptionsForm.setRetypedPassword(retypedPassword);
    }

    @NotNull(message = "Please select a security question.")
    @Min(value = 1, message = "Please select a security question.")
    public Integer getSecurityQuestionId() {
        return this.passwordOptionsForm.getSecurityQuestionId();
    }

    public void setSecurityQuestionId(Integer securityQuestionId) {
        this.passwordOptionsForm.setSecurityQuestionId(securityQuestionId);
    }

    @NotBlank(message = "Please provide an answer to your security question.")
    public String getSecurityQuestionAnswer() {
        return this.passwordOptionsForm.getSecurityQuestionAnswer();
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.passwordOptionsForm.setSecurityQuestionAnswer(securityQuestionAnswer);
    }

    @Pattern(regexp = "\\d{4}", message = "Please enter a 4 digit verification number")
    @NotNull(message = "Please enter a 4 digit verification number")
    public String getVerificationNumber() {
        return this.passwordOptionsForm.getVerificationNumber();
    }

    public void setVerificationNumber(String verificationNumber) {
        passwordOptionsForm.setVerificationNumber(verificationNumber);
    }

    public PasswordOptionsForm getPasswordOptionsForm() {
        return passwordOptionsForm;
    }

    public void setPasswordOptionsForm(PasswordOptionsForm passwordOptionsForm) {
        this.passwordOptionsForm = passwordOptionsForm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ManagePasswordOptionsForm that = (ManagePasswordOptionsForm) o;

        if (!passwordOptionsForm.equals(that.passwordOptionsForm)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return passwordOptionsForm.hashCode();
    }

}
