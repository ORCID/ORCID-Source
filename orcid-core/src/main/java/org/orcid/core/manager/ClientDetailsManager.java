package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public interface ClientDetailsManager {
    ClientDetailsEntity findByClientId(String orcid);

    void removeByClientId(String clientId);

    void persist(ClientDetailsEntity clientDetails);

    ClientDetailsEntity merge(ClientDetailsEntity clientDetails);
}
