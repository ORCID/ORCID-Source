package org.orcid.persistence.jpa.entities;

import org.orcid.persistence.jpa.entities.keys.ClientAuthorisedGrantTypePk;

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

/**
 * @author Declan Newman (declan) Date: 12/03/2012
 */
@Entity
@Table(name = "client_authorised_grant_type")
@IdClass(ClientAuthorisedGrantTypePk.class)
public class ClientAuthorisedGrantTypeEntity extends BaseEntity<ClientAuthorisedGrantTypePk> {

    private static final long serialVersionUID = 1L;
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

    /**
     * This should not be called explicitly as the {@link #updateTimeStamps()}
     * method will be called whenever an update or persist is called
     * 
     * @param dateCreated
     *            the dateCreated to set
     */
    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    /**
     * This should not be called explicitly as the {@link #updateTimeStamps()}
     * method will be called whenever an update or persist is called
     * 
     * @param lastModified
     *            the lastModified to set
     */
    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((grantType == null) ? 0 : grantType.hashCode());
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
        ClientAuthorisedGrantTypeEntity other = (ClientAuthorisedGrantTypeEntity) obj;
        if (grantType == null) {
            if (other.grantType != null)
                return false;
        } else if (!grantType.equals(other.grantType))
            return false;
        return true;
    }

}
