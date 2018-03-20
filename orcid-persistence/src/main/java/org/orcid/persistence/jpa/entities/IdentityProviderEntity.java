package org.orcid.persistence.jpa.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * @author Will Simpson
 */
@Entity
@Table(name = "identity_provider")
public class IdentityProviderEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String providerid;
    private String displayName;
    private String supportEmail;
    private String adminEmail;
    private String techEmail;
    private Date lastFailed;
    private int failedCount;
    private List<IdentityProviderNameEntity> names;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "identity_provider_seq")
    @SequenceGenerator(name = "identity_provider_seq", sequenceName = "identity_provider_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProviderid() {
        return providerid;
    }

    public void setProviderid(String providerid) {
        this.providerid = providerid;
    }

    @Column(name = "display_name")
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Column(name = "support_email")
    public String getSupportEmail() {
        return supportEmail;
    }

    public void setSupportEmail(String supportEmail) {
        this.supportEmail = supportEmail;
    }

    @Column(name = "admin_email")
    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    @Column(name = "tech_email")
    public String getTechEmail() {
        return techEmail;
    }

    public void setTechEmail(String techEmail) {
        this.techEmail = techEmail;
    }

    @Column(name = "last_failed")
    public Date getLastFailed() {
        return lastFailed;
    }

    public void setLastFailed(Date lastFailed) {
        this.lastFailed = lastFailed;
    }

    @Column(name = "failed_count")
    public int getFailedCount() {
        return failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "identityProvider", orphanRemoval = true)
    public List<IdentityProviderNameEntity> getNames() {
        return names;
    }

    public void setNames(List<IdentityProviderNameEntity> names) {
        this.names = names;
    }

    @Override
    public String toString() {
        return toShortString();
    }

    public String toShortString() {
        return "IdentityProviderEntity [providerid=" + providerid + ", displayName=" + displayName + ", supportEmail=" + supportEmail + ", adminEmail=" + adminEmail
                + ", techEmail=" + techEmail + "]";
    }

}
