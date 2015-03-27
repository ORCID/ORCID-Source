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
package org.orcid.frontend.web.forms;

import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.ElementNameAndTextQualifier;
import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Test;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.aop.ProfileLastModifiedAspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChangePersonalInfoFormTest {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangePersonalInfoFormTest.class);

    @Test
    public void testConvertFormToOrcidProfile() throws Exception {
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4446"));
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
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expected, actual);
        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
        LOGGER.error(expected);
        LOGGER.error(actual);
        XMLAssert.assertXMLEqual(diff, true);
    }

    @Test
    public void testRemoveDuplicatedOtherNames() throws Exception {
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4446"));
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
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expected, actual);
        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
        LOGGER.error(expected);
        LOGGER.error(actual);
        XMLAssert.assertXMLEqual(diff, true);
    }

    @Test
    public void testRemoveDuplicatedKeywords() throws Exception {
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcidIdentifier(new OrcidIdentifier("4444-4444-4444-4446"));
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
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = new Diff(expected, actual);
        diff.overrideElementQualifier(new ElementNameAndTextQualifier());
        LOGGER.error(expected);
        LOGGER.error(actual);
        XMLAssert.assertXMLEqual(diff, true);
    }

}
