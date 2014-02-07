package org.orcid.core.manager;

import java.util.Set;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public interface OrcidSSOManager {

    ClientDetailsEntity generateUserCredentials(String orcid, Set<String> redirectUris);
    ClientDetailsEntity getUserCredentials(String orcid);

}
