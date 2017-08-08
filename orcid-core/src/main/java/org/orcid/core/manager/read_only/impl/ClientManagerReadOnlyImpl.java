/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.read_only.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbClientAdapter;
import org.orcid.core.manager.read_only.ClientManagerReadOnly;
import org.orcid.jaxb.model.client_v2.Client;
import org.orcid.jaxb.model.client_v2.ClientSummary;
import org.orcid.persistence.dao.ClientDetailsDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;

public class ClientManagerReadOnlyImpl implements ClientManagerReadOnly {

    @Resource
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
    public Set<Client> getClients(String memberId) {
        List<ClientDetailsEntity> clients = clientDetailsDao.findByGroupId(memberId);
        return jpaJaxbClientAdapter.toClientList(clients);
    }

    @Override
    public ClientSummary getSummary(String clientId) {
        Date lastModified = clientDetailsDao.getLastModified(clientId);
        ClientDetailsEntity clientDetailsEntity = clientDetailsDao.findByClientId(clientId, lastModified.getTime());
        return jpaJaxbClientAdapter.toClientSummary(clientDetailsEntity);
    }

}
