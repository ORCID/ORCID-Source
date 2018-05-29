package org.orcid.persistence.jpa.entities;

import static org.orcid.utils.NullUtils.compareObjectsNullSafe;

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

import org.apache.commons.lang3.StringUtils;
import org.orcid.utils.OrcidStringUtils;

@Entity
@Table(name = "peer_review")
public class PeerReviewEntity extends SourceAwareEntity<Long> implements Comparable<PeerReviewEntity>, ProfileAware, DisplayIndexInterface {
    
    private static final long serialVersionUID = -172752706595347541L;
    private Long id;
    private ProfileEntity profile;
    private String role;
    private OrgEntity org;
    private String externalIdentifiersJson;
    private String url;
    private String type;
    private CompletionDateEntity completionDate;        
    private String visibility;                
    private String subjectExternalIdentifiersJson;
    private String subjectType;
    private String subjectContainerName;
    private String subjectName;
    private String subjectTranslatedName;
    private String subjectTranslatedNameLanguageCode;
    private String subjectUrl;
    private String groupId;
    private Long displayIndex;
    
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "peer_review_seq")
    @SequenceGenerator(name = "peer_review_seq", sequenceName = "peer_review_seq")
    public Long getId() {
        return id;
    }

    @Column(name = "peer_review_role")
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @ManyToOne(fetch = FetchType.EAGER, cascade = { CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "org_id", nullable = false)
    public OrgEntity getOrg() {
        return org;
    }

    public void setOrg(OrgEntity org) {
        this.org = org;
    }

    @Column(name = "external_identifiers_json")
    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }

    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }

    @Column(name = "url", length = 350)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "peer_review_type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public CompletionDateEntity getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(CompletionDateEntity completionDate) {
        this.completionDate = completionDate;
    }

    @Column
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }
    
    @Override
    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false)
    public ProfileEntity getProfile() {
        return profile;
    }     

    @Column(name = "subject_external_identifiers_json")
    public String getSubjectExternalIdentifiersJson() {
        return subjectExternalIdentifiersJson;
    }

    public void setSubjectExternalIdentifiersJson(String subjectExternalIdentifiersJson) {
        this.subjectExternalIdentifiersJson = subjectExternalIdentifiersJson;
    }

    @Column(name = "subject_type")
    public String getSubjectType() {
        return subjectType;
    }

    public void setSubjectType(String subjectType) {
        this.subjectType = subjectType;
    }

    @Column(name = "subject_container_name")
    public String getSubjectContainerName() {
        return subjectContainerName;
    }

    public void setSubjectContainerName(String subjectContainerName) {
        this.subjectContainerName = subjectContainerName;
    }

    @Column(name = "subject_name")
    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Column(name = "subject_translated_name")
    public String getSubjectTranslatedName() {
        return subjectTranslatedName;
    }

    public void setSubjectTranslatedName(String subjectTranslatedName) {
        this.subjectTranslatedName = subjectTranslatedName;
    }

    @Column(name = "subject_translated_name_language_code")
    public String getSubjectTranslatedNameLanguageCode() {      
        return subjectTranslatedNameLanguageCode;
    }

    public void setSubjectTranslatedNameLanguageCode(String subjectTranslatedNameLanguageCode) {
        this.subjectTranslatedNameLanguageCode = subjectTranslatedNameLanguageCode;
    }

    @Column(name = "subject_url")
    public String getSubjectUrl() {
        return subjectUrl;
    }

    public void setSubjectUrl(String subjectUrl) {
        this.subjectUrl = subjectUrl;
    }

    @Column(name = "group_id")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /*
     * Dictates the display order for works (and versions of works)
     * works with higher numbers should be displayed first. 
     * 
     * Currently only updatable via ProfileWorkDaoImpl.updateToMaxDisplay
     *
     */

    @Column(name = "display_index", updatable=false)
    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }
    
    public int compareTo(PeerReviewEntity other) {        
        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }
                
        int typeCompare = compareObjectsNullSafe(type, other.getType());
        if(typeCompare != 0) {
            return typeCompare;
        }
        
        int roleCompare = compareObjectsNullSafe(role, other.getRole());
        if(roleCompare != 0) {
            return roleCompare;
        }
        
        int completionDateCompare = compareObjectsNullSafe((FuzzyDateEntity)completionDate, (FuzzyDateEntity)other.getCompletionDate());
        if(completionDateCompare != 0) {
            return completionDateCompare;
        }
              
        int urlCompare = OrcidStringUtils.compareStrings(url, other.getUrl());
        if(urlCompare != 0) {
            return urlCompare;
        }
        
        int extIdsCompare = OrcidStringUtils.compareStrings(externalIdentifiersJson, other.getExternalIdentifiersJson());
        if(extIdsCompare != 0) {
            return extIdsCompare;
        }
        
        int compareOrgName = OrcidStringUtils.compareStrings(org.getName(), other.getOrg().getName());
        if (compareOrgName != 0) {
            return compareOrgName;
        }

        int compareOrgCountry = OrcidStringUtils.compareStrings(org.getCountry() == null ? null : org.getCountry(), other.getOrg().getCountry() == null ? null : other.getOrg()
                .getCountry());
        if (compareOrgCountry != 0) {
            return compareOrgCountry;
        }

        int compareOrgCity = OrcidStringUtils.compareStrings(org.getCity(), other.getOrg().getCity());
        if (compareOrgCity != 0) {
            return compareOrgCity;
        }
        
        int subjectExtIdCompare = OrcidStringUtils.compareStrings(subjectExternalIdentifiersJson, other.getSubjectExternalIdentifiersJson());
        if(subjectExtIdCompare != 0) {
            return subjectExtIdCompare;
        }
        
        int subjectTypeCompare = compareObjectsNullSafe(subjectType, other.getSubjectType());
        if(subjectTypeCompare != 0) {
            return subjectTypeCompare;
        }
        
        int subjectContainerNameCompare = OrcidStringUtils.compareStrings(subjectContainerName, other.getSubjectContainerName());
        if(subjectContainerNameCompare != 0) {
            return subjectContainerNameCompare;
        }
        
        int subjectNameCompare = OrcidStringUtils.compareStrings(subjectName, other.getSubjectName());
        if(subjectNameCompare != 0) {
            return subjectNameCompare;
        }
        
        int subjectTranslatedNameCompare = OrcidStringUtils.compareStrings(subjectTranslatedName, other.getSubjectTranslatedName());
        if(subjectTranslatedNameCompare != 0) {
            return subjectTranslatedNameCompare;
        }
        
        int subjectTranslatedNameLanguageCodeCompare = OrcidStringUtils.compareStrings(subjectTranslatedNameLanguageCode, other.getSubjectTranslatedNameLanguageCode());
        if(subjectTranslatedNameLanguageCodeCompare != 0) {
            return subjectTranslatedNameLanguageCodeCompare;
        }
        
        int subjectUrlCompare = OrcidStringUtils.compareStrings(subjectUrl, other.getSubjectUrl());
        if(subjectUrlCompare != 0) {
            return subjectUrlCompare;
        }
        
        int groupIdCompare = OrcidStringUtils.compareStrings(groupId, other.getGroupId());        
        if(groupIdCompare != 0) {
            return groupIdCompare;
        }
        
        if(StringUtils.isEmpty(getElementSourceId())) {
            if(!StringUtils.isEmpty(other.getElementSourceId())) {
                return -1;
            }
        } else {
            if(StringUtils.isEmpty(other.getElementSourceId())) {
                return 1;
            } else {
                int sourceCompare = OrcidStringUtils.compareStrings(getElementSourceId(), other.getElementSourceId());
                if(sourceCompare != 0) {
                    return sourceCompare;
                }
            }
        }        
        
        return 0;
    }
    
    public void clean() {
        externalIdentifiersJson = null;
        url = null;
        type = null;
        completionDate = null;        
        visibility = null;   
        subjectExternalIdentifiersJson = null;
        subjectContainerName = null;
        subjectName = null;
        subjectTranslatedName = null;
        subjectTranslatedNameLanguageCode = null;
        subjectUrl = null;
        groupId = null;
    }
}
