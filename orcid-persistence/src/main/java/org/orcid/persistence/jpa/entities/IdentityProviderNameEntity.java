package org.orcid.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

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
    @SequenceGenerator(name = "identity_provider_name_seq", sequenceName = "identity_provider_name_seq", allocationSize = 1)
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
