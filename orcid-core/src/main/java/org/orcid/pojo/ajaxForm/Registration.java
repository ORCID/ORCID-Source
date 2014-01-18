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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Registration implements ErrorsInterface, Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private List<String> errors;

    private Checkbox sendChangeNotifications;

    private Checkbox sendOrcidNews;

    private Checkbox termsOfUse;

    private Visibility workVisibilityDefault;

    private Text password;

    private Text passwordConfirm;

    private Text email;

    private Text emailConfirm;

    private Text givenNames;

    private Text familyNames;
    
    private Text creationType;

    public Registration() {
        errors = new ArrayList<String>();
        password = new Text();
        passwordConfirm = new Text();
        email = new Text();
        emailConfirm = new Text();
        givenNames = new Text();
        familyNames = new Text();
        creationType = new Text();
        sendChangeNotifications = new Checkbox();
        sendOrcidNews = new Checkbox();
        termsOfUse = new Checkbox();
        workVisibilityDefault = new Visibility();

    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getPassword() {
        return password;
    }

    public void setPassword(Text password) {
        this.password = password;
    }

    public Text getEmail() {
        return email;
    }

    public void setEmail(Text email) {
        this.email = email;
    }

    public Text getEmailConfirm() {
        return emailConfirm;
    }

    public void setEmailConfirm(Text emailConfirm) {
        this.emailConfirm = emailConfirm;
    }

    public Text getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(Text givenNames) {
        this.givenNames = givenNames;
    }

    public Text getFamilyNames() {
        return familyNames;
    }

    public void setFamilyNames(Text familyNames) {
        this.familyNames = familyNames;
    }

    public Text getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(Text passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public Checkbox getSendChangeNotifications() {
        return sendChangeNotifications;
    }

    public void setSendChangeNotifications(Checkbox sendChangeNotifications) {
        this.sendChangeNotifications = sendChangeNotifications;
    }

    public Visibility getWorkVisibilityDefault() {
        return workVisibilityDefault;
    }

    public void setWorkVisibilityDefault(Visibility workVisibilityDefault) {
        this.workVisibilityDefault = workVisibilityDefault;
    }

    public Checkbox getSendOrcidNews() {
        return sendOrcidNews;
    }

    public void setSendOrcidNews(Checkbox sendOrcidNews) {
        this.sendOrcidNews = sendOrcidNews;
    }

    public Checkbox getTermsOfUse() {
        return termsOfUse;
    }

    public void setTermsOfUse(Checkbox termsOfUse) {
        this.termsOfUse = termsOfUse;
    }

    public Text getCreationType() {
        return creationType;
    }

    public void setCreationType(Text creationType) {
        this.creationType = creationType;
    }

}
