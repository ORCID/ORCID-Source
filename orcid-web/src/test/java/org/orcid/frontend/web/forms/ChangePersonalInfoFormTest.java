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

import static org.junit.Assert.assertEquals;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.orcid.jaxb.model.message.OrcidProfile;

public class ChangePersonalInfoFormTest {

    @Test
    public void testConvertFormToOrcidProfile() throws Exception {
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcid("4444-4444-4444-4446");
        ChangePersonalInfoForm changePersonalInfoForm = new ChangePersonalInfoForm(profile);
        changePersonalInfoForm.setFirstName("Johnny");
        changePersonalInfoForm.setLastName("Simpson");
        changePersonalInfoForm.setCreditName("Homer Thompson");
        changePersonalInfoForm.setCreditNameVisibility("private");
        changePersonalInfoForm.setOtherNames("Gal;Dove");
        changePersonalInfoForm.setOtherNamesVisibility("limited");
        changePersonalInfoForm.setBiography("my biography");
        changePersonalInfoForm.setKeywordsDelimited("The;Wicker;Fudd");
        changePersonalInfoForm.setIsoCountryCode("GB");
        changePersonalInfoForm.setWebsiteUrl("www.bbc.co.uk");
        changePersonalInfoForm.setWebsiteUrlText("Ok this might not be my site");
        changePersonalInfoForm.mergeOrcidBioDetails(profile);

        String expected = IOUtils.toString(getClass().getResourceAsStream("change_personal_info_orcid_profile.xml"));
        String actual = profile.toString();
        //assertEquals(expected, actual);
    }

    @Test
    public void testRemoveDuplicatedOtherNames() throws Exception {
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcid("4444-4444-4444-4446");
        ChangePersonalInfoForm changePersonalInfoForm = new ChangePersonalInfoForm(profile);
        changePersonalInfoForm.setFirstName("Johnny");
        changePersonalInfoForm.setLastName("Simpson");
        changePersonalInfoForm.setCreditName("Homer Thompson");
        changePersonalInfoForm.setCreditNameVisibility("private");
        changePersonalInfoForm.setOtherNames("Gal;Dove;Gal;Dove");
        changePersonalInfoForm.setOtherNamesVisibility("limited");
        changePersonalInfoForm.setBiography("my biography");
        changePersonalInfoForm.setKeywordsDelimited("The;Wicker;Fudd");
        changePersonalInfoForm.setIsoCountryCode("GB");
        changePersonalInfoForm.setWebsiteUrl("www.bbc.co.uk");
        changePersonalInfoForm.setWebsiteUrlText("Ok this might not be my site");
        changePersonalInfoForm.mergeOrcidBioDetails(profile);

        String expected = IOUtils.toString(getClass().getResourceAsStream("change_personal_info_orcid_profile.xml"));
        String actual = profile.toString();
        //assertEquals(expected, actual);
    }

    @Test
    public void testRemoveDuplicatedKeywords() throws Exception {
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcid("4444-4444-4444-4446");
        ChangePersonalInfoForm changePersonalInfoForm = new ChangePersonalInfoForm(profile);
        changePersonalInfoForm.setFirstName("Johnny");
        changePersonalInfoForm.setLastName("Simpson");
        changePersonalInfoForm.setCreditName("Homer Thompson");
        changePersonalInfoForm.setCreditNameVisibility("private");
        changePersonalInfoForm.setOtherNames("Gal;Dove;Gal;Dove");
        changePersonalInfoForm.setOtherNamesVisibility("limited");
        changePersonalInfoForm.setBiography("my biography");
        changePersonalInfoForm.setKeywordsDelimited("The;Wicker;Fudd;Fudd;Wicker;The");
        changePersonalInfoForm.setIsoCountryCode("GB");
        changePersonalInfoForm.setWebsiteUrl("www.bbc.co.uk");
        changePersonalInfoForm.setWebsiteUrlText("Ok this might not be my site");
        changePersonalInfoForm.mergeOrcidBioDetails(profile);

        String expected = IOUtils.toString(getClass().getResourceAsStream("change_personal_info_orcid_profile.xml"));
        String actual = profile.toString();
        //assertEquals(expected, actual);
    }

}
