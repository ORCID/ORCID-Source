package org.orcid.persistence.jpa.entities;

import org.orcid.persistence.jpa.entities.keys.OrcidGrantedAuthorityPk;
import org.springframework.security.core.GrantedAuthority;

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
 * Simplistic implementation of {@link GrantedAuthority}. This will need to be
 * extended to accommodate OAuth2 etc.
 * <p/>
 * orcid-persistence - Dec 8, 2011 - OrcidGrantedAuthority
 * 
 * @author Declan Newman (declan)
 */

@Entity
@Table(name = "granted_authority")
@IdClass(OrcidGrantedAuthorityPk.class)
public class OrcidGrantedAuthority extends BaseEntity<OrcidGrantedAuthorityPk> implements GrantedAuthority, OrcidAware {

    private static final long serialVersionUID = 2301981481864446645L;

    private String orcid;
    private String authority;

    @Id    
    @Column(name = "orcid", nullable = false, updatable = false, insertable = false)
    public String getOrcid() {
        return orcid;
    }

    @Override
    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Override
    @Id
    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Transient
    public OrcidGrantedAuthorityPk getId() {
        return null;
    }    

}
