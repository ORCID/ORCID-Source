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

import static org.junit.Assert.*;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.orcid.jaxb.model.message.OrcidProfile;

public class OAuthRegistrationFormTest {

    @Test
    public void testProfileMapping() throws Exception{
       OAuthRegistrationForm form = new OAuthRegistrationForm();
       form.setConfirmedEmail("confirmedEmail");
       form.setConfirmedPassword("confirmedPassword");
       form.setEmail("email");
       form.setFamilyName("familyName");
       form.setGivenNames("givenNames");
       form.setKeepLoggedIn(false);
       form.setNewFeatureInformationRequested(true);
       form.setPassword("password");
       form.setRelatedProductsServiceInformationRequested(false);
       form.setTermsAccepted(true);
       OrcidProfile profile = form.getOrcidProfile();
       assertNotNull(profile);
       String actual = profile.toString();
       String expected = IOUtils.toString(getClass().getResourceAsStream("minimal_orcid_profile.xml"));
       boolean profileDataFound = StringUtils.containsAny(actual, expected);
       assertTrue(profileDataFound);
    }

}
