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

public class ProfileDetails implements ErrorsInterface {

    private List<String> errors = new ArrayList<String>();
    String orcid;
    String givenNames;
    String familyName;
    String email;

    public ProfileDetails() {

    }

    public ProfileDetails(String orcid, String givenNames, String familyName, String email) {
        this.orcid = orcid;
        this.givenNames = givenNames;
        this.familyName = familyName;
        this.email = email;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
