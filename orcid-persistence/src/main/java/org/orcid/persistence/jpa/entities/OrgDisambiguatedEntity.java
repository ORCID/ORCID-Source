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

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.jaxb.model.message.Iso3166Country;

/**
 * 
 * @author Will Simpson
 * 
 */

@Entity
@Table(name = "org_disambiguated")
public class OrgDisambiguatedEntity extends BaseEntity<Long> {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String sourceId;
    private String sourceUrl;
    private String sourceType;
    private String orgType;
    private String name;
    private String city;
    private String region;
    private Iso3166Country country;
    private String url;
    private String status;
    private Date lastIndexedDate;
    private IndexingStatus indexingStatus = IndexingStatus.PENDING;
    private Integer popularity;
    private Set<OrgDisambiguatedExternalIdentifierEntity> externalIdentifiers;
    private Set<OrgEntity> orgs;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "org_disambiguated_seq")
    @SequenceGenerator(name = "org_disambiguated_seq", sequenceName = "org_disambiguated_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "source_id")
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Column(name = "source_url")
    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    @Column(name = "source_type")
    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    public Iso3166Country getCountry() {
        return country;
    }

    public void setCountry(Iso3166Country country) {
        this.country = country;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "last_indexed_date")
    public Date getLastIndexedDate() {
        return lastIndexedDate;
    }

    public void setLastIndexedDate(Date lastIndexedDate) {
        this.lastIndexedDate = lastIndexedDate;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    @Column(name = "indexing_status")
    public IndexingStatus getIndexingStatus() {
        return indexingStatus;
    }

    public void setIndexingStatus(IndexingStatus indexingStatus) {
        this.indexingStatus = indexingStatus;
    }

    public Integer getPopularity() {
        return popularity;
    }

    public void setPopularity(Integer popularity) {
        this.popularity = popularity;
    }

    @OneToMany(mappedBy = "orgDisambiguated")
    public Set<OrgDisambiguatedExternalIdentifierEntity> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(Set<OrgDisambiguatedExternalIdentifierEntity> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    @OneToMany(mappedBy = "orgDisambiguated")
    public Set<OrgEntity> getOrgs() {
        return orgs;
    }

    public void setOrgs(Set<OrgEntity> orgs) {
        this.orgs = orgs;
    }

    @Column(name = "org_type")
    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

}
