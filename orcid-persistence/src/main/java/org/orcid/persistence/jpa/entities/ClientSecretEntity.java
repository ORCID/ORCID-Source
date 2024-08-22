package org.orcid.persistence.jpa.entities;

import java.util.Date;

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

import org.orcid.persistence.jpa.entities.keys.ClientSecretPk;
import org.orcid.utils.NullUtils;

/**
 * 
 * @author Will Simpson
 */
@Entity
@Table(name = "client_secret")
@IdClass(ClientSecretPk.class)
public class ClientSecretEntity extends BaseEntity<ClientSecretPk> implements Comparable<ClientSecretEntity> {

    private static final long serialVersionUID = 1L;

    private String clientSecret;
    private String decryptedClientSecret;
    private ClientDetailsEntity clientDetailsEntity;
    private boolean primary;
    
    public ClientSecretEntity() {
        super();
    }

    public ClientSecretEntity(String clientSecret, ClientDetailsEntity clientDetailsEntity) {
        this.clientSecret = clientSecret;
        this.clientDetailsEntity = clientDetailsEntity;
    }
    
    public ClientSecretEntity(String clientSecret, ClientDetailsEntity clientDetailsEntity, boolean primary) {
        this.clientSecret = clientSecret;
        this.clientDetailsEntity = clientDetailsEntity;
        this.primary = primary;
    }

    /**
     * As this uses a composite key this is ignored always returns null
     * 
     * @return always returns null
     */
    @Override
    @Transient
    public ClientSecretPk getId() {
        return null;
    }

    @Id
    @Column(name = "client_secret", length = 150)
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
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

    @Column(name = "is_primary")
    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Transient
    public String getDecryptedClientSecret() {
        return decryptedClientSecret;
    }

    public void setDecryptedClientSecret(String decryptedClientSecret) {
        this.decryptedClientSecret = decryptedClientSecret;
    }
    
    @Override
    public int compareTo(ClientSecretEntity other) {
        Date otherLastModified = other.getLastModified();
        Date thisLastModified = getLastModified();
        int dateComparison = NullUtils.compareObjectsNullSafe(thisLastModified, otherLastModified);
        if (dateComparison != 0) {
            return -dateComparison;
        }
        
        if(isPrimary() != other.isPrimary()) {
            return -1;
        }
        
        return clientSecret.compareTo(other.getClientSecret());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clientSecret == null) ? 0 : clientSecret.hashCode());
        result = prime * result + ((decryptedClientSecret == null) ? 0 : decryptedClientSecret.hashCode());
        result = prime * result + (primary ? 1231 : 1237);
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
        ClientSecretEntity other = (ClientSecretEntity) obj;
        if (clientSecret == null) {
            if (other.clientSecret != null)
                return false;
        } else if (!clientSecret.equals(other.clientSecret))
            return false;
        if (decryptedClientSecret == null) {
            if (other.decryptedClientSecret != null)
                return false;
        } else if (!decryptedClientSecret.equals(other.decryptedClientSecret))
            return false;
        if (primary != other.primary)
            return false;
        return true;
    }        

}
