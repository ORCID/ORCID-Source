package org.orcid.core.manager;

import java.util.Date;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public interface ClientDetailsManager {
    ClientDetailsEntity findByClientId(String orcid, Date lastModified);

    void removeByClientId(String clientId);

    void persist(ClientDetailsEntity clientDetails);

    ClientDetailsEntity merge(ClientDetailsEntity clientDetails);
}
