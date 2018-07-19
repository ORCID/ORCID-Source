package org.orcid.persistence.jpa.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Will Simpson
 */
@Entity
@Table(name = "email")
public class EmailEntity extends SourceAwareEntity<String> implements ProfileAware {

    private static final long serialVersionUID = 1;

    private String email;
    private String emailHash;
    private ProfileEntity profile;
    private Boolean primary;
    private Boolean current;
    private Boolean verified;
    private String visibility;    
    
    @Id
    @Override
    @Column(name = "email_hash")
    public String getId() {
        return emailHash;
    }
    
    @Column(name = "email", length = 350)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "email_hash")
    public String getEmailHash() {
        return emailHash;
    }

    public void setEmailHash(String emailHash) {
        this.emailHash = emailHash;
    }
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false)
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    @Column(name = "is_primary")
    public Boolean getPrimary() {
        return primary;
    }

    public void setPrimary(Boolean primary) {
        this.primary = primary;
    }

    @Column(name = "is_current")
    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    @Column(name = "is_verified")
    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }

    @Column
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public static Map<String, EmailEntity> mapByLowerCaseEmail(Collection<EmailEntity> emailEntities) {
        Map<String, EmailEntity> map = new HashMap<>();
        for (EmailEntity existingEmail : emailEntities) {
            map.put(existingEmail.getId().toLowerCase(), existingEmail);
        }
        return map;
    }

    /**
     * Clean simple fields to allow entity to be reused
     */
    public void clean() {
        primary = null;
        current = null;
        verified= null;
        visibility= null;
        verified = null;
        visibility = null;
    }     
}