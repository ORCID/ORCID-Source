package org.orcid.persistence.jpa.entities;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.orcid.persistence.jpa.entities.keys.OrcidSocialPk;


@Entity
@Table(name = "orcid_social")
public class OrcidSocialEntity extends BaseEntity<OrcidSocialPk>{
    private static final long serialVersionUID = 1L;
    private ProfileEntity profile;
    private OrcidSocialType type;
    private String encryptedCredentials;
    private Date lastRun;
    
    @Override
    @Transient
    public OrcidSocialPk getId() {
        return new OrcidSocialPk(profile.getId(), type);
    }

    @Id
    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.ALL })
    @JoinColumn(name = "orcid") 
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    @Id
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    public OrcidSocialType getType() {
        return type;
    }

    public void setType(OrcidSocialType type) {
        this.type = type;
    }

    @Column(name = "encrypted_credentials")
    public String getEncryptedCredentials() {
        return encryptedCredentials;
    }

    public void setEncryptedCredentials(String encryptedCredentials) {
        this.encryptedCredentials = encryptedCredentials;
    }

    @Column(name = "last_run")
    public Date getLastRun() {
        return lastRun;
    }

    public void setLastRun(Date lastRun) {
        this.lastRun = lastRun;
    }
        
}
