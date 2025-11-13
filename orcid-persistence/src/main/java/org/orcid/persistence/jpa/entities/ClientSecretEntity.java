package org.orcid.persistence.jpa.entities;

import java.util.Date;
import java.util.Objects;

import javax.persistence.*;

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

    private String clientId;
    private String clientSecret;
    private boolean primary;
    private String decryptedClientSecret;
    
    public ClientSecretEntity() {
        super();
    }

    public ClientSecretEntity(String clientSecret, String clientDetailsId) {
        this.clientSecret = clientSecret;
        this.clientId = clientDetailsId;
    }
    
    public ClientSecretEntity(String clientSecret, String clientDetailsId, boolean primary) {
        this.clientSecret = clientSecret;
        this.clientId = clientDetailsId;
        this.primary = primary;
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
    @Column(name = "client_secret")
    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
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

        return NullUtils.compareObjectsNullSafe(clientSecret, other.clientSecret);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientSecretEntity that = (ClientSecretEntity) o;
        return primary == that.primary && Objects.equals(clientId, that.clientId) && Objects.equals(clientSecret, that.clientSecret) && Objects.equals(decryptedClientSecret, that.decryptedClientSecret);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId, clientSecret, primary, decryptedClientSecret);
    }

    /**
     * As this uses a composite key this is ignored. Always returns null
     *
     * @return always null
     */
    @Override
    @Transient
    public ClientSecretPk getId() {
        return null;
    }
}
