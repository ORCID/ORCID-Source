package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author Will Simpson
 *
 */
@Entity
@Table(name = "shibboleth_account")
public class ShibbolethAccountEntity extends BaseEntity<Long> implements OrcidAware {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String orcid;
    private String remoteUser;
    private String shibIdentityProvider;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "shibboleth_account_seq")
    @SequenceGenerator(name = "shibboleth_account_seq", sequenceName = "shibboleth_account_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    @JsonIgnore
    @Column(name = "orcid", nullable = false, updatable = false, insertable = true)
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "remote_user")
    public String getRemoteUser() {
        return remoteUser;
    }

    public void setRemoteUser(String remoteUser) {
        this.remoteUser = remoteUser;
    }

    @Column(name = "shib_identity_provider")
    public String getShibIdentityProvider() {
        return shibIdentityProvider;
    }

    public void setShibIdentityProvider(String shibIdentityProvider) {
        this.shibIdentityProvider = shibIdentityProvider;
    }

}
