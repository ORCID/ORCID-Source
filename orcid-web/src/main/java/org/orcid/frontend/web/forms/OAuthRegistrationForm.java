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

import java.util.Date;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.xml.datatype.XMLGregorianCalendar;

import org.hibernate.validator.constraints.NotBlank;
import org.orcid.frontend.web.forms.validate.FieldMatch;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.CompletionDate;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreationMethod;
import org.orcid.jaxb.model.message.FamilyName;
import org.orcid.jaxb.model.message.GivenNames;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.Preferences;
import org.orcid.jaxb.model.message.SecurityDetails;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkVisibilityDefault;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.utils.DateUtils;
import org.springframework.web.bind.annotation.SessionAttributes;

@FieldMatch.List({ @FieldMatch(first = "password", second = "confirmedPassword", message = "The password and confirmed password must match"),
        @FieldMatch(first = "email", second = "confirmedEmail", message = "The email and confirmed email must match") })
@SessionAttributes("oAuthRegistrationForm")
public class OAuthRegistrationForm {

    private String givenNames;

    private String familyName;

    private String email;

    private String confirmedEmail;

    private String password;

    private String confirmedPassword;

    private boolean termsAccepted;

    private boolean newFeatureInformationRequested;

    private boolean relatedProductsServiceInformationRequested;

    private boolean keepLoggedIn;

    private String workVisibilityDefault = Visibility.PUBLIC.value();

    @NotBlank(message = "Please enter a first name.")
    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    @NotBlank(message = "Please enter a last name.")
    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @NotBlank(message = "Please enter your e-mail address (example: jdoe@institution.edu).")
    @org.hibernate.validator.constraints.Email(message = "Please enter your email address in the proper format " + "(example: jdoe@institution.edu).")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmedEmail() {
        return confirmedEmail;
    }

    public void setConfirmedEmail(String confirmedEmail) {
        this.confirmedEmail = confirmedEmail;
    }

    @NotBlank(message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)
    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }

    @AssertTrue(message = "You must accept the terms and conditions in order to register")
    public boolean isTermsAccepted() {
        return termsAccepted;
    }

    public void setTermsAccepted(boolean termsAccepted) {
        this.termsAccepted = termsAccepted;
    }

    public boolean isNewFeatureInformationRequested() {
        return newFeatureInformationRequested;
    }

    public void setNewFeatureInformationRequested(boolean newFeatureInformationRequested) {
        this.newFeatureInformationRequested = newFeatureInformationRequested;
    }

    public boolean isRelatedProductsServiceInformationRequested() {
        return relatedProductsServiceInformationRequested;
    }

    public void setRelatedProductsServiceInformationRequested(boolean relatedProductsServiceInformationRequested) {
        this.relatedProductsServiceInformationRequested = relatedProductsServiceInformationRequested;
    }

    public boolean isKeepLoggedIn() {
        return keepLoggedIn;
    }

    public void setKeepLoggedIn(boolean keepLoggedIn) {
        this.keepLoggedIn = keepLoggedIn;
    }

    public String getWorkVisibilityDefault() {
        return workVisibilityDefault;
    }

    public void setWorkVisibilityDefault(String workVisibilityDefault) {
        this.workVisibilityDefault = workVisibilityDefault;
    }

    public OrcidProfile getOrcidProfile() {

        OrcidProfile profile = new OrcidProfile();
        profile.setPassword(password);
        OrcidBio bio = new OrcidBio();
        profile.setOrcidBio(bio);
        PersonalDetails personalDetails = new PersonalDetails();

        bio.setPersonalDetails(personalDetails);
        personalDetails.setGivenNames(new GivenNames(givenNames));
        personalDetails.setFamilyName(new FamilyName(familyName));

        ContactDetails contactDetails = new ContactDetails();
        bio.setContactDetails(contactDetails);
        contactDetails.addOrReplacePrimaryEmail(new org.orcid.jaxb.model.message.Email(email));

        OrcidInternal orcidInternal = new OrcidInternal();
        SecurityDetails securityDetails = new SecurityDetails();
        orcidInternal.setSecurityDetails(securityDetails);

        Preferences preferences = new Preferences();
        preferences.setSendChangeNotifications(new SendChangeNotifications(newFeatureInformationRequested));
        preferences.setSendOrcidNews(new SendOrcidNews(relatedProductsServiceInformationRequested));
        preferences.setWorkVisibilityDefault(new WorkVisibilityDefault(Visibility.fromValue(workVisibilityDefault)));
        orcidInternal.setPreferences(preferences);
        profile.setOrcidInternal(orcidInternal);

        OrcidHistory orcidHistory = profile.getOrcidHistory();
        if (orcidHistory == null) {
            orcidHistory = new OrcidHistory();
        }
        XMLGregorianCalendar now = DateUtils.convertToXMLGregorianCalendar(new Date());
        orcidHistory.setSubmissionDate(new SubmissionDate(now));
        orcidHistory.setCompletionDate(new CompletionDate(now));
        orcidHistory.setCreationMethod(CreationMethod.WEBSITE);
        orcidHistory.setClaimed(new Claimed(true));
        profile.setOrcidHistory(orcidHistory);

        return profile;
    }

}
