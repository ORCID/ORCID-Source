package org.orcid.persistence.jpa.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.*;

import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;
import org.orcid.utils.NullUtils;

/**
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_redirect_uri")
public class ClientRedirectUriEntity extends BaseEntity<ClientRedirectUriPk> implements Comparable<ClientRedirectUriEntity> {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    private ClientRedirectUriPk id;
    private String predefinedClientScope;
    private String uriActType;
    private String uriGeoArea;
    private ClientRedirectUriStatus status = ClientRedirectUriStatus.OK;

    public ClientRedirectUriEntity() {
    }

    @Column(name = "predefined_client_redirect_scope", length = 150)
    public String getPredefinedClientScope() {
        return predefinedClientScope;
    }

    public void setPredefinedClientScope(String predefinedClientScope) {
        this.predefinedClientScope = predefinedClientScope;
    }

    public static Map<String, ClientRedirectUriEntity> mapByUriAndType(Set<ClientRedirectUriEntity> clientRedirectUriEntities) {
        Map<String, ClientRedirectUriEntity> map = new HashMap<String, ClientRedirectUriEntity>();
        for (ClientRedirectUriEntity clientRedirectUriEntity : clientRedirectUriEntities) {
            map.put(getUriAndTypeKey(clientRedirectUriEntity), clientRedirectUriEntity);
        }
        return map;
    }

    public static String getUriAndTypeKey(ClientRedirectUriEntity rUri) {
        return rUri.getId().getRedirectUri() + '-' + rUri.getId().getRedirectUriType();
    }
    
    public static String getUriAndTypeKey(String redirectUri, String redirectUriType) {
        return redirectUri + '-' + redirectUriType;
    }

    @Column(name = "uri_act_type")
    public String getUriActType() {
        return uriActType;
    }

    public void setUriActType(String uriActType) {
        this.uriActType = uriActType;
    }

    @Column(name = "uri_geo_area")
    public String getUriGeoArea() {
        return uriGeoArea;
    }

    public void setUriGeoArea(String uriGeoArea) {
        this.uriGeoArea = uriGeoArea;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    public ClientRedirectUriStatus getStatus() {
        return status;
    }

    public void setStatus(ClientRedirectUriStatus status) {
        this.status = status;
    }

    @Override
    public int compareTo(ClientRedirectUriEntity o) {
        if (o == null) {
            return 1;
        }
        String uri = (this.getId() == null) ? null : this.getId().getRedirectUri();
        String otherUri = (o.getId() == null) ? null : o.getId().getRedirectUri();

        int uriComparison = NullUtils.compareObjectsNullSafe(uri, otherUri);
        if (uriComparison != 0) {
            return -uriComparison;
        }

        return 0;
    }

    @Override
    public ClientRedirectUriPk getId() {
        return id;
    }

    public void setId(ClientRedirectUriPk id) {
        this.id = id;
    }
}
