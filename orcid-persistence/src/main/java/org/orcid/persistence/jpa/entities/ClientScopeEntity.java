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

import org.orcid.persistence.jpa.entities.keys.ClientScopePk;

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
@Table(name = "client_scope")
@IdClass(ClientScopePk.class)
public class ClientScopeEntity extends BaseEntity<ClientScopePk> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public ClientScopeEntity() {
    }

    public ClientScopeEntity(String scopeType) {
        this.scopeType = scopeType;
    }

    private String scopeType;
    private ClientDetailsEntity clientDetailsEntity;

    /**
     * As this uses a composite key this is ignored always returns null
     * 
     * @return always returns null
     */
    @Override
    @Transient
    public ClientScopePk getId() {
        return null;
    }

    @Id
    @Column(name = "scope_type", length = 70)
    public String getScopeType() {
        return scopeType;
    }

    public void setScopeType(String scopeType) {
        this.scopeType = scopeType;
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
