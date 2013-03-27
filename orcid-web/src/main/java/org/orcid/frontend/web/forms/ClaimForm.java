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

import java.io.Serializable;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.NotBlank;
import org.orcid.frontend.web.forms.validate.FieldMatch;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.password.constants.OrcidPasswordConstants;

@FieldMatch.List( { @FieldMatch(first = "password", second = "retypedPassword", message = "New password values don’t match. Please try again") })
public class ClaimForm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String password;

    private String retypedPassword;

    private boolean sendOrcidNews;

    private boolean sendOrcidChangeNotifcations;

    private String workVisibilityDefault = Visibility.PUBLIC.value();

    private boolean acceptTermsAndConditions;

    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NotBlank(message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)
    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)
    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }

    public boolean isSendOrcidNews() {
        return sendOrcidNews;
    }

    public void setSendOrcidNews(boolean sendOrcidNews) {
        this.sendOrcidNews = sendOrcidNews;
    }

    public boolean isSendOrcidChangeNotifcations() {
        return sendOrcidChangeNotifcations;
    }

    public void setSendOrcidChangeNotifcations(boolean sendOrcidChangeNotifcations) {
        this.sendOrcidChangeNotifcations = sendOrcidChangeNotifcations;
    }

    public String getWorkVisibilityDefault() {
        return workVisibilityDefault;
    }

    public void setWorkVisibilityDefault(String workVisibilityDefault) {
        this.workVisibilityDefault = workVisibilityDefault;
    }

    @AssertTrue(message = "You must accept the terms and conditions in order to register")
    public boolean isAcceptTermsAndConditions() {
        return acceptTermsAndConditions;
    }

    public void setAcceptTermsAndConditions(boolean acceptTermsAndConditions) {
        this.acceptTermsAndConditions = acceptTermsAndConditions;
    }

}
