package org.orcid.persistence.jpa.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;

import org.orcid.persistence.jpa.entities.keys.ClientRedirectUriPk;
import org.orcid.utils.NullUtils;

/**
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_redirect_uri")
@IdClass(ClientRedirectUriPk.class)
public class ClientRedirectUriEntity extends BaseEntity<ClientRedirectUriPk> implements Comparable<ClientRedirectUriEntity> {

    private static final long serialVersionUID = 1L;

    private String clientId;
    private String redirectUri;
    private String redirectUriType;
    private String predefinedClientScope;
    private String uriActType;
    private String uriGeoArea;
    private String redirectUriMetadata;
    private ClientRedirectUriStatus status = ClientRedirectUriStatus.OK;

    public ClientRedirectUriEntity() {
    }

    @Id
    @Column(name = "client_details_id")
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Id
    @Column(name = "redirect_uri")
    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    @Id
    @Column(name = "redirect_uri_type")
    public String getRedirectUriType() {
        return redirectUriType;
    }

    public void setRedirectUriType(String redirectUriType) {
        this.redirectUriType = redirectUriType;
    }

    @Column(name = "predefined_client_redirect_scope", length = 150)
    public String getPredefinedClientScope() {
        return predefinedClientScope;
    }

    public void setPredefinedClientScope(String predefinedClientScope) {
        this.predefinedClientScope = predefinedClientScope;
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

    @Column(name = "redirect_uri_metadata")
    public String getRedirectUriMetadata() {
        return redirectUriMetadata;
    }

    public void setRedirectUriMetadata(String redirectUriMetadata) {
        this.redirectUriMetadata = redirectUriMetadata;
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

    public static Map<String, ClientRedirectUriEntity> mapByUriAndType(Set<ClientRedirectUriEntity> clientRedirectUriEntities) {
        Map<String, ClientRedirectUriEntity> map = new HashMap<String, ClientRedirectUriEntity>();
        for (ClientRedirectUriEntity clientRedirectUriEntity : clientRedirectUriEntities) {
            map.put(getUriAndTypeKey(clientRedirectUriEntity), clientRedirectUriEntity);
        }
        return map;
    }

    public static String getUriAndTypeKey(ClientRedirectUriEntity rUri) {
        return rUri.getRedirectUri() + '-' + rUri.getRedirectUriType();
    }

    public static String getUriAndTypeKey(String redirectUri, String redirectUriType) {
        return redirectUri + '-' + redirectUriType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientRedirectUriEntity that = (ClientRedirectUriEntity) o;
        return Objects.equals(clientId, that.clientId) && Objects.equals(redirectUri, that.redirectUri) && Objects.equals(redirectUriType, that.redirectUriType) && Objects.equals(predefinedClientScope, that.predefinedClientScope) && Objects.equals(uriActType, that.uriActType) && Objects.equals(uriGeoArea, that.uriGeoArea) && Objects.equals(redirectUriMetadata, that.redirectUriMetadata) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, redirectUri, redirectUriType, predefinedClientScope, uriActType, uriGeoArea, redirectUriMetadata, status);
    }

    @Override
    public int compareTo(ClientRedirectUriEntity o) {
        if (o == null) {
            return 1;
        }

        int uriComparison = NullUtils.compareObjectsNullSafe(this.getRedirectUri(), o.getRedirectUri());
        if (uriComparison != 0) {
            return -uriComparison;
        }

        return 0;
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

}
