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

import org.orcid.persistence.jpa.entities.keys.ExternalIdentifierEntityPk;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * orcid-entities - Dec 6, 2011 - ExternalIdentifierEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "external_identifier")
@IdClass(ExternalIdentifierEntityPk.class)
public class ExternalIdentifierEntity extends BaseEntity<String> {

    private static final long serialVersionUID = 1L;

    private String externalIdReference;
    private String externalIdCommonName;
    private String externalIdUrl;
    private ProfileEntity externalIdOrcid;
    private ProfileEntity owner;

    @Override
    @Transient
    public String getId() {
        return null;
    }

    @Id
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

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "external_orcid", nullable = true, updatable = false)
    public ProfileEntity getExternalIdOrcid() {
        return externalIdOrcid;
    }

    public void setExternalIdOrcid(ProfileEntity externalIdOrcid) {
        this.externalIdOrcid = externalIdOrcid;
    }

    /**
     * @return the owner
     */
    @Id
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "orcid", nullable = false)
    public ProfileEntity getOwner() {
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

}
