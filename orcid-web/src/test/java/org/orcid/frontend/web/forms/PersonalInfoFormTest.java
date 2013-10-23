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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.orcid.jaxb.model.message.OrcidProfile;

public class PersonalInfoFormTest {

    @Test
    public void testGetOrcidProfile() throws IOException {
        PersonalInfoForm form = new PersonalInfoForm();
        form.setOrcid("4444-4444-4444-4446");
        form.setGivenNames("Johnny");
        form.setFamilyName("Simpson");
        // show that other names aren't duplicates
        form.setSelectedOtherNames(Arrays.asList(new String[] { "Don", "Gal", "Don" }));
        form.setCreditName("Homer Thomson");
        form.setEmail("homer.thomson@simpsons.com");
        // workaround because we're not creating form from a profile object, but
        // from scratch
        form.setEmailVisibility("public");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<PersonalInfoForm>> errors = validator.validate(form);
        assertTrue(errors.isEmpty());
        OrcidProfile profile = form.getOrcidProfile();
        assertNotNull(profile);
        String expected = IOUtils.toString(getClass().getResourceAsStream("personal_info_orcid_profile.xml"), "UTF-8");
        String actual = profile.toString();
        //assertEquals(expected, actual);
    }
}
