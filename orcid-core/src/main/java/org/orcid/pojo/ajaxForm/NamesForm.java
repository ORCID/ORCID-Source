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

import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.Keyword;
import org.orcid.jaxb.model.message.Keywords;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OtherName;
import org.orcid.jaxb.model.message.OtherNames;
import org.orcid.jaxb.model.message.PersonalDetails;

public class NamesForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text givenNames;
    private Text familyName;
    private Text creditName;

    
    private Visibility creditNameVisibility;

    public static NamesForm valueOf(PersonalDetails personalDetails) {
        NamesForm nf = new NamesForm();
        if (personalDetails.getGivenNames() != null)
            nf.setGivenNames(Text.valueOf(personalDetails.getGivenNames().getContent()));
        if (personalDetails.getFamilyName() != null)
            nf.setFamilyName(Text.valueOf(personalDetails.getFamilyName().getContent()));
        if (personalDetails.getCreditName() != null) 
            nf.setCreditName(Text.valueOf(personalDetails.getCreditName().getContent()));
        if (personalDetails.getCreditName() != null && personalDetails.getCreditName().getVisibility() != null)
            nf.setCreditNameVisibility(Visibility.valueOf(personalDetails.getCreditName().getVisibility()));
        else
            nf.setCreditNameVisibility(new Visibility());
        return nf;
    }

    public void populatePersonalDetails (PersonalDetails personalDetails) {
        if (this.givenNames != null) {
            if (personalDetails.getGivenNames() == null) personalDetails.setGivenNames(new GivenNames());
            personalDetails.getGivenNames().setContent(this.givenNames.getValue());
        }
        if (this.familyName != null) {
            if (personalDetails.getFamilyName() == null) personalDetails.setFamilyName(new FamilyName());
            personalDetails.getFamilyName().setContent(this.familyName.getValue());
        }
        if (this.creditName != null) {
            if (personalDetails.getCreditName() == null)
                personalDetails.setCreditName(new CreditName());     
            personalDetails.getCreditName().setContent(this.creditName.getValue());
            personalDetails.getCreditName().setVisibility(creditNameVisibility.getVisibility());
        }
    }
    
    public Visibility getCreditNameVisibility() {
        return creditNameVisibility;
    }

    public void setCreditNameVisibility(Visibility visibility) {
        this.creditNameVisibility = visibility;
    }

        
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }



    public Text getGivenNames() {
        return givenNames;
    }



    public void setGivenNames(Text givenNames) {
        this.givenNames = givenNames;
    }



    public Text getFamilyName() {
        return familyName;
    }



    public void setFamilyName(Text familyName) {
        this.familyName = familyName;
    }



    public Text getCreditName() {
        return creditName;
    }



    public void setCreditName(Text creditName) {
        this.creditName = creditName;
    }

}
