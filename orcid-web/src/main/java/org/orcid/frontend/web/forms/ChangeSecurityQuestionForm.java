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

import org.orcid.frontend.web.forms.validate.IntegerStringCrossField;



@IntegerStringCrossField(indexToIgnoreValidation = 0,
        theFieldToIgnoreValidation = "securityQuestionAnswer",
        theFieldToIndex="securityQuestionId",
        message = "Please provide an answer to your challenge question.")
public class ChangeSecurityQuestionForm {

    private Integer securityQuestionId;

    private String securityQuestionAnswer;
    
    public Integer getSecurityQuestionId() {
        return securityQuestionId;
    }
   
    public void setSecurityQuestionId(Integer securityQuestionId) {
        this.securityQuestionId = securityQuestionId;
    }
    
    public String getSecurityQuestionAnswer() {
        return securityQuestionAnswer;
    }

    public void setSecurityQuestionAnswer(String securityQuestionAnswer) {
        this.securityQuestionAnswer = securityQuestionAnswer;
    }

}