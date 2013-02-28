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
package org.orcid.frontend.web.forms.validate;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.orcid.frontend.web.forms.JointAffiliationForm;

/**
 * Copyright 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 27/02/2012
 */
public class JointAffiliationValidatorTest extends AbstractConstraintValidator<JointAffiliationForm> {

    Validator validator;

    @Before
    public void resetValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testIsValidEmpty() throws Exception {
        JointAffiliationForm affiliationForm = new JointAffiliationForm();
        Set<ConstraintViolation<JointAffiliationForm>> violations = validator.validate(affiliationForm);
        assertEquals(0, violations.size());
    }

    @Test
    public void testEnteringFieldTriggersValidation() throws Exception {
        JointAffiliationForm affiliationForm = new JointAffiliationForm();
        affiliationForm.setAddressLine2("An address 2");
        Set<ConstraintViolation<JointAffiliationForm>> violations = validator.validate(affiliationForm);
        assertEquals(5, violations.size());
        Set<String> errorMessages = retrieveErrorValuesOnly(violations);
        assertTrue(errorMessages.contains("Joint affiliation city is a required field"));
        assertTrue(errorMessages.contains("Joint affiliation registration role is a required field"));
        assertTrue(errorMessages.contains("Joint affiliation institution name is required"));
        assertTrue(errorMessages.contains("Joint affiliation country is a required field"));
        assertTrue(errorMessages.contains("Joint affiliation address line 1 is a required field"));

    }
}
