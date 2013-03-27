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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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

import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_redirect_uri")
@IdClass(ClientRedirectUriPk.class)
public class ClientRedirectUriEntity extends BaseEntity<ClientRedirectUriPk> implements Comparable<ClientRedirectUriEntity> {

    private static final long serialVersionUID = 1L;
    private String redirectUri;
    private String predefinedClientScope;
    private ClientDetailsEntity clientDetailsEntity;

    public ClientRedirectUriEntity() {
    }

    public ClientRedirectUriEntity(String redirectUri, ClientDetailsEntity clientDetailsEntity) {
        this.redirectUri = redirectUri;
        this.clientDetailsEntity = clientDetailsEntity;
    }

    /**
     * As this uses a composite key this is ignored. Always returns null
     * 
     * @return always null
     */
    @Override
    @Transient
    public ClientRedirectUriPk getId() {
        return null;
    }

    @Id
    @Column(name = "redirect_uri", length = 500)
    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Column(name = "predefined_client_redirect_scope", length = 150)
    public String getPredefinedClientScope() {
        return predefinedClientScope;
    }

    public void setPredefinedClientScope(String predefinedClientScope) {
        this.predefinedClientScope = predefinedClientScope;
    }

    @Id
    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH, CascadeType.DETACH })
    @JoinColumn(name = "client_details_id")
    public ClientDetailsEntity getClientDetailsEntity() {
        return clientDetailsEntity;
    }

    public void setClientDetailsEntity(ClientDetailsEntity clientDetailsEntity) {
        this.clientDetailsEntity = clientDetailsEntity;
    }

    public static Map<String, ClientRedirectUriEntity> mapByUri(Set<ClientRedirectUriEntity> clientRedirectUriEntities) {
        Map<String, ClientRedirectUriEntity> map = new HashMap<String, ClientRedirectUriEntity>();
        for (ClientRedirectUriEntity clientRedirectUriEntity : clientRedirectUriEntities) {
            map.put(clientRedirectUriEntity.getRedirectUri(), clientRedirectUriEntity);
        }
        return map;
    }

    @Override
    public int compareTo(ClientRedirectUriEntity o) {
        if (o == null) {
            return 1;
        }
        String otherUri = o.getRedirectUri();
        if (otherUri == null) {
            return redirectUri == null ? 0 : 1;
        }
        return redirectUri == null ? -1 : redirectUri.compareTo(otherUri);
    }

}
