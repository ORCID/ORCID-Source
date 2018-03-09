package org.orcid.core.manager.read_only;

import java.util.Set;

import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.client_v2.ClientSummary;

public interface ClientManagerReadOnly {
    Client get(String clientId);

    Set<Client> getClients(String memberId);

    ClientSummary getSummary(String clientId);
}
