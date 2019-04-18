package org.orcid.core.manager.v3.read_only;

import java.util.Set;

import org.orcid.jaxb.model.v3.release.client.Client;
import org.orcid.jaxb.model.v3.release.client.ClientSummary;

public interface ClientManagerReadOnly {
    
    Client get(String clientId);

    ClientSummary getSummary(String clientId);
    
    Set<Client> getClients(String memberId);
}

