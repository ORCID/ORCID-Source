package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
public class ShibbolethAccountEntity extends BaseEntity<Long> implements ProfileAware {

    private static final long serialVersionUID = 1L;

    private Long id;
    private ProfileEntity profile;
    private String remoteUser;
    private String shibIdentityProvider;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "shibboleth_account_seq")
    @SequenceGenerator(name = "shibboleth_account_seq", sequenceName = "shibboleth_account_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false, updatable = false, insertable = true)
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
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
