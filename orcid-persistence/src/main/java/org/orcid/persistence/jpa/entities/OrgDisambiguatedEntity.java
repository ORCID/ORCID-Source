package org.orcid.persistence.jpa.entities;

import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedNativeQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * 
 * @author Will Simpson
 * 
 */

@Entity
@Table(name = "org_disambiguated")
@NamedNativeQuery(name = OrgDisambiguatedEntity.FIND_DUPLICATES, query = "SELECT o.* FROM org_disambiguated o JOIN (SELECT name, city, region, country FROM org_disambiguated GROUP BY name, city, region, country HAVING COUNT(*) > 1) d ON (d.name = o.name OR (d.name IS NULL AND o.name IS NULL)) AND (d.city = o.city OR (d.city IS NULL AND o.city IS NULL)) AND (d.region = o.region OR (d.region IS NULL AND o.region IS NULL)) AND (d.country = o.country OR (d.country IS NULL AND o.country IS NULL)) ORDER BY o.source_type, o.name, o.city, o.region, o.country, o.org_type;", resultClass = OrgDisambiguatedEntity.class)
public class OrgDisambiguatedEntity extends BaseEntity<Long> {

    public static final String FIND_DUPLICATES = "findDuplicates";

    private static final long serialVersionUID = 1L;

    private Long id;
    private String sourceId;
    private String sourceParentId;
    private String sourceUrl;
    private String sourceType;
    private String orgType;
    private String name;
    private String city;
    private String region;
    private String country;
    private String url;
    private String status;
    private Date lastIndexedDate;
    private IndexingStatus indexingStatus = IndexingStatus.PENDING;
    private Integer popularity = 0;
    private Set<OrgDisambiguatedExternalIdentifierEntity> externalIdentifiers;
    private Set<OrgEntity> orgs;
    private MemberChosenOrgDisambiguatedEntity memberChosenOrgDisambiguatedEntity;

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

    @Column(name = "source_parent_id")
    public String getSourceParentId() {
        return sourceParentId;
    }

    public void setSourceParentId(String sourceParentId) {
        this.sourceParentId = sourceParentId;
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

    @Column(name = "status")
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

    @Column
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
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

    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "orgDisambiguated")
    @Fetch(FetchMode.SUBSELECT)
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

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id")
    public MemberChosenOrgDisambiguatedEntity getMemberChosenOrgDisambiguatedEntity() {
        return memberChosenOrgDisambiguatedEntity;
    }

    public void setMemberChosenOrgDisambiguatedEntity(MemberChosenOrgDisambiguatedEntity memberChosenOrgDisambiguatedEntity) {
        this.memberChosenOrgDisambiguatedEntity = memberChosenOrgDisambiguatedEntity;
    }
    
}
