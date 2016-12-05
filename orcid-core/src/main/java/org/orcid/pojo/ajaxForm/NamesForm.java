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
import org.orcid.jaxb.model.common_rc4.CreditName;
import org.orcid.jaxb.model.record_rc4.FamilyName;
import org.orcid.jaxb.model.record_rc4.GivenNames;
import org.orcid.jaxb.model.record_rc4.Name;
import org.orcid.jaxb.model.record_rc4.PersonalDetails;

public class NamesForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text givenNames;
    private Text familyName;
    private Text creditName;
    private Visibility namesVisibility;

    public static NamesForm valueOf(Name name) {
        NamesForm nf = new NamesForm();

        if (name != null) {
            if (name.getGivenNames() != null) {
                nf.setGivenNames(Text.valueOf(name.getGivenNames().getContent()));
            }

            if (name.getFamilyName() != null) {
                nf.setFamilyName(Text.valueOf(name.getFamilyName().getContent()));
            }

            if (name.getCreditName() != null) {
                nf.setCreditName(Text.valueOf(name.getCreditName().getContent()));
            }

            if (name.getVisibility() != null) {
                nf.setNamesVisibility(Visibility.valueOf(name.getVisibility()));
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
            personalDetails.getName().setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.fromValue(namesVisibility.getVisibility().value()));
        } else {
            personalDetails.getName().setVisibility(org.orcid.jaxb.model.common_rc4.Visibility.fromValue(OrcidVisibilityDefaults.NAMES_DEFAULT.getVisibility().value()));
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
