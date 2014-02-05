package org.orcid.core.manager;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public interface OrcidSSOManager {

    ClientDetailsEntity generateUserCredentials(String orcid);
    ClientDetailsEntity getUserCredentials(String orcid);

}
