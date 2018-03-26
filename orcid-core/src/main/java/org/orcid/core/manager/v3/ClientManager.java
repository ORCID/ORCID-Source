package org.orcid.core.manager.v3;

import org.orcid.jaxb.model.v3.dev1.client.Client;

public interface ClientManager {

    Client create(Client newClient);
    
    Client createPublicClient(Client newClient);

    Client edit(Client existingClient, boolean updateConfigValues);
    
    Boolean resetClientSecret(String clientId);
}
