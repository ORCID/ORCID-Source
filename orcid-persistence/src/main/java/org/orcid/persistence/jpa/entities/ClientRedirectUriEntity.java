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
    private String redirectUriType;
    private String uriActType;
    private String uriGeoArea;

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
    public int compareTo(ClientRedirectUriEntity o) {
        if (o == null) {
            return 1;
        }
        String otherUri = o.getRedirectUri();
        if (otherUri == null) {
            return redirectUri == null ? 0 : 1;
        } else if(redirectUri == null){
            return -1;
        } else {
            int compare = redirectUri.compareTo(otherUri);
            if(compare != 0)
                return compare;
            else {
                if(o.getRedirectUriType() == null){
                    if(redirectUriType != null)
                        return 1;
                } else if(redirectUriType == null) {
                    return -1;
                } else {
                    return redirectUriType.compareTo(o.getRedirectUriType());
                }
            }
        }
        
        return 0;
    }

    @Column(name = "redirect_uri_type", length = 20)
    public String getRedirectUriType() {
        return redirectUriType;
    }

    public void setRedirectUriType(String redirectUriType) {
        this.redirectUriType = redirectUriType;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((predefinedClientScope == null) ? 0 : predefinedClientScope.hashCode());
        result = prime * result + ((redirectUri == null) ? 0 : redirectUri.hashCode());
        result = prime * result + ((redirectUriType == null) ? 0 : redirectUriType.hashCode());
        result = prime * result + ((uriActType == null) ? 0 : uriActType.hashCode());
        result = prime * result + ((uriGeoArea == null) ? 0 : uriGeoArea.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientRedirectUriEntity other = (ClientRedirectUriEntity) obj;
        if (predefinedClientScope == null) {
            if (other.predefinedClientScope != null)
                return false;
        } else if (!predefinedClientScope.equals(other.predefinedClientScope))
            return false;
        if (redirectUri == null) {
            if (other.redirectUri != null)
                return false;
        } else if (!redirectUri.equals(other.redirectUri))
            return false;
        if (redirectUriType == null) {
            if (other.redirectUriType != null)
                return false;
        } else if (!redirectUriType.equals(other.redirectUriType))
            return false;
        if (uriActType == null) {
            if (other.uriActType != null)
                return false;
        } else if (!uriActType.equals(other.uriActType))
            return false;
        if (uriGeoArea == null) {
            if (other.uriGeoArea != null)
                return false;
        } else if (!uriGeoArea.equals(other.uriGeoArea))
            return false;
        return true;
    }

}
