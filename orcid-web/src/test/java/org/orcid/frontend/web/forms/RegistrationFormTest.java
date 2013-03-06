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

public class RegistrationFormTest {

    @Test
    public void testProfileMapping() throws Exception {
        RegistrationForm form = new RegistrationForm();
        form.setConfirmedEmail("confirmedEmail");
        form.setConfirmedPassword("confirmedPassword");
        form.setEmail("email");
        form.setFamilyName("familyName");
        form.setGivenNames("givenNames");
        form.setSendOrcidChangeNotifications(true);
        form.setPassword("password");
        form.setSendOrcidNews(false);
        form.setAcceptTermsAndConditions(true);
        OrcidProfile profile = form.toOrcidProfile();
        assertNotNull(profile);
        String actual = profile.toString();
        String expected = IOUtils.toString(getClass().getResourceAsStream("minimal_orcid_profile.xml"));
        boolean profileDataFound = StringUtils.containsAny(actual, expected);
        assertTrue(profileDataFound);
    }

}
