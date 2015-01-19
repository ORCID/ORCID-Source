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
package org.orcid.integration.api.helper;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.message.ActivitiesVisibilityDefault;
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
import org.orcid.jaxb.model.message.ReferredBy;
import org.orcid.jaxb.model.message.SendChangeNotifications;
import org.orcid.jaxb.model.message.SendOrcidNews;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.pojo.ajaxForm.Client;
import org.orcid.pojo.ajaxForm.Group;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.RedirectUri;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.DateUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class InitializeDataHelper {

    @Resource
    private OrcidClientGroupManager orcidClientGroupManager;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Value("${org.orcid.web.base.url:http://localhost:8080/orcid-web}")
    protected String webBaseUrl;

    public void deleteProfile(String orcid) throws Exception {
        orcidProfileManager.deactivateOrcidProfile(orcidProfileManager.retrieveOrcidProfile(orcid));
        profileDao.removeProfile(orcid);
    }

    public void deleteClient(String clientId) throws Exception {
        clientDetailsDao.removeClient(clientId);
    }

    public Group createMember(GroupType type) throws Exception {
        String name = type.value() + System.currentTimeMillis() + "@orcid-integration-test.com";
        Group group = new Group();
        group.setEmail(Text.valueOf(name));
        group.setGroupName(Text.valueOf(name));
        group.setType(Text.valueOf(type.value()));
        group.setErrors(new ArrayList<String>());

        OrcidClientGroup clientGroup = orcidClientGroupManager.createGroup(group.toOrcidClientGroup());
        assertNotNull(clientGroup);
        assertFalse(PojoUtil.isEmpty(clientGroup.getGroupOrcid()));
        group.setGroupOrcid(Text.valueOf(clientGroup.getGroupOrcid()));
        return group;
    }

    public OrcidClient createClient(String groupOrcid) throws Exception {
        Client client = new Client();
        client.setDisplayName(Text.valueOf("client_for_" + groupOrcid));
        client.setShortDescription(Text.valueOf("Description test"));
        client.setWebsite(Text.valueOf("www." + groupOrcid + ".com"));
        RedirectUri rUri = new RedirectUri();
        rUri.setValue(Text.valueOf(getRedirectUri()));
        List<RedirectUri> rUris = new ArrayList<RedirectUri>();
        rUris.add(rUri);
        client.setRedirectUris(rUris);

        OrcidClient orcidClient = client.toOrcidClient();
        orcidClient = orcidClientGroupManager.createAndPersistClientProfile(groupOrcid, orcidClient);

        assertNotNull(orcidClient);
        assertFalse(PojoUtil.isEmpty(orcidClient.getClientId()));
        return orcidClient;
    }

    public OrcidProfile createProfile(String email, String password) throws Exception {
        Text emailText = Text.valueOf(email);
        Text passwordText = Text.valueOf(password);
        Registration registration = new Registration();
        registration.setGivenNames(emailText);
        registration.setEmail(emailText);
        registration.setEmailConfirm(emailText);
        registration.setPassword(passwordText);
        registration.setPasswordConfirm(passwordText);
        OrcidProfile orcidProfile = toProfile(registration);
        orcidProfile = orcidProfileManager.createOrcidProfile(orcidProfile);
        return orcidProfile;
    }

    protected String getRedirectUri() {
        return webBaseUrl + "/oauth/playground";
    }

    private OrcidProfile toProfile(Registration reg) {
        OrcidProfile profile = new OrcidProfile();
        OrcidBio bio = new OrcidBio();

        ContactDetails contactDetails = new ContactDetails();
        contactDetails.addOrReplacePrimaryEmail(new org.orcid.jaxb.model.message.Email(reg.getEmail().getValue()));
        Preferences preferences = new Preferences();
        preferences.setSendChangeNotifications(new SendChangeNotifications(reg.getSendChangeNotifications().getValue()));
        preferences.setSendOrcidNews(new SendOrcidNews(reg.getSendOrcidNews().getValue()));
        preferences.setSendMemberUpdateRequests(reg.getSendMemberUpdateRequests().getValue());
        preferences.setSendEmailFrequencyDays(reg.getSendEmailFrequencyDays().getValue());
        preferences.setActivitiesVisibilityDefault(new ActivitiesVisibilityDefault(Visibility.fromValue(reg.getActivitiesVisibilityDefault().getVisibility().value())));

        PersonalDetails personalDetails = new PersonalDetails();
        personalDetails.setFamilyName(new FamilyName(reg.getFamilyNames().getValue()));
        personalDetails.setGivenNames(new GivenNames(reg.getGivenNames().getValue()));

        bio.setContactDetails(contactDetails);
        bio.setPersonalDetails(personalDetails);
        OrcidInternal internal = new OrcidInternal();
        internal.setPreferences(preferences);
        profile.setOrcidBio(bio);
        if (!PojoUtil.isEmpty(reg.getReferredBy()))
            internal.setReferredBy(new ReferredBy(reg.getReferredBy().getValue()));

        profile.setOrcidInternal(internal);

        OrcidHistory orcidHistory = new OrcidHistory();
        orcidHistory.setClaimed(new Claimed(true));
        orcidHistory.setCreationMethod(CreationMethod.INTEGRATION);

        profile.setOrcidHistory(orcidHistory);
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));

        profile.setPassword(reg.getPassword().getValue());

        return profile;

    }
}
