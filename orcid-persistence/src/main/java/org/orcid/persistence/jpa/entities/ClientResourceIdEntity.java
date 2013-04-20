/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities;

import org.orcid.persistence.jpa.entities.keys.ClientResourceIdPk;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_resource_id")
@IdClass(ClientResourceIdPk.class)
public class ClientResourceIdEntity extends BaseEntity<ClientResourceIdPk> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String resourceId;
    private ClientDetailsEntity clientDetailsEntity;

    /**
     * As this uses a composite key this is ignored. Always returns null
     * 
     * @return always null
     */
    @Override
    @Transient
    public ClientResourceIdPk getId() {
        return null;
    }

    @Id
    @Column(name = "resource_id", length = 175)
    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "client_details_id")
    public ClientDetailsEntity getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(ClientDetailsEntity clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
    }

}
