package org.orcid.core.manager.v3;

import org.orcid.jaxb.model.v3.release.client.Client;

public interface ClientManager {

    Client create(Client newClient);
    
    Client createPublicClient(Client newClient);

    Client createWithConfigValues(Client newClient);

    Client edit(Client existingClient, boolean updateConfigValues);

    Boolean resetClientSecret(String clientId);

    String resetAndGetClientSecret(String clientId);
}
