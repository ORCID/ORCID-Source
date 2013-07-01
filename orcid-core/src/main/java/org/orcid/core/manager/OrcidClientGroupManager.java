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

import org.orcid.jaxb.model.clientgroup.ClientType;
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
     *            The ORCID client group to be ingested.
     * @param clientType
     *            The client scopes are set based on the client type.
     * @return The ORCID client group that was ingested, populated with IDs and
     *         secrets.
     */
    OrcidClientGroup createOrUpdateOrcidClientGroup(OrcidClientGroup orcidClientGroup, ClientType clientType);

    /**
     * Creates a new orcidClient and assign it to the specified group
     * 
     * @param orcidClient
     *            The ORCID client to be created.
     * @param clientType
     *            The client scopes are set based on the client type.
     * @param groupOrcid
     *            The group owner for this client
     * @return The ORCID client that was processed, populated with IDs and
     *         secrets.
     */
    OrcidClient createOrUpdateOrcidClientGroup(String groupOrcid, OrcidClient orcidClient, ClientType clientType);

    OrcidClientGroup retrieveOrcidClientGroup(String groupOrcid);

    void removeOrcidClientGroup(String groupOrcid);

}
