package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.dao.ClientRedirectDao;
import org.orcid.persistence.jpa.entities.ClientRedirectUriEntity;
import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author jamesb
 * 
 */
public class ClientRedirectDaoImpl extends GenericDaoImpl<ClientRedirectUriEntity, ClientRedirectUriPk> implements ClientRedirectDao {

    public ClientRedirectDaoImpl() {
        super(ClientRedirectUriEntity.class);
    }

    @Override
    public List<ClientRedirectUriEntity> findClientDetailsWithRedirectScope() {

        Query query = entityManager.createQuery("from ClientRedirectUriEntity as crue " + "inner join fetch crue.clientDetailsEntity "
                + "where crue.predefinedClientScope is not null");

        return query.getResultList();

    }

    @Override
    @Transactional
    public void addClientRedirectUri(String clientId, String redirectUri) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO client_redirect_uri (date_created, last_modified, client_details_id, redirect_uri) VALUES (now(), now(), :clientId, :redirectUri)");
        query.setParameter("clientId", clientId);
        query.setParameter("redirectUri", redirectUri);
        query.executeUpdate();
    }
    
    @Override
    @Transactional
    public void addClientRedirectUri(String clientId, String redirectUri, String type, String scope) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO client_redirect_uri (date_created, last_modified, client_details_id, redirect_uri,redirect_uri_type,predefined_client_redirect_scope) VALUES (now(), now(), :clientId, :redirectUri, :type, :scope)");
        query.setParameter("clientId", clientId);
        query.setParameter("redirectUri", redirectUri);
        query.setParameter("type", type);
        query.setParameter("scope", scope);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void removeClientRedirectUri(String clientId, String redirectUri) {
        Query query = entityManager
                .createNativeQuery("delete from client_redirect_uri where client_details_id=:clientId and redirect_uri=:redirectUri");
        query.setParameter("clientId", clientId);
        query.setParameter("redirectUri", redirectUri);
        query.executeUpdate();
    }


}
