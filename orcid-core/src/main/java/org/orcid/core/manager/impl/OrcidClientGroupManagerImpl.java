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
import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidClientDetailsService;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.GroupType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
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
    public OrcidClient createOrUpdateOrcidClientGroup(String groupOrcid, OrcidClient orcidClient) {
        OrcidClient result = null;
        // Use the profile DAO to link the clients to the group, so get the
        // group profile entity.
        ProfileEntity groupProfileEntity = profileDao.find(groupOrcid);
        SortedSet<ProfileEntity> clientProfileEntities = groupProfileEntity.getClientProfiles();
        if (clientProfileEntities == null) {
            clientProfileEntities = new TreeSet<ProfileEntity>(new OrcidEntityIdComparator<String>());
            groupProfileEntity.setClientProfiles(clientProfileEntities);
        }
        
        processClient(groupOrcid, clientProfileEntities, orcidClient, getClientType(groupProfileEntity.getGroupType()));

        OrcidClientGroup group = retrieveOrcidClientGroup(groupOrcid);

        for (OrcidClient populatedClient : group.getOrcidClient()) {
            if (compareClients(orcidClient, populatedClient))
                result = populatedClient;
        }

        // Regenerate client group and return.
        return result;
    }

    @Override
    @Transactional
    public OrcidClientGroup createOrUpdateOrcidClientGroupForAPIRequest(OrcidClientGroup orcidClientGroup) {
        OrcidClientGroup result = createOrUpdateOrcidClientGroup(orcidClientGroup);
        modifyClientTypesForAPIRequest(result);
        return result;
    }
    
    /**
     * Modifies the client types for API calls, so, the resulting client types will be only 
     * creator or updater instead of "premium-creator" or "premium-updater"
     * @param orcidClientGroup
     *          The client group with the list of clients associated with this group
     * */
    private void modifyClientTypesForAPIRequest(OrcidClientGroup orcidClientGroup){
        for(OrcidClient client : orcidClientGroup.getOrcidClient()){
            if(client.getType().equals(ClientType.PREMIUM_CREATOR))
                client.setType(ClientType.CREATOR);
            else if(client.getType().equals(ClientType.PREMIUM_UPDATER))
                client.setType(ClientType.UPDATER);
        }
    }
    
    @Override
    @Transactional
    public OrcidClientGroup createOrUpdateOrcidClientGroup(OrcidClientGroup orcidClientGroup) {
        // For each client in the client group, validate the client type
        for (OrcidClient client : orcidClientGroup.getOrcidClient()) {
            checkClientType(client.getType(), orcidClientGroup.getType());
        }
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
                // If the existing client group is found, then update the name
                // and contact email from the incoming client group, using the
                // profile DAO
                if (!orcidClientGroup.getEmail().equals(groupProfileEntity.getPrimaryEmail().getId())) {
                    EmailEntity primaryEmailEntity = new EmailEntity();
                    primaryEmailEntity.setId(orcidClientGroup.getEmail().toLowerCase().trim());
                    primaryEmailEntity.setCurrent(true);
                    primaryEmailEntity.setVerified(true);
                    primaryEmailEntity.setVisibility(Visibility.PRIVATE);
                    groupProfileEntity.setPrimaryEmail(primaryEmailEntity);
                }
                groupProfileEntity.setCreditName(orcidClientGroup.getGroupName());
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
            processClient(groupOrcid, clientProfileEntities, client, getClientType(groupProfileEntity.getGroupType()));
        }
        // Regenerate client group and return.
        return retrieveOrcidClientGroup(groupOrcid);
    }

    /**
     * Check if the client type matches the types that the group is allowed to add.
     * @param clientType
     *          Type of the client that want to be created 
     * @param groupType
     *          Group type
     * @throws OrcidClientGroupManagementException if the client type cant be added by this group
     * */
    private void checkClientType(ClientType clientType, GroupType groupType) {
        switch(groupType){
        case BASIC:
        case PREMIUM:        
            if(clientType.equals(ClientType.CREATOR) || clientType.equals(ClientType.PREMIUM_CREATOR))
                throw new OrcidClientGroupManagementException("Groups of type basic or premium can only create updator clients");
            break;
        case BASIC_INSTITUTION:
        case PREMIUM_INSTITUTION:
            if(clientType.equals(ClientType.UPDATER) || clientType.equals(ClientType.PREMIUM_UPDATER))
                throw new OrcidClientGroupManagementException("Groups of type basic-institution or premium-institution can only create creator clients");
            break;
        }
    }
    
    
    /**
     * Creates a new client and set the group orcid as the owner of that client
     * 
     * @param groupOrcid
     *            The group owner for this client
     * @param client
     *            The new client
     * @return the new OrcidClient
     * */
    public OrcidClient createAndPersistClientProfile(String groupOrcid, OrcidClient client) throws OrcidClientGroupManagementException {
        ProfileEntity groupProfileEntity = profileDao.find(groupOrcid);
        //Sets the client type
        setClientType(groupProfileEntity.getGroupType(), client);        
        if (!isAllowedToAddNewClient(groupOrcid))
            throw new OrcidClientGroupManagementException("Your contract allow you to have only 1 client.");        
        // Create a new client profile for the orcidClient.
        OrcidProfile clientProfile = createClientProfile(client);
        clientProfile = orcidProfileManager.createOrcidProfile(clientProfile);
        // Now the client profile has been created, use the profile DAO
        // to link it to the group.
        ProfileEntity clientProfileEntity = profileDao.find(clientProfile.getOrcid().getValue());
        clientProfileEntity.setGroupOrcid(groupOrcid);
        profileDao.merge(clientProfileEntity);
        // And link the client to the copy of the profile cached in
        // memory by Hibernate
        // Use the profile DAO to link the clients to the group, so get the
        // group profile entity.        
        SortedSet<ProfileEntity> clientProfileEntities = groupProfileEntity.getClientProfiles();
        if (clientProfileEntities == null) {
            clientProfileEntities = new TreeSet<ProfileEntity>(new OrcidEntityIdComparator<String>());
            groupProfileEntity.setClientProfiles(clientProfileEntities);
        }
        clientProfileEntities.add(clientProfileEntity);
        // Use the client details service to create the client details
        ClientDetailsEntity clientDetailsEntity = createClientDetails(clientProfile.getOrcid().getValue(), client, client.getType());
        // And put the client details into the copy of the profile
        // entity cached in memory by Hibernate.
        clientProfileEntity.setClientDetails(clientDetailsEntity);

        return adapter.toOrcidClient(clientProfileEntity);
    }

    /**
     * Check if the group can add more clients. Rules: BASIC and
     * BASIC_INSTITUTION can have only one client
     * 
     * @param groupOrcid
     * @return true if the group matches the rules described above
     * */
    private boolean isAllowedToAddNewClient(String groupOrcid) {
        OrcidClientGroup group = retrieveOrcidClientGroup(groupOrcid);
        
        // If this is a basic group or a basic institution group, it should
        // be allowed to have only one client
        if (group.getType().equals(GroupType.BASIC) || group.getType().equals(GroupType.BASIC_INSTITUTION)) {
            if (!group.getOrcidClient().isEmpty())
                return false;
        }
        
        return true;
    }

    /**
     * Returns the client type based on the group type
     * @param groupType
     * @return the client type associated with the given group type
     * */
    private ClientType getClientType(GroupType groupType) {
        ClientType clientType = null;
        switch(groupType){
        case BASIC:
            clientType = ClientType.UPDATER;
            break;
        case PREMIUM:
            clientType = ClientType.PREMIUM_UPDATER;
            break;
        case BASIC_INSTITUTION:
            clientType = ClientType.CREATOR;
            break;
        case PREMIUM_INSTITUTION:
            clientType = ClientType.PREMIUM_CREATOR;
            break;
        default:
            //This should never happen
            throw new OrcidClientGroupManagementException("Inexisting group type: " + groupType);
        }
        return clientType;
    }

    /**
     * Updates a client profile, updates can be adding or removing redirect uris
     * or updating the client fields
     * 
     * @param groupOrcid
     *            The group owner for this client
     * @param client
     *            The updated client
     * @return the updated OrcidClient
     * */
    public OrcidClient updateClientProfile(String groupOrcid, OrcidClient client) {
        ProfileEntity clientProfileEntity = null;
        if (client.getClientId() != null) {
            // Look up the existing client.
            String clientId = client.getClientId();
            clientProfileEntity = profileDao.find(clientId);
            if (clientProfileEntity == null) {
                // If the existing client can't be found then raise an
                // error.
                throw new OrcidClientGroupManagementException("Unable to find client profile: " + clientId);
            } else {
                if (clientProfileEntity.getClientType() == null) {
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
                updateProfileEntityFromClient(client, clientProfileEntity, true);
                profileDao.merge(clientProfileEntity);
            }

        }

        return adapter.toOrcidClient(clientProfileEntity);
    }

    /**
     * Get a client and evaluates if it is new or it is an update and act
     * accordingly.
     * 
     * @param groupOrcid
     *            The client owner
     * @param clientProfileEntities
     *            The cached list of profiles
     * @param client
     *            The client that is being evaluated
     * @param clientType
     *            The type of client
     * */
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
                if (clientProfileEntity.getClientType() == null) {
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
                updateProfileEntityFromClient(client, clientProfileEntity, true);
                profileDao.merge(clientProfileEntity);
            }
        }
    }

    /**
     * Updates an existing profile entity with the information that comes in a
     * OrcidClient
     * 
     * @param client
     *            The OrcidClient that contains the new information
     * @param clientProfileEntity
     *            The client profile entity that will be updated
     * @param isUpdate
     *            Indicates if this will be an update or is a new client
     * */
    private void updateProfileEntityFromClient(OrcidClient client, ProfileEntity clientProfileEntity, boolean isUpdate) {
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
        if (!isUpdate)
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
        orcidProfile.setGroupType(orcidClientGroup.getType());
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
        orcidProfile.setClientType(orcidClient.getType());
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
        if(orcidClient.getRedirectUris() != null) {        	
        	redirectUrisToAdd.addAll(orcidClient.getRedirectUris().getRedirectUri());
        	redirectUrisToAdd.addAll(defaultRedirectUris);
        }
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
                ScopePathType.ORCID_PROFILE_READ_LIMITED, ScopePathType.ORCID_WORKS_UPDATE, ScopePathType.AFFILIATIONS_UPDATE,
                ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE, ScopePathType.ORCID_BIO_UPDATE, ScopePathType.ORCID_WORKS_CREATE, ScopePathType.AFFILIATIONS_CREATE));
    }

    /**
     * Deletes a group
     * 
     * @param groupOrcid
     *            The orcid of the group that wants to be deleted
     * */
    @Override
    public void removeOrcidClientGroup(String groupOrcid) {
        this.orcidProfileManager.deleteProfile(groupOrcid);
    }

    public OrcidProfileManager getOrcidProfileManager() {
        return orcidProfileManager;
    }

    /**
     * Sets the client type based on the group type
     * */
    private void setClientType(GroupType groupType, OrcidClient client){
        switch(groupType){
        case BASIC: 
            client.setType(ClientType.UPDATER);
            break;
        case BASIC_INSTITUTION:
            client.setType(ClientType.CREATOR);
            break;
        case PREMIUM: 
            client.setType(ClientType.PREMIUM_UPDATER);
            break;
        case PREMIUM_INSTITUTION: 
            client.setType(ClientType.PREMIUM_CREATOR);
            break;            
        }
    }
    
    /**
     * Compare two client objects, one sent by the user and another one already
     * inserted on database. They both will be the same OrcidClient if they
     * share all properties but the key and secret.
     * 
     * @param original
     *            Orcid client sent by the user form the UI
     * @param withPopulatedKeys
     *            Orcid client populated from database
     * @return true if the orcidClient original is "equals" to the populated
     *         orcidClient
     * */
    private boolean compareClients(OrcidClient original, OrcidClient withPopulatedKeys) {
        if (original == null)
            return false;
        if (withPopulatedKeys == null)
            return false;

        if (!original.getDisplayName().equals(withPopulatedKeys.getDisplayName()))
            return false;

        if (!original.getWebsite().equals(withPopulatedKeys.getWebsite()))
            return false;

        if (!original.getShortDescription().equals(withPopulatedKeys.getShortDescription()))
            return false;

        List<RedirectUri> originalUris = original.getRedirectUris().getRedirectUri();
        List<RedirectUri> withPopulatedKeysUris = withPopulatedKeys.getRedirectUris().getRedirectUri();

        if (!originalUris.containsAll(withPopulatedKeysUris) || !withPopulatedKeysUris.containsAll(originalUris))
            return false;

        return true;
    }
}
