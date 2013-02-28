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

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.orcid.frontend.web.forms.validate.FieldMatch;
import org.orcid.jaxb.model.message.Claimed;
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
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkVisibilityDefault;
import org.orcid.password.constants.OrcidPasswordConstants;
import org.orcid.utils.DateUtils;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * 
 * @author Will Simpson
 * 
 */

@FieldMatch.List({ @FieldMatch(first = "password", second = "retypedPassword", message = "The password and confirmed password must match"),
        @FieldMatch(first = "email", second = "confirmedEmail", message = "The email and confirmed email must match") })
public class RegistrationForm implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1139846493791158920L;

    private String givenNames;

    private String familyName;

    private String email;

    private String confirmedEmail;

    private String antiSpam;

    private String password;

    private String retypedPassword;

    private boolean sendOrcidNews;

    private boolean sendOrcidChangeNotifcations;

    private boolean acceptTermsAndConditions;

    private String workVisibilityDefault = Visibility.PUBLIC.value();

    @NotBlank(message = "Please enter your first name.")
    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    @NotBlank(message = "Please enter your e-mail address.")
    @Email(message = "Invalid email address.")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Size(max = 0, message = "There was an error validating your input")
    public String getReferRegOrcid() {
        return antiSpam;
    }

    /**
     * A honeypot trap for bots to try and stop them registering. Named field
     * referOrcid to mislead...
     * 
     * @param antiSpam
     */
    public void setReferRegOrcid(String antiSpam) {
        this.antiSpam = antiSpam;
    }

    public String getConfirmedEmail() {
        return confirmedEmail;
    }

    public void setConfirmedEmail(String confirmedEmail) {
        this.confirmedEmail = confirmedEmail;
    }

    public boolean isSendOrcidNews() {
        return sendOrcidNews;
    }

    public void setSendOrcidNews(boolean sendOrcidNews) {
        this.sendOrcidNews = sendOrcidNews;
    }

    public boolean isSendOrcidChangeNotifcations() {
        return sendOrcidChangeNotifcations;
    }

    public void setSendOrcidChangeNotifcations(boolean sendOrcidChangeNotifcations) {
        this.sendOrcidChangeNotifcations = sendOrcidChangeNotifcations;
    }

    @AssertTrue(message = "You must accept the terms and conditions.")
    public boolean isAcceptTermsAndConditions() {
        return acceptTermsAndConditions;
    }

    public void setAcceptTermsAndConditions(boolean acceptTermsAndConditions) {
        this.acceptTermsAndConditions = acceptTermsAndConditions;
    }

    @NotBlank(message = "Please enter a password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NotBlank(message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)
    @Pattern(regexp = OrcidPasswordConstants.ORCID_PASSWORD_REGEX, message = OrcidPasswordConstants.PASSWORD_REGEX_MESSAGE)
    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }

    public String getWorkVisibilityDefault() {
        return workVisibilityDefault;
    }

    public void setWorkVisibilityDefault(String workVisibilityDefault) {
        this.workVisibilityDefault = workVisibilityDefault;
    }

    public OrcidProfile toOrcidProfile() {

        OrcidProfile profile = new OrcidProfile();
        OrcidBio bio = new OrcidBio();

        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new org.orcid.jaxb.model.message.Email(email));
        Preferences preferences = new Preferences();
        preferences.setSendChangeNotifications(new SendChangeNotifications(sendOrcidChangeNotifcations));
        preferences.setSendOrcidNews(new SendOrcidNews(sendOrcidNews));
        preferences.setWorkVisibilityDefault(new WorkVisibilityDefault(Visibility.fromValue(workVisibilityDefault)));

        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFamilyName(new FamilyName(familyName));
        personalDetails.setGivenNames(new GivenNames(givenNames));

        bio.setContactDetails(contactDetails);
        bio.setPersonalDetails(personalDetails);
        OrcidInternal internal = new OrcidInternal();
        internal.setPreferences(preferences);
        profile.setOrcidBio(bio);
        profile.setOrcidInternal(internal);

        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(true));
        orcidHistory.setCreationMethod(CreationMethod.WEBSITE);

        profile.setOrcidHistory(orcidHistory);
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));

        profile.setPassword(password);

        return profile;
    }

}
