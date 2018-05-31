package org.orcid.core.manager.v3.read_only.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbClientAdapter;
import org.orcid.core.manager.v3.read_only.ClientManagerReadOnly;
import org.orcid.jaxb.model.v3.rc1.client.Client;
import org.orcid.jaxb.model.v3.rc1.client.ClientSummary;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public class ClientManagerReadOnlyImpl implements ClientManagerReadOnly {

    @Resource(name = "jpaJaxbClientAdapterV3")
    protected JpaJaxbClientAdapter jpaJaxbClientAdapter;

    private ClientDetailsDao clientDetailsDao;

    public void setClientDetailsDao(ClientDetailsDao clientDetailsDao) {
        this.clientDetailsDao = clientDetailsDao;
    }

    @Override
    public Client get(String clientId) {
        Date lastModified = clientDetailsDao.getLastModified(clientId);
        ClientDetailsEntity entity = clientDetailsDao.findByClientId(clientId, lastModified.getTime());
        return jpaJaxbClientAdapter.toClient(entity);
    }

    @Override
    public ClientSummary getSummary(String clientId) {
        Date lastModified = clientDetailsDao.getLastModified(clientId);
        ClientDetailsEntity clientDetailsEntity = clientDetailsDao.findByClientId(clientId, lastModified.getTime());
        return jpaJaxbClientAdapter.toClientSummary(clientDetailsEntity);
    }
    
    @Override
    public Set<Client> getClients(String memberId) {
        List<ClientDetailsEntity> clients = clientDetailsDao.findByGroupId(memberId);
        return jpaJaxbClientAdapter.toClientList(clients);
    }

}
