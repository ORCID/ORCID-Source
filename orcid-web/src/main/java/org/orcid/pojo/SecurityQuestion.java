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
package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class SecurityQuestion implements ErrorsInterface {
    private List<String> errors = new ArrayList<String>();

    private String securityAnswer;

    private long securityQuestionId;

    private String password;
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public long getSecurityQuestionId() {
        return securityQuestionId;
    }

    public void setSecurityQuestionId(long securityQuestionId) {
        this.securityQuestionId = securityQuestionId;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
