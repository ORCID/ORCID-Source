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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.orcid.frontend.web.forms.ChangePasswordForm;


public class ChangePasswordFormValidatorTest extends AbstractConstraintValidator<ChangePasswordForm>{
    
    Validator validator;
   
    @Before
    public void resetValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }
    
   

    @Test
    public void allValuesPopulatedHappyPath() {
        ChangePasswordForm form = new ChangePasswordForm();
        form.setOldPassword("0ldPassw$rd");
        form.setPassword("passw0rd");
        form.setRetypedPassword("passw0rd");
        Set<ConstraintViolation<ChangePasswordForm>> errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());
    }
    
    @Test
    public void testPasswordNumbersOnlyInvalid() {
        ChangePasswordForm form = new ChangePasswordForm();
        form.setOldPassword("12345678");       
        Set<ConstraintViolation<ChangePasswordForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);

        String password = allErrorValues.get("oldPassword");
        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol", password);
        
        //add in a char
        form.setOldPassword("12345678b");       
        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);

        password = allErrorValues.get("oldPassword");
        assertNull(password);
    }
   
    @Test
    public void testPasswordLengthMin8() {
        ChangePasswordForm form = new ChangePasswordForm();
        form.setPassword("a$1");       
        Set<ConstraintViolation<ChangePasswordForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);
        String password = allErrorValues.get("password");
        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol", password);
        
        form.setPassword("a$1a$1a$1");     
        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);
        password = allErrorValues.get("password");
        assertNull(password);        
    }
    
    
    @Test
    public void testPasswordMissingNumberInvalid() {
        ChangePasswordForm form = new ChangePasswordForm();
        form.setOldPassword("£$$$$$$r");       
        Set<ConstraintViolation<ChangePasswordForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);

        String password = allErrorValues.get("oldPassword");
        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol", password);
        
        //add in a number
        form.setOldPassword("£$$$$$r1");       
        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);

        password = allErrorValues.get("oldPassword");
        assertNull(password);
    }
    
    @Test
    public void testPasswordWithSymbolButNotCharacterValid() {
        ChangePasswordForm form = new ChangePasswordForm();
        form.setPassword("£$$$$$$$");      
       
        Set<ConstraintViolation<ChangePasswordForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);
        String password = allErrorValues.get("password");
        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol", password);
        
        form.setPassword("£$$$$$$7"); 
        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);
        password = allErrorValues.get("password");
        assertNull(password);
    }

    
    
    @Test
    public void testNonUsAsciiCharsPermitted() throws Exception{
        ChangePasswordForm form = new ChangePasswordForm();
        form.setOldPassword("passw0rd");
        form.setPassword("ååååååå1å");
        form.setRetypedPassword("ååååååå1å");      
        Set<ConstraintViolation<ChangePasswordForm>> errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());
    }
    
    
    @Test
    public void testSpacesPermittted() throws Exception{
        ChangePasswordForm form = new ChangePasswordForm();
        form.setPassword("Ben Kingsley  is my no. 1 actor");
        form.setRetypedPassword("Ben Kingsley  is my no. 1 actor");
        form.setOldPassword("ååååååå1å");        
        Set<ConstraintViolation<ChangePasswordForm>> errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());  
    
    }
    
    @Test
    public void testSymbolsPermitttedButNotRequired() throws Exception{
        ChangePasswordForm form = new ChangePasswordForm();
        form.setOldPassword("Ben Kingsley  is my no. 1 actor");
        form.setPassword("passw0rd");
        form.setRetypedPassword("passw0rd");       
        Set<ConstraintViolation<ChangePasswordForm>> errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());
        
        //check that the test doesn't break when symbols introduced
        form.setPassword("p$ssw0rd");
        form.setRetypedPassword("p$ssw0rd");
        errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());
    }
    
    

    @Test
    public void testPasswordFormMissingData() {
        ChangePasswordForm form = new ChangePasswordForm();
        form.setOldPassword("");
        form.setPassword("");
        form.setRetypedPassword("");
        Set<ConstraintViolation<ChangePasswordForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);

        String password = allErrorValues.get("password");
        String confirmedPassword = allErrorValues.get("retypedPassword");
        String oldPassword = allErrorValues.get("oldPassword");
     

        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol", password);
        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol", confirmedPassword);
        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol", oldPassword);

    }

 

    @Test
    public void testPasswordFormatValidation() throws Exception {

        String pwErrorMessage = "Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol";

        String tooShortPassword = "pssword";
        ChangePasswordForm form = new ChangePasswordForm();
        form.setPassword(tooShortPassword);
        Set<ConstraintViolation<ChangePasswordForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);
        String passwordValidationError = allErrorValues.get("password");
        assertEquals(pwErrorMessage, passwordValidationError);

        String eightDigitsButLowerCaseAlphaOnly = "password";
        form.setPassword(eightDigitsButLowerCaseAlphaOnly);
        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);
        passwordValidationError = allErrorValues.get("password");
        assertEquals(pwErrorMessage, passwordValidationError);

        String eightDigitsLowerUpperCaseButAlphaOnly = "pAssword";
        form.setPassword(eightDigitsLowerUpperCaseButAlphaOnly);
        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);
        passwordValidationError = allErrorValues.get("password");
        assertEquals(pwErrorMessage, passwordValidationError);

        String eightCharactersLowerUpperCaseSymbol = "pAssw%rd";
        form.setPassword(eightCharactersLowerUpperCaseSymbol);
        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);
        passwordValidationError = allErrorValues.get("password");
        assertEquals(pwErrorMessage, passwordValidationError);

        String eightMixedCharactersDigitsAndSymbol = "p4ssW&rd";
        form.setPassword(eightMixedCharactersDigitsAndSymbol);
        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);
        passwordValidationError = allErrorValues.get("password");
        assertNull(passwordValidationError);

        String eightUpperCaseCharactersOnlyDigitsAndSymbol = "P4SSW&RD";
        form.setPassword(eightUpperCaseCharactersOnlyDigitsAndSymbol);
        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);
        passwordValidationError = allErrorValues.get("password");
        assertNull(passwordValidationError);

    }

    @Test   
    public void testPasswordsMatchValidation() throws Exception {
        String eightMixedCharactersDigitsAndSymbol = "p4s]sW[rd";
        String incorrectRetype = "p4s]sT]rd";

        ChangePasswordForm form = new ChangePasswordForm();
        form.setPassword(eightMixedCharactersDigitsAndSymbol);
        form.setRetypedPassword(incorrectRetype);

        Set<ConstraintViolation<ChangePasswordForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);

        String passwordFormatError = allErrorValues.get("password");
        String retypedPasswordFormatError = allErrorValues.get("retypedPassword");
        allErrorValues = retrieveErrorKeyAndMessage(violations);

        // the passwords themselves should be ok as they conform to the format,
        // problem is they don't match
        assertNull(passwordFormatError);
        assertNull(retypedPasswordFormatError);

        Set<String> fieldLevelErrors = retrieveErrorValuesOnly(violations);
        assertTrue(fieldLevelErrors.contains("New password values don’t match. Please try again"));

        // now match them
        form.setRetypedPassword("p4s]sW[rd");

        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);

        passwordFormatError = allErrorValues.get("password");
        retypedPasswordFormatError = allErrorValues.get("retypedPassword");
        allErrorValues = retrieveErrorKeyAndMessage(violations);

        assertNull(passwordFormatError);
        assertNull(retypedPasswordFormatError);
        fieldLevelErrors = retrieveErrorValuesOnly(violations);
        assertFalse(fieldLevelErrors.contains("The password and confirmed password must match"));

    }

    

}
