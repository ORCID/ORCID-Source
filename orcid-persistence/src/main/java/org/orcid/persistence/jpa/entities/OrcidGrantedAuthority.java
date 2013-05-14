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

import org.orcid.persistence.jpa.entities.keys.OrcidGrantedAuthorityPk;
import org.springframework.security.core.GrantedAuthority;

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
public class OrcidGrantedAuthority extends BaseEntity<OrcidGrantedAuthorityPk> implements GrantedAuthority, ProfileAware {

    private static final long serialVersionUID = 2301981481864446645L;

    private ProfileEntity profileEntity;
    private String authority;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false, updatable = false, insertable = false)
    public ProfileEntity getProfileEntity() {
        return profileEntity;
    }
    
    @Transient
    public ProfileEntity getProfile(){
        return profileEntity;
    }

    public void setProfileEntity(ProfileEntity profileEntity) {
        this.profileEntity = profileEntity;
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
