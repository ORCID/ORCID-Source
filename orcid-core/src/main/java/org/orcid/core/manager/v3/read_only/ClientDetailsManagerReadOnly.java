package org.orcid.core.manager.v3.read_only;

import java.util.Date;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.client.Client;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.springframework.security.oauth2.provider.ClientDetailsService;

public interface ClientDetailsManagerReadOnly extends ClientDetailsService {
    ClientDetailsEntity findByClientId(String orcid);

    List<ClientDetailsEntity> getAll();

    Date getLastModified(String clientId);

    Date getLastModifiedByIdp(String idp);
    
    boolean exists(String cliendId);
    
    /**
     * Verifies if a client belongs to the given group id
     * @param clientId
     * @param groupId
     * @return true if clientId belongs to groupId
     * */
    boolean belongsTo(String clientId, String groupId);
    
    /**
     * Fetch all clients that belongs to a group
     * @param groupId
     *  Group id
     * @return A list containing all clients that belongs to the given group
     * */
    List<ClientDetailsEntity> findByGroupId(String groupId);
    
    ClientDetailsEntity getPublicClient(String ownerId);
    
    String getMemberName(String clientId);    
    
    ClientDetailsEntity findByIdP(String idp);

    boolean isLegacyClientId(String clientId);

    Client getClient(String clientId);
    
}
