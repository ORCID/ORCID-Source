package org.orcid.core.manager;

import java.net.URI;
import java.util.Set;

import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public interface OrcidSSOManager {

    ClientDetailsEntity generateUserCredentials(String orcid, Set<URI> redirect_uri);
    ClientDetailsEntity getUserCredentials(String orcid);

}
