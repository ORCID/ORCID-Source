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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
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
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.OrgDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileFundingDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientSecretEntity;
import org.orcid.pojo.ajaxForm.Member;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Registration;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.utils.DateUtils;
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
    
    @Resource
    private ClientDetailsManager clientDetailsManager;
    
    @Resource
    private JpaJaxbEntityAdapter adapter;
    
    @Resource
    private OrgDao orgDao;
            
    @Resource
    private ProfileFundingDao profileFundingDao;
    
    @Resource
    private OrgAffiliationRelationDao orgAffiliationRelationDao;
    
    @Resource
    private EncryptionManager encryptionManager;    
    
    @Resource
    private ProfileEntityManager profileEntityManager;   
    
    @Resource
    private WorkManager workManager;
    
    //Map containing a list of members, the key is the group type, there will be one member for each group type
    private Map<String, Member> members = new HashMap<String, Member>();
    
    //Map containing a list of clients, the key is the member orcid
    private Map<String, OrcidClient> clients = new HashMap<String, OrcidClient>();
    
    public void deleteProfile(String orcid) throws Exception {        
        orcidProfileManager.deactivateOrcidProfile(orcidProfileManager.retrieveOrcidProfile(orcid));
        orcidProfileManager.deleteProfile(orcid);
    }

    public void deleteClient(String clientId) throws Exception {
        List<ClientSecretEntity> clientSecrets = clientDetailsDao.getClientSecretsByClientId(clientId);
        //Remove the client secret
        for(ClientSecretEntity entity : clientSecrets) {
            clientDetailsDao.removeClientSecret(clientId, entity.getClientSecret());
        }
        //Remove fundings where he is the source
        profileFundingDao.removeFundingByClientSourceId(clientId);
        //Remove affiliations where he is the source
        orgAffiliationRelationDao.removeOrgAffiliationByClientSourceId(clientId);
        //Remove orgs where he is the source
        orgDao.removeOrgsByClientSourceId(clientId);
        //remove the client
        clientDetailsDao.removeClient(clientId);
    }
    
    public Member createMember(MemberType type) throws Exception {
        if(members.containsKey(type.value())) {            
            Member group = members.get(type.value());
            if(this.profileEntityManager.orcidExists(group.getGroupOrcid().getValue()))
                return group;
        }
        
        String name = type.value() + System.currentTimeMillis() + "@orcid-integration-test.com";
        Member group = new Member();
        group.setEmail(Text.valueOf(name));
        group.setGroupName(Text.valueOf(name));
        group.setType(Text.valueOf(type.value()));
        group.setErrors(new ArrayList<String>());

        OrcidClientGroup clientGroup = orcidClientGroupManager.createGroup(group.toOrcidClientGroup());
        assertNotNull(clientGroup);
        assertFalse(PojoUtil.isEmpty(clientGroup.getGroupOrcid()));
        group.setGroupOrcid(Text.valueOf(clientGroup.getGroupOrcid()));
        members.put(type.value(), group);
        return group;
    }

    public OrcidClient createClient(String groupOrcid, String redirectUri) throws Exception {
        if(clients.containsKey(groupOrcid)) {
            return clients.get(groupOrcid);
        }
        
        MemberType groupType = profileDao.getGroupType(groupOrcid);
        ClientType clientType = null;
        if(groupType == null)
            return null;
        switch (groupType){
        case BASIC:
            clientType = ClientType.UPDATER;
            break;
        case BASIC_INSTITUTION:
            clientType = ClientType.PREMIUM_UPDATER;
            break;
        case PREMIUM: 
            clientType = ClientType.CREATOR;
            break;
        case PREMIUM_INSTITUTION:
            clientType = ClientType.PREMIUM_CREATOR;
            break;
        }
        
        Set<String> clientResourceIds = new HashSet<String>();
        clientResourceIds.add("orcid");
        Set<String> clientAuthorizedGrantTypes = new HashSet<String>();
        clientAuthorizedGrantTypes.add("client_credentials");
        clientAuthorizedGrantTypes.add("authorization_code");
        clientAuthorizedGrantTypes.add("refresh_token");
        
        RedirectUri rUri = new RedirectUri();        
        rUri.setValue(redirectUri);
        rUri.setType(RedirectUriType.DEFAULT);
        Set<RedirectUri> redirectUrisToAdd = new HashSet<RedirectUri>();
        redirectUrisToAdd.add(rUri);        
        
        List<String> clientGrantedAuthorities = new ArrayList<String>();
        clientGrantedAuthorities.add("ROLE_CLIENT");

        String value = groupOrcid + "_client_" + System.currentTimeMillis();
        String name = value;
        String description = value;
        String website = value + "_website";
            
        ClientDetailsEntity clientDetails = clientDetailsManager.createClientDetails(groupOrcid, name, description, website, clientType, createScopes(clientType),
                clientResourceIds, clientAuthorizedGrantTypes, redirectUrisToAdd, clientGrantedAuthorities);
        
        OrcidClient client =  adapter.toOrcidClient(clientDetails);
        //Decrypt the client secret
        client.setClientSecret(encryptionManager.decryptForInternalUse(client.getClientSecret()));
        
        clients.put(groupOrcid, client);
        
        return client;
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
        registration.setActivitiesVisibilityDefault(org.orcid.pojo.ajaxForm.Visibility.valueOf(Visibility.LIMITED));
        OrcidProfile orcidProfile = toProfile(registration);
        orcidProfile = orcidProfileManager.createOrcidProfile(orcidProfile, false, false);
        return orcidProfile;
    }

    public boolean lockProfile(String orcid) throws Exception {
        return profileEntityManager.lockProfile(orcid);
    }
    
    public boolean unlockProfile(String orcid) throws Exception {
        return profileEntityManager.unlockProfile(orcid);
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
        orcidHistory.setCreationMethod(CreationMethod.INTEGRATION_TEST);

        profile.setOrcidHistory(orcidHistory);
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));

        profile.setPassword(reg.getPassword().getValue());

        return profile;

    }
    
    private Set<String> createScopes(ClientType clientType) {
        switch (clientType) {
        case PREMIUM_CREATOR:
            return orcidClientGroupManager.premiumCreatorScopes();
        case CREATOR:
            return orcidClientGroupManager.creatorScopes();
        case PREMIUM_UPDATER:
            return orcidClientGroupManager.premiumUpdaterScopes();
        case UPDATER:
            return orcidClientGroupManager.updaterScopes();
        default:
            throw new IllegalArgumentException("Unsupported client type: " + clientType);
        }
    }
}
