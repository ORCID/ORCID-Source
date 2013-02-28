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

import org.orcid.persistence.jpa.entities.keys.ClientAuthorisedGrantTypePk;

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
@Table(name = "client_authorised_grant_type")
@IdClass(ClientAuthorisedGrantTypePk.class)
public class ClientAuthorisedGrantTypeEntity extends BaseEntity<ClientAuthorisedGrantTypePk> {

    private String grantType;
    private ClientDetailsEntity clientDetailsEntity;

    /**
     * As this uses a composite key this is not used. Always returns null
     * 
     * @return always null
     */
    @Override
    @Transient
    public ClientAuthorisedGrantTypePk getId() {
        return null;
    }

    @Id
    @Column(name = "grant_type", length = 150)
    public String getGrantType() {
        return grantType;
    }

    public void setGrantType(String grantType) {
        this.grantType = grantType;
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
