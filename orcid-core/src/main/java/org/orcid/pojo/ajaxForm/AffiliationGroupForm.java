package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;

public class AffiliationGroupForm implements Serializable {
    
    private static final long serialVersionUID = -8347171671099477223L;

    private List<AffiliationForm> affiliations = new ArrayList<>();

    private Long activePutCode;

    private AffiliationForm defaultAffiliation;

    private String groupId;

    private String activeVisibility;

    private Boolean userVersionPresent;

    private List<ActivityExternalIdentifier> externalIdentifiers = new ArrayList<>();

    private AffiliationType affiliationType;

    public List<AffiliationForm> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(List<AffiliationForm> affiliations) {
        this.affiliations = affiliations;
    }

    public Long getActivePutCode() {
        return activePutCode;
    }

    public void setActivePutCode(Long activePutCode) {
        this.activePutCode = activePutCode;
    }

    public AffiliationForm getDefaultAffiliation() {
        return defaultAffiliation;
    }

    public void setDefaultAffiliation(AffiliationForm defaultAffiliation) {
        this.defaultAffiliation = defaultAffiliation;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getActiveVisibility() {
        return activeVisibility;
    }

    public void setActiveVisibility(String activeVisibility) {
        this.activeVisibility = activeVisibility;
    }

    public Boolean isUserVersionPresent() {
        return userVersionPresent;
    }

    public void setUserVersionPresent(Boolean userVersionPresent) {
        this.userVersionPresent = userVersionPresent;
    }

    public List<ActivityExternalIdentifier> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<ActivityExternalIdentifier> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public AffiliationType getAffiliationType() {
        return affiliationType;
    }

    public void setAffiliationType(AffiliationType affiliationType) {
        this.affiliationType = affiliationType;
    }
    
    public static AffiliationGroupForm valueOf(org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationGroup<? extends AffiliationSummary> group, String id, String orcid) {
        AffiliationGroupForm affiliationGroup = new AffiliationGroupForm();
        affiliationGroup.setGroupId(id);
        affiliationGroup.setUserVersionPresent(false);
        
        Long maxDisplayIndex = null;
        for(AffiliationSummary summary : group.getActivities()) {
            if(affiliationGroup.getAffiliationType() == null) {
                if(summary instanceof DistinctionSummary) {
                    affiliationGroup.setAffiliationType(AffiliationType.DISTINCTION);
                } else if(summary instanceof EducationSummary) {
                    affiliationGroup.setAffiliationType(AffiliationType.EDUCATION);
                } else if(summary instanceof EmploymentSummary) {
                    affiliationGroup.setAffiliationType(AffiliationType.EMPLOYMENT);
                } else if(summary instanceof InvitedPositionSummary) {
                    affiliationGroup.setAffiliationType(AffiliationType.INVITED_POSITION);
                } else if(summary instanceof MembershipSummary) {
                    affiliationGroup.setAffiliationType(AffiliationType.MEMBERSHIP);
                } else if(summary instanceof QualificationSummary) {
                    affiliationGroup.setAffiliationType(AffiliationType.QUALIFICATION);
                } else if(summary instanceof ServiceSummary) {
                    affiliationGroup.setAffiliationType(AffiliationType.SERVICE);
                } 
            }
            
            Long displayIndex = 0L;
            if(summary.getDisplayIndex() != null) {
                displayIndex = Long.parseLong(summary.getDisplayIndex());
            } 
            
            if(summary.getSource().retrieveSourcePath().equals(orcid)) {
                affiliationGroup.setUserVersionPresent(true);
            } 
            
            AffiliationForm form = AffiliationForm.valueOf(summary);
            affiliationGroup.getAffiliations().add(form);
            
            if(maxDisplayIndex == null || displayIndex > maxDisplayIndex) {
                affiliationGroup.setActivePutCode(summary.getPutCode());
                affiliationGroup.setActiveVisibility(summary.getVisibility().name());
                affiliationGroup.setDefaultAffiliation(form);
                maxDisplayIndex = displayIndex;
            }
        }
        
        if (group.getIdentifiers() != null) {
            List<ActivityExternalIdentifier> workExternalIdentifiersList = affiliationGroup.getExternalIdentifiers();
            for (ExternalID extId : group.getIdentifiers().getExternalIdentifier()) {                    
                workExternalIdentifiersList.add(ActivityExternalIdentifier.valueOf(extId));
            }            
            affiliationGroup.setExternalIdentifiers(workExternalIdentifiersList);
        }
        
        return affiliationGroup;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((activePutCode == null) ? 0 : activePutCode.hashCode());
        result = prime * result + ((activeVisibility == null) ? 0 : activeVisibility.hashCode());
        result = prime * result + ((affiliationType == null) ? 0 : affiliationType.hashCode());
        result = prime * result + ((affiliations == null) ? 0 : affiliations.hashCode());
        result = prime * result + ((defaultAffiliation == null) ? 0 : defaultAffiliation.hashCode());
        result = prime * result + ((externalIdentifiers == null) ? 0 : externalIdentifiers.hashCode());
        result = prime * result + ((userVersionPresent == null) ? 0 : userVersionPresent.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AffiliationGroupForm other = (AffiliationGroupForm) obj;
        if (activePutCode == null) {
            if (other.activePutCode != null)
                return false;
        } else if (!activePutCode.equals(other.activePutCode))
            return false;
        if (activeVisibility == null) {
            if (other.activeVisibility != null)
                return false;
        } else if (!activeVisibility.equals(other.activeVisibility))
            return false;
        if (affiliationType != other.affiliationType)
            return false;
        if (affiliations == null) {
            if (other.affiliations != null)
                return false;
        } else if (!affiliations.equals(other.affiliations))
            return false;
        if (defaultAffiliation == null) {
            if (other.defaultAffiliation != null)
                return false;
        } else if (!defaultAffiliation.equals(other.defaultAffiliation))
            return false;
        if (externalIdentifiers == null) {
            if (other.externalIdentifiers != null)
                return false;
        } else if (!externalIdentifiers.equals(other.externalIdentifiers))
            return false;
        if (groupId != other.groupId)
            return false;
        if (userVersionPresent == null) {
            if (other.userVersionPresent != null)
                return false;
        } else if (!userVersionPresent.equals(other.userVersionPresent))
            return false;
        return true;
    }        
}