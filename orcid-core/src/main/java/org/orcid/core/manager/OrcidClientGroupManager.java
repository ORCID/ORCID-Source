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
package org.orcid.core.manager;

import org.orcid.core.exception.OrcidClientGroupManagementException;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrcidClientGroupManager {

    /**
     * Creates a new orcidClientGroup if orcidClientGroup.groupOrcid is null.
     * Updates an existing orcidClientGroup if orcidClientGroup.groupOrcid is
     * not null.
     * 
     * Creates a new orcidClient for each client in orcidClientGroup for which
     * orcidClient.clientId is null. Updates an existing orcidClient for each
     * client in orcidClientGroup for which orcidClient.clientId is not null.
     * 
     * @param orcidClientGroup
     *            The ORCID client group to be ingested. The client scopes are set based on the group type.
     * @return The ORCID client group that was ingested, populated with IDs and
     *         secrets.
     */
    OrcidClientGroup createOrUpdateOrcidClientGroup(OrcidClientGroup orcidClientGroup);

    /**
     * Creates a new orcidClientGroup if orcidClientGroup.groupOrcid is null.
     * Updates an existing orcidClientGroup if orcidClientGroup.groupOrcid is
     * not null.
     * 
     * Creates a new orcidClient for each client in orcidClientGroup for which
     * orcidClient.clientId is null. Updates an existing orcidClient for each
     * client in orcidClientGroup for which orcidClient.clientId is not null.
     * 
     * Update the resulting client types, so, the types will be only creator or updater, removing the "premium-" 
     * from the type filed
     * 
     * @param orcidClientGroup
     *            The ORCID client group to be ingested. The client scopes are set based on the group type.
     * @return The ORCID client group that was ingested, populated with IDs and
     *         secrets.
     */
    OrcidClientGroup createOrUpdateOrcidClientGroupForAPIRequest(OrcidClientGroup orcidClientGroup);
    
    /**
     * Creates a new orcidClient and assign it to the specified group
     * 
     * @param orcidClient
     *            The ORCID client to be created. The client scopes are set based on the group type.
     * @param groupOrcid
     *            The group owner for this client
     * @return The ORCID client that was processed, populated with IDs and
     *         secrets.
     */
    OrcidClient createOrUpdateOrcidClientGroup(String groupOrcid, OrcidClient orcidClient);

    OrcidClientGroup retrieveOrcidClientGroup(String groupOrcid);

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
    OrcidClient updateClientProfile(String groupOrcid, OrcidClient client);

    /**
     * Creates a new client and set the group orcid as the owner of that client
     * 
     * @param groupOrcid
     *            The group owner for this client
     * @param client
     *            The new client
     * @return the new OrcidClient
     * */
    OrcidClient createAndPersistClientProfile(String groupOrcid, OrcidClient client) throws OrcidClientGroupManagementException;

    /**
     * Deletes a group
     * 
     * @param groupOrcid
     *            The orcid of the group that wants to be deleted
     * */
    void removeOrcidClientGroup(String groupOrcid);
    
    /**
     * Creates a group profile. If the OrcidClientGroup provided already contains a groupOrcid, 
     * it will just return it, if it doesnt, it will create the profile and update the parameter.
     * @param orcidClientGroup the group to be created
     * @return the group updated with his orcid id.
     * */
    OrcidClientGroup createGroup(OrcidClientGroup orcidClientGroup);

}
