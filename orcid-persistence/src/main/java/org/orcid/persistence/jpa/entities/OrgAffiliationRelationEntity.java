package org.orcid.persistence.jpa.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.utils.NullUtils;

/**
 * 
 * @author Will Simpson
 * 
 */
@Entity
@Table(name = "org_affiliation_relation")
public class OrgAffiliationRelationEntity extends SourceAwareEntity<Long> implements Comparable<OrgAffiliationRelationEntity>, ProfileAware, DisplayIndexInterface {

    private static final long serialVersionUID = 1L;

    private Long id;
    private OrgEntity org;
    private ProfileEntity profile;
    private String affiliationType;
    private String title;
    private String department;
    private StartDateEntity startDate;
    private EndDateEntity endDate;
    private String visibility;    
    private String url;
    private String externalIdentifiersJson;
    protected Long displayIndex;

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "org_affiliation_relation_seq")
    @SequenceGenerator(name = "org_affiliation_relation_seq", sequenceName = "org_affiliation_relation_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "org_id", nullable = false)
    public OrgEntity getOrg() {
        return org;
    }

    public void setOrg(OrgEntity org) {
        this.org = org;
    }

    @Override
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false, updatable = false, insertable = true)
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    @Column(name = "org_affiliation_relation_role", length = 200)
    public String getAffiliationType() {
        return affiliationType;
    }

    public void setAffiliationType(String affiliationType) {
        this.affiliationType = affiliationType;
    }

    @Column(name = "org_affiliation_relation_title", length = 4000)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public StartDateEntity getStartDate() {
        return startDate;
    }

    public void setStartDate(StartDateEntity startDate) {
        this.startDate = startDate;
    }

    public EndDateEntity getEndDate() {
        return endDate;
    }

    public void setEndDate(EndDateEntity endDate) {
        this.endDate = endDate;
    }

    @Column
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
    
    @Column(length = 350)
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Column(name = "external_ids_json")
    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }

    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }
    
    @Column(name = "display_index")
    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }

    @Override
    public int compareTo(OrgAffiliationRelationEntity other) {
        if (other == null) {
            return 1;
        }
        int compareEnds = compareEnds(endDate, other.getEndDate());
        if (compareEnds != 0) {
            return compareEnds;
        }
        int compareStarts = compareStarts(startDate, other.getStartDate());
        if (compareStarts != 0) {
            return compareStarts;
        }
        int compareTypes = compareTypes(affiliationType, other.getAffiliationType());
        if (compareTypes != 0) {
            return compareTypes;
        }
        int compareNames = compareNames(org.getName(), other.getOrg().getName());
        if (compareNames != 0) {
            return compareNames;
        }
        int compareDepartments = compareDepartments(department, other.getDepartment());
        if (compareDepartments != 0) {
            return compareDepartments;
        }
        return compareTitles(title, other.getTitle());
    }

    private int compareEnds(FuzzyDateEntity endDate, FuzzyDateEntity otherEndDate) {
        if (NullUtils.anyNull(endDate, otherEndDate)) {
            return -NullUtils.compareNulls(endDate, otherEndDate);
        }
        return -endDate.compareTo(otherEndDate);
    }

    private int compareStarts(FuzzyDateEntity startDate, FuzzyDateEntity otherStartDate) {
        if (NullUtils.anyNull(startDate, otherStartDate)) {
            return NullUtils.compareNulls(startDate, otherStartDate);
        }
        return -startDate.compareTo(otherStartDate);
    }

    private int compareTypes(String affiliationType, String otherAffiliationType) {
        if (NullUtils.anyNull(affiliationType, otherAffiliationType)) {
            return NullUtils.compareNulls(affiliationType, otherAffiliationType);
        }
        return affiliationType.compareTo(otherAffiliationType);
    }

    private int compareNames(String name, String otherName) {
        if (NullUtils.anyNull(name, otherName)) {
            return NullUtils.compareNulls(name, otherName);
        }
        return name.compareTo(otherName);
    }

    private int compareDepartments(String department, String otherDepartment) {
        if (NullUtils.anyNull(department, otherDepartment)) {
            return NullUtils.compareNulls(department, otherDepartment);
        }
        return department.compareTo(otherDepartment);
    }

    private int compareTitles(String title, String otherTitle) {
        if (NullUtils.anyNull(title, otherTitle)) {
            return NullUtils.compareNulls(title, otherTitle);
        }
        return title.compareTo(otherTitle);
    }

    /**
     * Clean simple fields so that entity can be reused.
     */
    public void clean() {
        affiliationType = null;
        title = null;
        department = null;
        startDate = null;
        endDate = null;
        visibility = null;
    }
}