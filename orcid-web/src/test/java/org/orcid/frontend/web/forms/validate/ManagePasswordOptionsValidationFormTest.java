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

import org.junit.Before;
import org.junit.Test;
import org.orcid.frontend.web.forms.ManagePasswordOptionsForm;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Test Scenarios for the validation of fields on the password options form on
 * the Manage Profile Screen. Much of the validation is similar to the
 * {@link VerifyRegistrationForm} but the forms use delegation so any validation
 * can be split out in future if need be.
 * 
 * @author jamesb
 * @See {@link VerifyRegistrationForm}
 * 
 */
public class ManagePasswordOptionsValidationFormTest extends AbstractConstraintValidator<ManagePasswordOptionsForm> {

    Validator validator;

    @Before
    public void resetValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void allValuesPopulatedHappyPath() {
        ManagePasswordOptionsForm form = new ManagePasswordOptionsForm();
        form.setPassword("passw0rd");
        form.setRetypedPassword("passw0rd");
        form.setSecurityQuestionId(1);
        form.setSecurityQuestionAnswer("stan");
        form.setVerificationNumber("9999");
        Map<String, String> registrationRoles = new HashMap<String, String>();
        registrationRoles.put("1", "Researcher");
        Set<ConstraintViolation<ManagePasswordOptionsForm>> errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());
    }
    
    @Test
    public void testNonUsAsciiCharsPermitted() throws Exception{
        ManagePasswordOptionsForm form = new ManagePasswordOptionsForm();
        form.setPassword("ååååååå1å");
        form.setRetypedPassword("ååååååå1å");
        form.setSecurityQuestionId(1);
        form.setSecurityQuestionAnswer("stan");
        form.setVerificationNumber("9999");
        Map<String, String> registrationRoles = new HashMap<String, String>();
        registrationRoles.put("1", "Researcher");
        Set<ConstraintViolation<ManagePasswordOptionsForm>> errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());
    }
    
    @Test
    public void testSpacesPermittted() throws Exception{
        ManagePasswordOptionsForm form = new ManagePasswordOptionsForm();
        form.setPassword("Stellan Skasgård  is my no. 1 actor");
        form.setRetypedPassword("Stellan Skasgård  is my no. 1 actor");
        form.setSecurityQuestionId(1);
        form.setSecurityQuestionAnswer("stan");
        form.setVerificationNumber("9999");
        Map<String, String> registrationRoles = new HashMap<String, String>();
        registrationRoles.put("1", "Researcher");
        Set<ConstraintViolation<ManagePasswordOptionsForm>> errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());    
    
    }
    
    @Test
    public void testSymbolsPermitttedButNotRequired() throws Exception{
        ManagePasswordOptionsForm form = new ManagePasswordOptionsForm();
        form.setPassword("passw0rd");
        form.setRetypedPassword("passw0rd");
        form.setSecurityQuestionId(1);
        form.setSecurityQuestionAnswer("stan");
        form.setVerificationNumber("9999");
        Map<String, String> registrationRoles = new HashMap<String, String>();
        registrationRoles.put("1", "Researcher");
        Set<ConstraintViolation<ManagePasswordOptionsForm>> errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());
        
        //check that the test doesn't break when symbols introduced
        form.setPassword("p$ssw0rd");
        form.setRetypedPassword("p$ssw0rd");
        errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());
    }
    
    

    @Test
    public void testPasswordFormMissingData() {
        ManagePasswordOptionsForm form = new ManagePasswordOptionsForm();
        form.setPassword("");
        form.setRetypedPassword("");
        Set<ConstraintViolation<ManagePasswordOptionsForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);

        String password = allErrorValues.get("password");
        String confirmedPassword = allErrorValues.get("retypedPassword");
        String securityQuestion = allErrorValues.get("securityQuestionId");
        String securityAnswer = allErrorValues.get("securityQuestionAnswer");

        String digits = allErrorValues.get("verificationNumber");

        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol", password);
        assertEquals("The confirm password field is invalid.", confirmedPassword);

        assertEquals("Please select a security question.", securityQuestion);
        assertEquals("Please provide an answer to your security question.", securityAnswer);

        assertEquals("Please enter a 4 digit verification number", digits);

    }

    @Test
    public void testRegistrationFormInvalidData() throws Exception {
        ManagePasswordOptionsForm form = new ManagePasswordOptionsForm();
        form.setSecurityQuestionId(0);
        form.setVerificationNumber("999");
        Set<ConstraintViolation<ManagePasswordOptionsForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);

        String securityQuestion = allErrorValues.get("securityQuestionId");
        String digits = allErrorValues.get("verificationNumber");

        assertEquals("Please select a security question.", securityQuestion);
        assertEquals("Please enter a 4 digit verification number", digits);

        // also try a negative verification number just to make sure
        form.setVerificationNumber("-7299");
        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);
        digits = allErrorValues.get("verificationNumber");
        assertEquals("Please enter a 4 digit verification number", digits);

    }

    @Test
    public void testPasswordFormatValidation() throws Exception {

        String pwErrorMessage = "Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol";

        String tooShortPassword = "pssword";
        ManagePasswordOptionsForm form = new ManagePasswordOptionsForm();
        form.setPassword(tooShortPassword);
        Set<ConstraintViolation<ManagePasswordOptionsForm>> violations = validator.validate(form);
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

        ManagePasswordOptionsForm form = new ManagePasswordOptionsForm();
        form.setPassword(eightMixedCharactersDigitsAndSymbol);
        form.setRetypedPassword(incorrectRetype);

        Set<ConstraintViolation<ManagePasswordOptionsForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);

        String passwordFormatError = allErrorValues.get("password");
        String retypedPasswordFormatError = allErrorValues.get("retypedPassword");
        allErrorValues = retrieveErrorKeyAndMessage(violations);

        // the passwords themselves should be ok as they conform to the format,
        // problem is they don't match
        assertNull(passwordFormatError);
        assertNull(retypedPasswordFormatError);

        Set<String> fieldLevelErrors = retrieveErrorValuesOnly(violations);
        assertTrue(fieldLevelErrors.contains("The password and confirm password field must match"));

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
