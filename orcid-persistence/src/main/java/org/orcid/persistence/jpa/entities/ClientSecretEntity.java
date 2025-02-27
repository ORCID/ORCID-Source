package org.orcid.persistence.jpa.entities;

import java.util.Date;

import javax.persistence.*;

import org.orcid.persistence.jpa.entities.keys.ClientSecretPk;
import org.orcid.utils.NullUtils;

/**
 * 
 * @author Will Simpson
 */
@Entity
@Table(name = "client_secret")
public class ClientSecretEntity extends BaseEntity<ClientSecretPk> implements Comparable<ClientSecretEntity> {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private ClientSecretPk id;
    private String decryptedClientSecret;
    private boolean primary;
    
    public ClientSecretEntity() {
        super();
    }

    public ClientSecretEntity(String clientSecret, String clientDetailsId) {
        this.id = new ClientSecretPk(clientSecret, clientDetailsId);
    }
    
    public ClientSecretEntity(String clientSecret, String clientDetailsId, boolean primary) {
        this.id = new ClientSecretPk(clientSecret, clientDetailsId);
        this.primary = primary;
    }

    @Override
    public ClientSecretPk getId() {
        return id;
    }

    public void setId(ClientSecretPk id) {
        this.id = id;
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

        String secret = (id == null) ? null : id.getClientSecret();
        String otherSecret = (other.id == null) ? null : other.id.getClientSecret();
        return NullUtils.compareObjectsNullSafe(secret, otherSecret);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
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
