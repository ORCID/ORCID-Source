/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.orcid.jaxb.model.message.Visibility;

/**
 * @author Will Simpson
 */
@Entity
@Table(name = "email")
public class EmailEntity extends BaseEntity<String> implements ProfileAware {

    private static final long serialVersionUID = 1;

    private String email;
    private ProfileEntity profile;
    private Boolean primary;
    private Boolean current;
    private Boolean verified;
    private Visibility visibility;
    private ProfileEntity source;

    @Override
    @Id
    @Column(name = "email", length = 350)
    public String getId() {
        return email;
    }

    public void setId(String email) {
        this.email = email;
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

    @Basic
    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @ManyToOne
    @JoinColumn(name = "source_id")
    public ProfileEntity getSource() {
        return source;
    }

    public void setSource(ProfileEntity source) {
        this.source = source;
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