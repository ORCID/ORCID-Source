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

import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public class CountryForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private Iso2Country iso2Country;

    private Visibility profileAddressVisibility;

    String orcid;

    private List<String> errors = new ArrayList<String>();

    public static CountryForm valueOf(OrcidProfile op) {
        CountryForm pf = new CountryForm();
        pf.setProfileAddressVisibility(new Visibility()); // always start off with public
        if (op.getOrcidBio() != null) {
            OrcidBio ob = op.getOrcidBio();
            if (ob.getContactDetails() != null) {
                ContactDetails cd = ob.getContactDetails();
                if (cd.getAddress() != null) {
                    Address a = cd.getAddress();
                    if (a.getCountry() != null && a.getCountry().getValue() != null)
                        pf.setIso2Country(Iso2Country.valueOf(a.getCountry().getValue()));
                    if (a.getCountry().getVisibility() != null)
                        pf.setProfileAddressVisibility(Visibility.valueOf(a.getCountry().getVisibility()));
                }
            }
        }
        return pf;
    }

    public void populateProfile(OrcidProfile op) {
        if (op.getOrcidBio() == null)
            op.setOrcidBio(new OrcidBio());
        OrcidBio ob = op.getOrcidBio();
        if (ob.getContactDetails() == null)
            ob.setContactDetails(new ContactDetails());
        ContactDetails cd = ob.getContactDetails();
        if (cd.getAddress() == null)
            cd.setAddress(new Address());
        Address a = cd.getAddress();
        if (this.iso2Country != null) { 
            a.setCountry(new org.orcid.jaxb.model.message.Country(this.iso2Country.getValue()));
            a.getCountry().setVisibility(this.profileAddressVisibility.getVisibility());
        }

    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Visibility getProfileAddressVisibility() {
        return profileAddressVisibility;
    }

    public void setProfileAddressVisibility(Visibility profileAddressVisibility) {
        this.profileAddressVisibility = profileAddressVisibility;
    }

    public Iso2Country getIso2Country() {
        return iso2Country;
    }

    public void setIso2Country(Iso2Country iso2Country) {
        this.iso2Country = iso2Country;
    }

}
