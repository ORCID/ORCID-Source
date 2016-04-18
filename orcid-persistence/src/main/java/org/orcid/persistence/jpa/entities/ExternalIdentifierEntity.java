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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.orcid.jaxb.model.common_rc2.Visibility;

/**
 * orcid-entities - Dec 6, 2011 - ExternalIdentifierEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "external_identifier")
public class ExternalIdentifierEntity extends BaseEntity<Long> implements Comparable<ExternalIdentifierEntity>, ProfileAware, SourceAware {

    private static final long serialVersionUID = 1L;

    private String externalIdReference;
    private String externalIdCommonName;
    private String externalIdUrl;
    private ProfileEntity owner;
    private SourceEntity source;
    private Long id;
    private Visibility visibility;
    private Long displayIndex;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "external_identifier_id_seq")
    @SequenceGenerator(name = "external_identifier_id_seq", sequenceName = "external_identifier_id_seq")
    @Override
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name = "external_id_reference", length = 255)
    public String getExternalIdReference() {
        return externalIdReference;
    }

    public void setExternalIdReference(String externalIdReference) {
        this.externalIdReference = externalIdReference;
    }

    @Column(name = "external_id_type", length = 255)
    public String getExternalIdCommonName() {
        return externalIdCommonName;
    }

    public void setExternalIdCommonName(String externalIdCommonName) {
        this.externalIdCommonName = externalIdCommonName;
    }

    public SourceEntity getSource() {
        return source;
    }

    public void setSource(SourceEntity source) {
        this.source = source;
    }

    /**
     * @return the owner
     */    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false)
    public ProfileEntity getOwner() {
        return owner;
    }

    @Override
    @Transient
    public ProfileEntity getProfile() {
        return owner;
    }

    /**
     * @param owner
     *            the owner to set
     */
    public void setOwner(ProfileEntity owner) {
        this.owner = owner;
    }

    @Column(name = "external_id_url", length = 300)
    public String getExternalIdUrl() {
        return externalIdUrl;
    }

    public void setExternalIdUrl(String externalIdUrl) {
        this.externalIdUrl = externalIdUrl;
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
    public int compareTo(ExternalIdentifierEntity other) {
        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }

        int result = 0;

        // First check externalIdUrl
        if (other.getExternalIdUrl() == null) {
            if (externalIdUrl == null) {
                result = 0;
            } else {
                result = 1;
            }
        } else {
            if (externalIdUrl == null) {
                result = -1;
            } else {
                result = externalIdUrl.compareToIgnoreCase(other.getExternalIdUrl());
            }
        }

        // If they are still equal, compare against the externalIdReference
        if (result == 0) {
            if (other.getExternalIdReference() == null) {
                if (externalIdReference == null) {
                    result = 0;
                } else {
                    result = 1;
                }
            } else {
                if (externalIdReference == null) {
                    result = -1;
                } else {
                    result = externalIdReference.compareToIgnoreCase(other.getExternalIdReference());
                }
            }
        }

        // If they are still equal, compare against the 
        if (result == 0) {
            if(other.getExternalIdCommonName() == null) {
                if(externalIdCommonName == null) {
                    result = 0;
                } else {
                    result = 1;
                }
            } else {
                if(externalIdCommonName == null) {
                    result = -1;                   
                } else {
                    result = externalIdCommonName.compareToIgnoreCase(other.getExternalIdCommonName());
                }                   
            }
        }
        return result;
    }

    /**
     * Clean simple properties to allow entity to be reused.
     */
    public void clean() {
        externalIdCommonName = null;
        externalIdUrl = null;
        externalIdReference = null;
    }

}
