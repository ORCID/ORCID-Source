package org.orcid.core.manager;

import org.orcid.jaxb.model.client_v2.Client;

public interface ClientManager {

    Client create(Client newClient);
    
    Client createPublicClient(Client newClient);

    Client edit(Client existingClient, boolean updateConfigValues);
    
    Boolean resetClientSecret(String clientId);
}
