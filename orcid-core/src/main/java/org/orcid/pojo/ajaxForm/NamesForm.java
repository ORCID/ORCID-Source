/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.common_rc2.CreditName;
import org.orcid.jaxb.model.record_rc2.FamilyName;
import org.orcid.jaxb.model.record_rc2.GivenNames;
import org.orcid.jaxb.model.record_rc2.Name;
import org.orcid.jaxb.model.record_rc2.PersonalDetails;

public class NamesForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text givenNames;
    private Text familyName;
    private Text creditName;
    private Visibility namesVisibility;

    public static NamesForm valueOf(PersonalDetails personalDetails) {
        NamesForm nf = new NamesForm();

        if (personalDetails.getName() != null) {
            if (personalDetails.getName().getGivenNames() != null) {
                nf.setGivenNames(Text.valueOf(personalDetails.getName().getGivenNames().getContent()));
            }

            if (personalDetails.getName().getFamilyName() != null) {
                nf.setFamilyName(Text.valueOf(personalDetails.getName().getFamilyName().getContent()));
            }

            if (personalDetails.getName().getCreditName() != null) {
                nf.setCreditName(Text.valueOf(personalDetails.getName().getCreditName().getContent()));
            }

            if (personalDetails.getName().getVisibility() != null) {
                nf.setNamesVisibility(Visibility.valueOf(personalDetails.getName().getVisibility()));
            } else {
                nf.setNamesVisibility(Visibility.valueOf(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility()));
            }
        }

        return nf;
    }
    
    public PersonalDetails toPersonalDetails() {
        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setName(new Name());
        if(!PojoUtil.isEmpty(givenNames)) {
            personalDetails.getName().setGivenNames(new GivenNames(givenNames.getValue()));
        }
        
        if(!PojoUtil.isEmpty(familyName)) {
            personalDetails.getName().setFamilyName(new FamilyName(familyName.getValue()));
        }

        if(!PojoUtil.isEmpty(creditName)) {
            personalDetails.getName().setCreditName(new CreditName(creditName.getValue()));
        }
        
        if(namesVisibility != null && namesVisibility.getVisibility() != null) {
            personalDetails.getName().setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(namesVisibility.getVisibility().value()));
        } else {
            personalDetails.getName().setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value()));
        }
        
        return personalDetails;
    }

    public Visibility getNamesVisibility() {
        return namesVisibility;
    }

    public void setNamesVisibility(Visibility visibility) {
        this.namesVisibility = visibility;
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
