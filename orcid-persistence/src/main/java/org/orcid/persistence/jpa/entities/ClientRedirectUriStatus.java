package org.orcid.persistence.jpa.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author Will Simpson
 * 
 */
public enum ClientRedirectUriStatus {

    OK, RETIRED;

    public static Object getNames(Collection<ClientRedirectUriStatus> indexingStatuses) {
        List<String> names = new ArrayList<>();
        for (ClientRedirectUriStatus indexingStatus : indexingStatuses) {
            names.add(indexingStatus.name());
        }
        return names;
    }

}
