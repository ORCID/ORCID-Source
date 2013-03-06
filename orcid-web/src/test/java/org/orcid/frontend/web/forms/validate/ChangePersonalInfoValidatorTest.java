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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.orcid.frontend.web.forms.ChangePersonalInfoForm;


public class ChangePersonalInfoValidatorTest extends AbstractConstraintValidator<ChangePersonalInfoForm>  {
    Validator validator;

    @Before
    public void resetValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void allMandatoryValuesPopulated() {
        
        ChangePersonalInfoForm form = new ChangePersonalInfoForm();
        form.setFirstName("firstName");
        form.setLastName("lastName");
        form.setEmail("jimmy@this.com");
        
        
        Set<ConstraintViolation<ChangePersonalInfoForm>> errors = validator.validate(form);   
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(errors);
        assertEquals("Should be 0 errors", 0, allErrorValues.size());
        
    
        
    }
    
    @Test
    public void noMandatoryValuesPopulated() {
        ChangePersonalInfoForm form = new ChangePersonalInfoForm();
        
        Set<ConstraintViolation<ChangePersonalInfoForm>> errors = validator.validate(form);   
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(errors);
        assertEquals("Should be 2 errors", 2, allErrorValues.size());
        String givenNamesMissing = allErrorValues.get("firstName");
        String emailMissing = allErrorValues.get("email");  
        
        assertEquals("Please enter your first name.", givenNamesMissing);
        assertEquals("Please enter your e-mail address.",emailMissing);       
    }
    
    @Test
    public void lengthGreaterThan100NotAcceptedForBio() throws Exception
    {
        String thousandAndOneChars = StringUtils.repeat("a",1001);
        assertTrue(thousandAndOneChars.length()==1001);
        
        ChangePersonalInfoForm form = new ChangePersonalInfoForm();
        form.setFirstName("firstName");
        form.setLastName("lastName");
        form.setEmail("test+11@orcid.org");        
        form.setBiography(thousandAndOneChars);
        Set<ConstraintViolation<ChangePersonalInfoForm>> errors = validator.validate(form);   
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(errors);
        assertEquals("Should be 1 error", 1, allErrorValues.size());
        String tooLong = allErrorValues.get("biography");
        assertEquals("The maximum length for biography is 1000 characters, including line breaks",tooLong);    
        
        String thousandChars = StringUtils.repeat("z",1000);               
        form.setBiography(thousandChars);
        errors = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(errors);
        assertEquals("Should be valid", 0, allErrorValues.size());
        
    }
}
