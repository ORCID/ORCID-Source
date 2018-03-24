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

/**
 * @author Will Simpson
 */
@Entity
@Table(name = "identity_provider_name")
public class IdentityProviderNameEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;
    private IdentityProviderEntity identityProvider;
    private String displayName;
    private String lang;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "identity_provider_name_seq")
    @SequenceGenerator(name = "identity_provider_name_seq", sequenceName = "identity_provider_name_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "identity_provider_id", nullable = false)
    public IdentityProviderEntity getIdentityProvider() {
        return identityProvider;
    }

    public void setIdentityProvider(IdentityProviderEntity identityProvider) {
        this.identityProvider = identityProvider;
    }

    @Column(name = "display_name")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

}
