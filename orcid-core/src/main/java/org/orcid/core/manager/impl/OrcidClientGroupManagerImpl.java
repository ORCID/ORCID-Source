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
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidClientDetailsService;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.clientgroup.RedirectUriType;
import org.orcid.jaxb.model.message.Biography;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.ResearcherUrl;
import org.orcid.jaxb.model.message.ResearcherUrls;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Url;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.adapter.JpaJaxbEntityAdapter;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.OrcidEntityIdComparator;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.utils.DateUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidClientGroupManagerImpl implements OrcidClientGroupManager {

    @Resource
    private OrcidProfileManager orcidProfileManager;

    @Resource
    ProfileDao profileDao;

    @Resource
    private JpaJaxbEntityAdapter adapter;

    @Resource
    private OrcidClientDetailsService orcidClientDetailsService;

    @Resource
    private EncryptionManager encryptionManager;

    private List<RedirectUri> defaultRedirectUris;

    public void setDefaultRedirectUrisAsWhiteSpaceSeparatedList(String defaultRedirectUrisAsWhiteSpaceSeparatedList) {
        if (defaultRedirectUrisAsWhiteSpaceSeparatedList == null || defaultRedirectUrisAsWhiteSpaceSeparatedList.length() == 0) {
            defaultRedirectUris = Collections.emptyList();
        } else {
            List<String> nonScopedDefaultRedirectUris = Arrays.asList(defaultRedirectUrisAsWhiteSpaceSeparatedList.split("\\s"));
            defaultRedirectUris = new ArrayList<RedirectUri>();
            for (String redirectUriOnly : nonScopedDefaultRedirectUris) {
                defaultRedirectUris.add(new RedirectUri(redirectUriOnly));
            }
        }
    }

    @Override
    @Transactional
    public OrcidClientGroup createOrUpdateOrcidClientGroup(OrcidClientGroup orcidClientGroup, ClientType clientType) {
        String groupOrcid = orcidClientGroup.getGroupOrcid();
        if (groupOrcid == null) {
            // If the incoming client group ORCID is null, then create a new
            // client group.
            OrcidProfile groupProfile = createGroupProfile(orcidClientGroup);
            groupProfile = orcidProfileManager.createOrcidProfile(groupProfile);
            groupOrcid = groupProfile.getOrcid().getValue();
        } else {
            // If the incoming client group ORCID is not null, then lookup the
            // existing client group.
            ProfileEntity groupProfileEntity = profileDao.find(groupOrcid);
            if (groupProfileEntity == null) {
                // If and existing client group can't be found
                // then raise an error.
                throw new OrcidClientGroupManagementException("Group ORCID was specified but does not yet exist: " + groupOrcid);
            } else {
                if (!OrcidType.GROUP.equals(groupProfileEntity.getOrcidType())) {
                    // If profile exists with client group orcid, but is not of
                    // group type, then make it a group
                    groupProfileEntity.setOrcidType(OrcidType.GROUP);
                }
                // If the existing client group is found, then update the name
                // and contact email from the incoming client group, using the
                // profile DAO
                if (!orcidClientGroup.getEmail().equals(groupProfileEntity.getPrimaryEmail().getId())) {
                    EmailEntity primaryEmailEntity = new EmailEntity();
                    primaryEmailEntity.setId(orcidClientGroup.getEmail());
                    primaryEmailEntity.setCurrent(true);
                    primaryEmailEntity.setVerified(false);
                    primaryEmailEntity.setVisibility(Visibility.PRIVATE);
                    groupProfileEntity.setPrimaryEmail(primaryEmailEntity);
                }
                groupProfileEntity.setCreditName(orcidClientGroup.getGroupName());
                groupProfileEntity.setOrcidType(OrcidType.GROUP);
                profileDao.merge(groupProfileEntity);
            }
        }
        // Use the profile DAO to link the clients to the group, so get the
        // group profile entity.
        ProfileEntity groupProfileEntity = profileDao.find(groupOrcid);
        SortedSet<ProfileEntity> clientProfileEntities = groupProfileEntity.getClientProfiles();
        if (clientProfileEntities == null) {
            clientProfileEntities = new TreeSet<ProfileEntity>(new OrcidEntityIdComparator<String>());
            groupProfileEntity.setClientProfiles(clientProfileEntities);
        }
        // For each client in the client group
        for (OrcidClient client : orcidClientGroup.getOrcidClient()) {
            processClient(groupOrcid, clientProfileEntities, client, clientType);
        }
        // Regenerate client group and return.
        return retrieveOrcidClientGroup(groupOrcid);
    }

    private void processClient(String groupOrcid, SortedSet<ProfileEntity> clientProfileEntities, OrcidClient client, ClientType clientType) {
        if (client.getClientId() == null) {
            // If the client ID in the incoming client is null, then create
            // a new client profile.
            OrcidProfile clientProfile = createClientProfile(client);
            clientProfile = orcidProfileManager.createOrcidProfile(clientProfile);
            // Now the client profile has been created, use the profile DAO
            // to link it to the group.
            ProfileEntity clientProfileEntity = profileDao.find(clientProfile.getOrcid().getValue());
            clientProfileEntity.setGroupOrcid(groupOrcid);
            profileDao.merge(clientProfileEntity);
            // And link the client to the copy of the profile cached in
            // memory by Hibernate
            clientProfileEntities.add(clientProfileEntity);
            // Use the client details service to create the client details
            ClientDetailsEntity clientDetailsEntity = createClientDetails(clientProfile.getOrcid().getValue(), client, clientType);
            // And put the client details into the copy of the profile
            // entity cached in memory by Hibernate.
            clientProfileEntity.setClientDetails(clientDetailsEntity);
        } else {
            // If the client ID in the incoming client is not null, then
            // look up the existing client.
            String clientId = client.getClientId();
            ProfileEntity clientProfileEntity = profileDao.find(clientId);
            if (clientProfileEntity == null) {
                // If the existing client can't be found then raise an
                // error.
                throw new OrcidClientGroupManagementException("Unable to find client profile: " + clientId);
            } else {
                if (!OrcidType.CLIENT.equals(clientProfileEntity.getOrcidType())) {
                    // If profile exists with for the client ID, but is not
                    // of client type, then raise an error.
                    throw new OrcidClientGroupManagementException("ORCID exists but is not a client: " + clientId);
                }
                if (!clientProfileEntity.getGroupOrcid().equals(groupOrcid)) {
                    // If client belongs to another group, then raise an
                    // error.
                    throw new OrcidClientGroupManagementException(String.format("Client %s does not belong to group %s (actually belongs to group %s)", clientId,
                            groupOrcid, clientProfileEntity.getGroupOrcid()));
                }
                // If the existing client is found, then update the client
                // details from the incoming client, and save using the profile
                // DAO.
                profileDao.removeChildrenWithGeneratedIds(clientProfileEntity);
                updateProfileEntityFromClient(client, clientProfileEntity);
                profileDao.merge(clientProfileEntity);
            }
        }
    }

    private void updateProfileEntityFromClient(OrcidClient client, ProfileEntity clientProfileEntity) {
        clientProfileEntity.setCreditName(client.getDisplayName());
        clientProfileEntity.setBiography(client.getShortDescription());
        SortedSet<ResearcherUrlEntity> researcherUrls = new TreeSet<ResearcherUrlEntity>();
        researcherUrls.add(new ResearcherUrlEntity(client.getWebsite(), clientProfileEntity));
        clientProfileEntity.setResearcherUrls(researcherUrls);
        ClientDetailsEntity clientDetailsEntity = clientProfileEntity.getClientDetails();
        Set<ClientRedirectUriEntity> clientRedirectUriEntities = clientDetailsEntity.getClientRegisteredRedirectUris();
        Map<String, ClientRedirectUriEntity> clientRedirectUriEntitiesMap = ClientRedirectUriEntity.mapByUri(clientRedirectUriEntities);
        clientRedirectUriEntities.clear();
        Set<RedirectUri> redirectUrisToAdd = new HashSet<RedirectUri>();
        redirectUrisToAdd.addAll(client.getRedirectUris().getRedirectUri());
        redirectUrisToAdd.addAll(defaultRedirectUris);
        for (RedirectUri redirectUri : redirectUrisToAdd) {
            if (clientRedirectUriEntitiesMap.containsKey(redirectUri.getValue())) {
                clientRedirectUriEntities.add(clientRedirectUriEntitiesMap.get(redirectUri.getValue()));
            } else {
                ClientRedirectUriEntity clientRedirectUriEntity = new ClientRedirectUriEntity(redirectUri.getValue(), clientDetailsEntity);
                List<ScopePathType> clientPredefinedScopes = redirectUri.getScope();
                clientRedirectUriEntity.setRedirectUriType(redirectUri.getType().value());
                String allPreDefScopes = null;
                if (clientPredefinedScopes != null) {
                    allPreDefScopes = ScopePathType.getScopesAsSingleString(clientPredefinedScopes);
                }
                clientRedirectUriEntity.setPredefinedClientScope(allPreDefScopes);
                clientRedirectUriEntities.add(clientRedirectUriEntity);
            }
        }
    }

    @Override
    @Transactional
    public OrcidClientGroup retrieveOrcidClientGroup(String groupOrcid) {
        ProfileEntity profileEntity = profileDao.find(groupOrcid);
        if (profileEntity == null) {
            return null;
        }
        OrcidClientGroup group = adapter.toOrcidClientGroup(profileEntity);
        for (OrcidClient client : group.getOrcidClient()) {
            client.setClientSecret(encryptionManager.decryptForInternalUse(client.getClientSecret()));
        }
        return group;
    }

    private OrcidProfile createGroupProfile(OrcidClientGroup orcidClientGroup) {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setType(OrcidType.GROUP);
        OrcidHistory orcidHistory = new OrcidHistory();
        orcidProfile.setOrcidHistory(orcidHistory);
        orcidHistory.setClaimed(new Claimed(true));
        orcidHistory.setSubmissionDate(new SubmissionDate(DateUtils.convertToXMLGregorianCalendar(new Date())));
        OrcidBio orcidBio = new OrcidBio();
        orcidProfile.setOrcidBio(orcidBio);
        PersonalDetails personalDetails = new PersonalDetails();
        orcidBio.setPersonalDetails(personalDetails);
        personalDetails.setCreditName(new CreditName(orcidClientGroup.getGroupName()));
        ContactDetails contactDetails = new ContactDetails();
        orcidBio.setContactDetails(contactDetails);
        Email primaryEmail = new Email(orcidClientGroup.getEmail());
        primaryEmail.setVisibility(Visibility.PRIVATE);
        contactDetails.addOrReplacePrimaryEmail(primaryEmail);
        return orcidProfile;
    }

    private OrcidProfile createClientProfile(OrcidClient orcidClient) {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setType(OrcidType.CLIENT);
        OrcidBio orcidBio = new OrcidBio();
        orcidProfile.setOrcidBio(orcidBio);
        PersonalDetails personalDetails = new PersonalDetails();
        orcidBio.setPersonalDetails(personalDetails);
        personalDetails.setCreditName(new CreditName(orcidClient.getDisplayName()));
        // Add website as researcher url
        if (StringUtils.isNotBlank(orcidClient.getWebsite())) {
            ResearcherUrls researcherUrls = new ResearcherUrls();
            researcherUrls.getResearcherUrl().add(new ResearcherUrl(new Url(orcidClient.getWebsite())));
            orcidBio.setResearcherUrls(researcherUrls);
        }
        orcidBio.setBiography(new Biography(orcidClient.getShortDescription()));
        return orcidProfile;
    }

    private ClientDetailsEntity createClientDetails(String orcid, OrcidClient orcidClient, ClientType clientType) {
        Set<String> clientResourceIds = new HashSet<String>();
        clientResourceIds.add("orcid");
        Set<String> clientAuthorizedGrantTypes = new HashSet<String>();
        clientAuthorizedGrantTypes.add("client_credentials");
        clientAuthorizedGrantTypes.add("authorization_code");
        clientAuthorizedGrantTypes.add("refresh_token");
        Set<RedirectUri> redirectUrisToAdd = new HashSet<RedirectUri>();
        redirectUrisToAdd.addAll(orcidClient.getRedirectUris().getRedirectUri());
        redirectUrisToAdd.addAll(defaultRedirectUris);
        List<String> clientGrantedAuthorities = new ArrayList<String>();
        clientGrantedAuthorities.add("ROLE_CLIENT");

        ClientDetailsEntity clientDetails = orcidClientDetailsService.createClientDetails(orcid, createScopes(clientType), clientResourceIds, clientAuthorizedGrantTypes,
                redirectUrisToAdd, clientGrantedAuthorities);
        return clientDetails;
    }

    private Set<String> createScopes(ClientType clientType) {
        switch (clientType) {
        case PREMIUM_CREATOR:
            return premiumCreatorScopes();
        case CREATOR:
            return creatorScopes();
        case PREMIUM_UPDATER:
            return premiumUpdaterScopes();
        case UPDATER:
            return updaterScopes();
        default:
            throw new IllegalArgumentException("Unsupported client type: " + clientType);
        }
    }

    private Set<String> premiumCreatorScopes() {
        Set<String> creatorScopes = creatorScopes();
        creatorScopes.add(ScopePathType.WEBHOOK.value());
        return creatorScopes;
    }

    private Set<String> creatorScopes() {
        return ScopePathType.ORCID_PROFILE_CREATE.getCombinedAsStrings();
    }

    private Set<String> premiumUpdaterScopes() {
        Set<String> updaterScopes = updaterScopes();
        updaterScopes.add(ScopePathType.WEBHOOK.value());
        return updaterScopes;
    }

    private Set<String> updaterScopes() {
        return new HashSet<>(ScopePathType.getScopesAsStrings(ScopePathType.AUTHENTICATE, ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.ORCID_WORKS_READ_LIMITED,
                ScopePathType.ORCID_PROFILE_READ_LIMITED, ScopePathType.ORCID_WORKS_UPDATE, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE,
                ScopePathType.ORCID_BIO_UPDATE, ScopePathType.ORCID_WORKS_CREATE));
    }

    @Override
    public void removeOrcidClientGroup(String groupOrcid) {
        this.orcidProfileManager.deleteProfile(groupOrcid);
    }

    public OrcidProfileManager getOrcidProfileManager() {
        return orcidProfileManager;
    }
}
