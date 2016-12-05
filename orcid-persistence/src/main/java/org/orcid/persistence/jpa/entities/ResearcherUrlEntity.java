/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.persistence.constants.SiteConstants;

/**
 * orcid-entities - Dec 6, 2011 - ElectronicResourceNumTypeEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "researcher_url")
public class ResearcherUrlEntity extends SourceAwareEntity<Long> implements Comparable<ResearcherUrlEntity>, ProfileAware, DisplayIndexInterface {

    private static final long serialVersionUID = -632507196189018770L;

    private Long id;
    private String url;
    private String urlName;
    private ProfileEntity user;    
    private Visibility visibility;
    private Long displayIndex;

    public ResearcherUrlEntity() {
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "researcher_url_seq")
    @SequenceGenerator(name = "researcher_url_seq", sequenceName = "researcher_url_seq")
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "url", length = SiteConstants.URL_MAX_LENGTH)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "url_name", length = SiteConstants.URL_MAX_LENGTH)
    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    /**
     * @return the user
     */
    @ManyToOne
    @JoinColumn(name = "orcid", nullable = false)
    public ProfileEntity getUser() {
        return user;
    }

    @Transient
    @Override
    public ProfileEntity getProfile() {
        return user;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(ProfileEntity user) {
        this.user = user;
    }    

    @Basic
    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    @Column(name = "display_index")
    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }
    
    @Override
    public int compareTo(ResearcherUrlEntity other) {
        String otherUrl = other.getUrl();
        if (url == null) {
            return otherUrl == null ? 0 : -1;
        } else {
            if (url.compareTo(otherUrl) != 0)
                return url.compareTo(otherUrl);
            else {
                return otherUrl == null ? 1 : url.compareTo(otherUrl);
            }
        }
    }

}
