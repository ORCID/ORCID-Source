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
package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbEntityAdapter;
import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.OrcidClientGroupManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.clientgroup.ClientType;
import org.orcid.jaxb.model.clientgroup.MemberType;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.message.Claimed;
import org.orcid.jaxb.model.message.ContactDetails;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.Email;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidHistory;
import org.orcid.jaxb.model.message.OrcidInternal;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidType;
import org.orcid.jaxb.model.message.PersonalDetails;
import org.orcid.jaxb.model.message.SalesforceId;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.SubmissionDate;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.dao.ClientScopeDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.ClientScopeEntity;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.OrcidEntityIdComparator;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
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
    private ClientDetailsManager clientDetailsManager;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    private ClientScopeDao clientScopeDao;

    @Override
    @Transactional
    public OrcidClient createOrUpdateOrcidClientGroup(String groupOrcid, OrcidClient orcidClient) {
        OrcidClient result = null;
        // Use the profile DAO to link the clients to the group, so get the
        // group profile entity.
        ProfileEntity groupProfileEntity = profileDao.find(groupOrcid);
        SortedSet<ClientDetailsEntity> clientDetailsEntities = groupProfileEntity.getClients();
        if (clientDetailsEntities == null) {
            clientDetailsEntities = new TreeSet<>(new OrcidEntityIdComparator<String>());
            groupProfileEntity.setClients(clientDetailsEntities);
        }

        processClient(groupOrcid, clientDetailsEntities, orcidClient, getClientType(groupProfileEntity.getGroupType()));

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
     * Modifies the client types for API calls, so, the resulting client types
     * will be only creator or updater instead of "premium-creator" or
     * "premium-updater"
     * 
     * @param orcidClientGroup
     *            The client group with the list of clients associated with this
     *            group
     * */
    private void modifyClientTypesForAPIRequest(OrcidClientGroup orcidClientGroup) {
        for (OrcidClient client : orcidClientGroup.getOrcidClient()) {
            if (client.getType().equals(ClientType.PREMIUM_CREATOR))
                client.setType(ClientType.CREATOR);
            else if (client.getType().equals(ClientType.PREMIUM_UPDATER))
                client.setType(ClientType.UPDATER);
        }
    }

    @Override
    @Transactional
    public OrcidClientGroup createOrUpdateOrcidClientGroup(OrcidClientGroup orcidClientGroup) {
        // For each client in the client group, validate the client type
        for (OrcidClient client : orcidClientGroup.getOrcidClient()) {
            checkAndSetClientType(client, orcidClientGroup.getType());
        }
        String groupOrcid = orcidClientGroup.getGroupOrcid();
        if (groupOrcid == null) {
            orcidClientGroup = createGroup(orcidClientGroup);
            groupOrcid = orcidClientGroup.getGroupOrcid();
        } else {
            updateGroup(orcidClientGroup);
        }
        // Use the profile DAO to link the clients to the group, so get the
        // group profile entity.
        ProfileEntity groupProfileEntity = profileDao.find(groupOrcid);
        SortedSet<ClientDetailsEntity> clientProfileEntities = groupProfileEntity.getClients();
        if (clientProfileEntities == null) {
            clientProfileEntities = new TreeSet<>(new OrcidEntityIdComparator<String>());
            groupProfileEntity.setClients(clientProfileEntities);
        }
        // For each client in the client group
        for (OrcidClient client : orcidClientGroup.getOrcidClient()) {
            processClient(groupOrcid, clientProfileEntities, client, getClientType(groupProfileEntity.getGroupType()));
        }
        // Regenerate client group and return.
        return retrieveOrcidClientGroup(groupOrcid);
    }

    /**
     * Creates a group profile. If the OrcidClientGroup provided already
     * contains a groupOrcid, it will just return it, if it doesnt, it will
     * create the profile and update the parameter.
     * 
     * @param orcidClientGroup
     *            the group to be created
     * @return the group updated with his orcid id.
     * */
    public OrcidClientGroup createGroup(OrcidClientGroup orcidClientGroup) {
        String groupOrcid = orcidClientGroup.getGroupOrcid();
        if (PojoUtil.isEmpty(groupOrcid)) {
            OrcidProfile groupProfile = createGroupProfile(orcidClientGroup);
            groupProfile = orcidProfileManager.createOrcidProfile(groupProfile, false, false);
            groupOrcid = groupProfile.getOrcidIdentifier().getPath();
            orcidClientGroup.setGroupOrcid(groupOrcid);
        }

        return orcidClientGroup;
    }

    /**
     * Updates an existing group profile. If the group doesnt exists it will
     * throw a OrcidClientGroupManagementException
     * 
     * @param orcidClientGroup
     *            The group to be updated
     * */
    @Transactional
    public void updateGroup(OrcidClientGroup orcidClientGroup) {
        String groupOrcid = orcidClientGroup.getGroupOrcid();
        // If the incoming client group ORCID is not null, then lookup the
        // existing client group.
        ProfileEntity groupProfileEntity = profileDao.find(groupOrcid);
        if (groupProfileEntity == null) {
            // If and existing client group can't be found
            // then raise an error.
            throw new OrcidClientGroupManagementException("Group ORCID was specified but does not yet exist: " + groupOrcid);
        } else {
            boolean updateClientScopes = false;
            // If the existing client group is found, then update the type, name
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
            if(groupProfileEntity.getRecordNameEntity() == null) {
                groupProfileEntity.setRecordNameEntity(new RecordNameEntity());
                groupProfileEntity.getRecordNameEntity().setProfile(groupProfileEntity);
            }
            //Set the record name entity table
            groupProfileEntity.getRecordNameEntity().setCreditName(orcidClientGroup.getGroupName());
            groupProfileEntity.getRecordNameEntity().setVisibility(org.orcid.jaxb.model.common_rc2.Visibility.PUBLIC);
            
            //TODO: remove after names migration
            //Set the profile table credit name
            groupProfileEntity.setCreditName(orcidClientGroup.getGroupName());
            groupProfileEntity.setNamesVisibility(Visibility.PUBLIC);
            
            groupProfileEntity.setSalesforeId(orcidClientGroup.getSalesforceId());
            // If group type changed
            if (!groupProfileEntity.getGroupType().equals(orcidClientGroup.getType())) {
                // Update the group type
                groupProfileEntity.setGroupType(orcidClientGroup.getType());
                // Set the flag to update the client scopes
                updateClientScopes = true;
            }
            // Merge changes
            profileDao.merge(groupProfileEntity);
            profileDao.updateLastModifiedDate(groupOrcid);
            // Update client types and scopes
            if (updateClientScopes)
                updateClientTypeDueGroupTypeUpdate(groupProfileEntity);
        }
    }

    /**
     * Updates the client type and client scopes of all clients that belongs to
     * the given group
     * 
     * @param groupProfileEntity
     *            the group profile
     * */
    @Transactional
    private void updateClientTypeDueGroupTypeUpdate(ProfileEntity groupProfileEntity) {
        Set<ClientDetailsEntity> clients = groupProfileEntity.getClients();
        ClientType clientType = this.getClientType(groupProfileEntity.getGroupType());
        for (ClientDetailsEntity client : clients) {
            Set<String> newSetOfScopes = this.createScopes(clientType);
            Set<ClientScopeEntity> existingScopes = client.getClientScopes();
            Iterator<ClientScopeEntity> scopesIterator = existingScopes.iterator();
            while (scopesIterator.hasNext()) {
                ClientScopeEntity clientScopeEntity = scopesIterator.next();
                if (newSetOfScopes.contains(clientScopeEntity.getScopeType())) {
                    newSetOfScopes.remove(clientScopeEntity.getScopeType());
                } else {
                    clientScopeDao.deleteScope(client.getClientId(), clientScopeEntity.getScopeType());
                }
            }

            // Insert the new scopes
            for (String newScope : newSetOfScopes) {
                ClientScopeEntity clientScopeEntity = new ClientScopeEntity();
                clientScopeEntity.setClientDetailsEntity(client);
                clientScopeEntity.setScopeType(newScope);
                clientScopeEntity.setDateCreated(new Date());
                clientScopeEntity.setLastModified(new Date());
                clientScopeDao.persist(clientScopeEntity);
            }

            // Update client type
            clientDetailsDao.updateClientType(clientType, client.getClientId());
            // Update last modified
            clientDetailsDao.updateLastModified(client.getClientId());
        }
    }

    /**
     * If the client type is set, check if the client type matches the types
     * that the group is allowed to add. If the client type is null, assig it
     * based on the group type
     * 
     * @param clientType
     *            Type of the client that want to be created
     * @param groupType
     *            Group type
     * @throws OrcidClientGroupManagementException
     *             if the client type cant be added by this group
     * */
    private void checkAndSetClientType(OrcidClient client, MemberType groupType) {
        ClientType clientType = client.getType();
        if (clientType != null) {
            switch (groupType) {
            case BASIC:
            case PREMIUM:
                if (clientType.equals(ClientType.CREATOR) || clientType.equals(ClientType.PREMIUM_CREATOR))
                    throw new OrcidClientGroupManagementException("Groups of type basic or premium can only create updator clients");
                break;
            case BASIC_INSTITUTION:
            case PREMIUM_INSTITUTION:
                if (clientType.equals(ClientType.UPDATER) || clientType.equals(ClientType.PREMIUM_UPDATER))
                    throw new OrcidClientGroupManagementException("Groups of type basic-institution or premium-institution can only create creator clients");
                break;
            }
        } else {
            setClientType(groupType, client);
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
        if (!isAllowedToAddNewClient(groupOrcid))
            throw new OrcidClientGroupManagementException("Your contract allows you to have only 1 client.");
        ProfileEntity groupProfileEntity = profileDao.find(groupOrcid);
        checkAndSetClientType(client, groupProfileEntity.getGroupType());
        // Use the client details service to create the client details
        ClientDetailsEntity clientDetailsEntity = createClientDetails(groupOrcid, client, client.getType());
        // Link the client to the copy of the profile cached in
        // memory by Hibernate
        SortedSet<ClientDetailsEntity> clientProfileEntities = groupProfileEntity.getClients();
        if (clientProfileEntities == null) {
            clientProfileEntities = new TreeSet<>(new OrcidEntityIdComparator<String>());
            groupProfileEntity.setClients(clientProfileEntities);
        }
        clientProfileEntities.add(clientDetailsEntity);
        return adapter.toOrcidClient(clientDetailsEntity);
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
        if (group.getType().equals(MemberType.BASIC) || group.getType().equals(MemberType.BASIC_INSTITUTION)) {
            if (!group.getOrcidClient().isEmpty())
                return false;
        }

        return true;
    }

    /**
     * Returns the client type based on the group type
     * 
     * @param groupType
     * @return the client type associated with the given group type
     * */
    private ClientType getClientType(MemberType groupType) {
        ClientType clientType = null;
        switch (groupType) {
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
            // This should never happen
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
    public OrcidClient updateClient(String groupOrcid, OrcidClient client) {
        ClientDetailsEntity clientDetailsEntity = null;
        if (client.getClientId() != null) {
            // Look up the existing client.
            String clientId = client.getClientId();
            clientDetailsEntity = clientDetailsDao.find(clientId);
            if (clientDetailsEntity == null) {
                // If the existing client can't be found then raise an
                // error.
                throw new OrcidClientGroupManagementException("Unable to find client: " + clientId);
            } else {
                if (!PojoUtil.isEmpty(clientDetailsEntity.getGroupProfileId()) && !clientDetailsEntity.getGroupProfileId().equals(groupOrcid)) {
                    // If client belongs to another group, then raise an
                    // error.
                    throw new OrcidClientGroupManagementException(String.format("Client %s does not belong to group %s (actually belongs to group %s)", clientId,
                            groupOrcid, clientDetailsEntity.getGroupProfileId()));
                }
                // If the existing client is found, then update the client
                // details from the incoming client, and save using the client
                // details manager
                updateClientDetailsEntityFromClient(client, clientDetailsEntity, true);
                clientDetailsManager.merge(clientDetailsEntity);
            }
        }
        return adapter.toOrcidClient(clientDetailsEntity);
    }

    /**
     * Updates a client profile, updates can be adding or removing redirect uris
     * or updating the client fields
     * 
     * @param client
     *            The updated client
     * @return the updated OrcidClient
     * */
    public OrcidClient updateClient(OrcidClient client) {
        ClientDetailsEntity clientDetailsEntity = null;
        if (client.getClientId() != null) {
            // Look up the existing client.
            String clientId = client.getClientId();
            clientDetailsEntity = clientDetailsDao.find(clientId);
            if (clientDetailsEntity == null) {
                // If the existing client can't be found then raise an
                // error.
                throw new OrcidClientGroupManagementException("Unable to find client: " + clientId);
            } else {
                // If the existing client is found, then update the client
                // details from the incoming client, and save using the
                // client details manager.
                updateClientDetailsEntityFromClient(client, clientDetailsEntity, true);
                clientDetailsManager.merge(clientDetailsEntity);
            }
        }
        return adapter.toOrcidClient(clientDetailsEntity);
    }

    /**
     * Get a client and evaluates if it is new or it is an update and act
     * accordingly.
     * 
     * @param groupOrcid
     *            The client owner
     * @param clientDetailsEntities
     *            The cached list of clients
     * @param client
     *            The client that is being evaluated
     * @param clientType
     *            The type of client
     * */
    private void processClient(String groupOrcid, SortedSet<ClientDetailsEntity> clientDetailsEntities, OrcidClient client, ClientType clientType) {
        if (client.getClientId() == null) {
            // If the client ID in the incoming client is null, then create
            // a new client.
            // Use the client details service to create the client details
            ClientDetailsEntity clientDetailsEntity = createClientDetails(groupOrcid, client, clientType);
            // And link the client to the copy of the profile cached in
            // memory by Hibernate
            clientDetailsEntities.add(clientDetailsEntity);
        } else {
            // If the client ID in the incoming client is not null, then
            // look up the existing client.
            String clientId = client.getClientId();
            ClientDetailsEntity clientDetailsEntity = clientDetailsManager.findByClientId(clientId);
            if (clientDetailsEntity == null) {
                // If the existing client can't be found then raise an
                // error.
                throw new OrcidClientGroupManagementException("Unable to find client: " + clientId);
            } else {
                if (!PojoUtil.isEmpty(clientDetailsEntity.getGroupProfileId()) && !clientDetailsEntity.getGroupProfileId().equals(groupOrcid)) {
                    // If client belongs to another group, then raise an
                    // error.
                    throw new OrcidClientGroupManagementException(String.format("Client %s does not belong to group %s (actually belongs to group %s)", clientId,
                            groupOrcid, clientDetailsEntity.getGroupProfileId()));
                }
                // If the existing client is found, then update the client
                // details from the incoming client
                updateClientDetailsEntityFromClient(client, clientDetailsEntity, true);
                clientDetailsManager.merge(clientDetailsEntity);
            }
        }
    }

    /**
     * Updates an existing profile entity with the information that comes in a
     * OrcidClient
     * 
     * @param client
     *            The OrcidClient that contains the new information
     * @param clientDetailsEntity
     *            The client profile entity that will be updated
     * @param isUpdate
     *            Indicates if this will be an update or is a new client
     * */
    private void updateClientDetailsEntityFromClient(OrcidClient client, ClientDetailsEntity clientDetailsEntity, boolean isUpdate) {
        clientDetailsEntity.setClientType(client.getType());
        clientDetailsEntity.setClientName(client.getDisplayName());
        clientDetailsEntity.setClientDescription(client.getShortDescription());
        clientDetailsEntity.setClientWebsite(client.getWebsite());
        clientDetailsEntity.setPersistentTokensEnabled(client.isPersistentTokenEnabled());
        Set<ClientRedirectUriEntity> clientRedirectUriEntities = clientDetailsEntity.getClientRegisteredRedirectUris();
        Map<String, ClientRedirectUriEntity> clientRedirectUriEntitiesMap = ClientRedirectUriEntity.mapByUriAndType(clientRedirectUriEntities);
        clientRedirectUriEntities.clear();
        Set<RedirectUri> redirectUrisToAdd = new HashSet<RedirectUri>();
        redirectUrisToAdd.addAll(client.getRedirectUris().getRedirectUri());
        for (RedirectUri redirectUri : redirectUrisToAdd) {
            String rUriKey = ClientRedirectUriEntity.getUriAndTypeKey(redirectUri);
            // If there is a redirect uri with the same uri
            if (clientRedirectUriEntitiesMap.containsKey(rUriKey)) {
                // Check if it have the same scope and update it
                // If it doesnt have the same scope
                ClientRedirectUriEntity existingEntity = clientRedirectUriEntitiesMap.get(rUriKey);
                // Update the scopes
                List<ScopePathType> clientPredefinedScopes = redirectUri.getScope();
                if (clientPredefinedScopes != null) {
                    existingEntity.setPredefinedClientScope(ScopePathType.getScopesAsSingleString(clientPredefinedScopes));
                }
                existingEntity.setUriActType(redirectUri.getActType());
                existingEntity.setUriGeoArea(redirectUri.getGeoArea());
                // Add to the list
                clientRedirectUriEntities.add(existingEntity);
            } else {
                ClientRedirectUriEntity clientRedirectUriEntity = new ClientRedirectUriEntity(redirectUri.getValue(), clientDetailsEntity);
                List<ScopePathType> clientPredefinedScopes = redirectUri.getScope();
                clientRedirectUriEntity.setRedirectUriType(redirectUri.getType().value());
                String allPreDefScopes = null;
                if (clientPredefinedScopes != null) {
                    allPreDefScopes = ScopePathType.getScopesAsSingleString(clientPredefinedScopes);
                }
                clientRedirectUriEntity.setPredefinedClientScope(allPreDefScopes);
                clientRedirectUriEntity.setUriActType(redirectUri.getActType());
                clientRedirectUriEntity.setUriGeoArea(redirectUri.getGeoArea());
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
        primaryEmail.setVerified(true);
        contactDetails.addOrReplacePrimaryEmail(primaryEmail);

        if (!PojoUtil.isEmpty(orcidClientGroup.getSalesforceId())) {
            OrcidInternal orcidInternal = new OrcidInternal();
            orcidInternal.setSalesforceId(new SalesforceId(orcidClientGroup.getSalesforceId()));
            orcidProfile.setOrcidInternal(orcidInternal);
        }

        return orcidProfile;
    }

    private ClientDetailsEntity createClientDetails(String groupOrcid, OrcidClient orcidClient, ClientType clientType) {
        Set<String> clientResourceIds = new HashSet<String>();
        clientResourceIds.add("orcid");
        Set<String> clientAuthorizedGrantTypes = new HashSet<String>();
        clientAuthorizedGrantTypes.add("client_credentials");
        clientAuthorizedGrantTypes.add("authorization_code");
        clientAuthorizedGrantTypes.add("refresh_token");
        Set<RedirectUri> redirectUrisToAdd = new HashSet<RedirectUri>();
        if (orcidClient.getRedirectUris() != null) {
            redirectUrisToAdd.addAll(orcidClient.getRedirectUris().getRedirectUri());
        }
        List<String> clientGrantedAuthorities = new ArrayList<String>();
        clientGrantedAuthorities.add("ROLE_CLIENT");

        String name = orcidClient.getDisplayName();
        String description = orcidClient.getShortDescription();
        String website = orcidClient.getWebsite();

        ClientDetailsEntity clientDetails = clientDetailsManager.createClientDetails(groupOrcid, name, description, website, clientType, createScopes(clientType),
                clientResourceIds, clientAuthorizedGrantTypes, redirectUrisToAdd, clientGrantedAuthorities);
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

    @Override
    public Set<String> premiumCreatorScopes() {
        Set<String> creatorScopes = creatorScopes();
        addPremiumOnlyScopes(creatorScopes);
        return creatorScopes;
    }

    @Override
    public Set<String> creatorScopes() {
        return ScopePathType.ORCID_PROFILE_CREATE.getCombinedAsStrings();
    }

    @Override
    public Set<String> premiumUpdaterScopes() {
        Set<String> updaterScopes = updaterScopes();
        addPremiumOnlyScopes(updaterScopes);
        return updaterScopes;
    }

    @Override
    public Set<String> updaterScopes() {
        return new HashSet<>(ScopePathType.getScopesAsStrings(ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_READ_LIMITED,
                ScopePathType.AFFILIATIONS_UPDATE, ScopePathType.AUTHENTICATE, ScopePathType.FUNDING_CREATE, ScopePathType.FUNDING_READ_LIMITED,
                ScopePathType.FUNDING_UPDATE, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE, ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.ORCID_BIO_UPDATE,
                ScopePathType.ORCID_PROFILE_READ_LIMITED, ScopePathType.ORCID_WORKS_CREATE, ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_WORKS_UPDATE,
                ScopePathType.READ_PUBLIC, ScopePathType.ACTIVITIES_UPDATE, ScopePathType.PERSON_UPDATE, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.READ_LIMITED,
                ScopePathType.PERSON_READ_LIMITED));
    }

    private void addPremiumOnlyScopes(Set<String> scopes) {
        scopes.add(ScopePathType.WEBHOOK.value());
        // scopes.add(ScopePathType.PREMIUM_NOTIFICATION.value());
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
    private void setClientType(MemberType groupType, OrcidClient client) {
        switch (groupType) {
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
