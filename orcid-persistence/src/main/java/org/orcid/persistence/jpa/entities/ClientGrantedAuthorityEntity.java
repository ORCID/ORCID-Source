package org.orcid.persistence.jpa.entities;

import org.orcid.persistence.jpa.entities.keys.ClientGrantedAuthorityPk;
import org.springframework.security.core.GrantedAuthority;

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
@Table(name = "client_granted_authority")
@IdClass(ClientGrantedAuthorityPk.class)
public class ClientGrantedAuthorityEntity extends BaseEntity<ClientGrantedAuthorityPk> implements GrantedAuthority {

    private static final long serialVersionUID = 1L;
    private String authority;
    private ClientDetailsEntity clientDetailsEntity;

    /**
     * As this uses a composite key this is not used
     * 
     * @return always null
     */
    @Override
    @Transient
    public ClientGrantedAuthorityPk getId() {
        return null;
    }

    /**
     * If the <code>GrantedAuthority</code> can be represented as a
     * <code>String</code> and that <code>String</code> is sufficient in
     * precision to be relied upon for an access control decision by an
     * {@link org.springframework.security.access.AccessDecisionManager} (or
     * delegate), this method should return such a <code>String</code>.
     * <p/>
     * If the <code>GrantedAuthority</code> cannot be expressed with sufficient
     * precision as a <code>String</code>, <code>null</code> should be returned.
     * Returning <code>null</code> will require an
     * <code>AccessDecisionManager</code> (or delegate) to specifically support
     * the <code>GrantedAuthority</code> implementation, so returning
     * <code>null</code> should be avoided unless actually required.
     * 
     * @return a representation of the granted authority (or <code>null</code>
     *         if the granted authority cannot be expressed as a
     *         <code>String</code> with sufficient precision).
     */
    @Override
    @Id
    @Column(name = "granted_authority", length = 150)
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((authority == null) ? 0 : authority.hashCode());
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
        ClientGrantedAuthorityEntity other = (ClientGrantedAuthorityEntity) obj;
        if (authority == null) {
            if (other.authority != null)
                return false;
        } else if (!authority.equals(other.authority))
            return false;
        return true;
    }
    
}
