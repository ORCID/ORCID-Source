package org.orcid.persistence.jpa.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Will Simpson
 */
@Entity
@Table(name = "email")
public class EmailEntity extends SourceAwareEntity<String> implements OrcidAware {

    private static final long serialVersionUID = 1;

    private String email;
    private String emailHash;
    private String orcid;
    private Boolean primary;
    private Boolean current;
    private Boolean verified;
    private String visibility; 
    private Date dateVerified;
    
    @Override
    @Id
    @Column(name = "email_hash")
    public String getId() {
        return emailHash;
    }
    
    public void setId(String emailHash) {
        this.emailHash = emailHash;
    }
    
    @Column(name = "email", length = 350)
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "orcid", nullable = false)
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
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
    
    @Column(name = "date_verified")
    public Date getDateVerified() {
        return dateVerified;
    }

    public void setDateVerified(Date dateVerified) {
        this.dateVerified = dateVerified;
    }

    public static Map<String, EmailEntity> mapByLowerCaseEmail(Collection<EmailEntity> emailEntities) {
        Map<String, EmailEntity> map = new HashMap<>();
        for (EmailEntity existingEmail : emailEntities) {
            map.put(existingEmail.getEmail().toLowerCase(), existingEmail);
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
        dateVerified = null;
    }     
}