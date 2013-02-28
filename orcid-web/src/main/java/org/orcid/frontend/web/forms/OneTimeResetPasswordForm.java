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

import org.apache.commons.lang.StringUtils;
import org.orcid.frontend.web.forms.validate.FieldMatch;
import org.orcid.frontend.web.forms.validate.IntegerStringCrossField;
import org.orcid.password.constants.OrcidPasswordConstants;

@IntegerStringCrossField(indexToIgnoreValidation = 0,
theFieldToIgnoreValidation = "securityQuestionAnswer",
theFieldToIndex="securityQuestionId",
message = "Please provide an answer to your challenge question.")
@FieldMatch.List({ @FieldMatch(first = "password", second = "retypedPassword", message = "Your password values do not match. Please try again.") })
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
        
    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)    
    public String getPassword() {
        return passwordTypeAndConfirmForm.getPassword();
    }

    public void setPassword(String password) {
        passwordTypeAndConfirmForm.setPassword(password);
    }
    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)    
    public String getRetypedPassword() {
        return passwordTypeAndConfirmForm.getRetypedPassword();
    }

    public void setRetypedPassword(String retypedPassword) {
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
    
    public boolean isSecurityDetailsPopulated()
    {
        return changeSecurityQuestionForm.getSecurityQuestionId()!=null && changeSecurityQuestionForm.getSecurityQuestionId() !=0 && StringUtils.isNotBlank(changeSecurityQuestionForm.getSecurityQuestionAnswer());
    }
    
    
   

}
