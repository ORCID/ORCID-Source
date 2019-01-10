package org.orcid.core.manager.v3.read_only.impl;

import java.util.Iterator;
import java.util.List;

import org.orcid.core.manager.v3.read_only.ActivitiesSummaryManagerReadOnly;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ProfileFundingManagerReadOnly;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.rc2.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Educations;
import org.orcid.jaxb.model.v3.rc2.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Employments;
import org.orcid.jaxb.model.v3.rc2.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Fundings;
import org.orcid.jaxb.model.v3.rc2.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.rc2.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Memberships;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.rc2.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResources;
import org.orcid.jaxb.model.v3.rc2.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Services;
import org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Works;

public class ActivitiesSummaryManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ActivitiesSummaryManagerReadOnly {    
    private AffiliationsManagerReadOnly affiliationsManager;
    
    private ProfileFundingManagerReadOnly fundingManager;
    
    private PeerReviewManagerReadOnly peerReviewManager;
    
    private WorkManagerReadOnly workManager;
    
    private ResearchResourceManagerReadOnly researchResourceManager;

    public void setAffiliationsManager(AffiliationsManagerReadOnly affiliationsManager) {
        this.affiliationsManager = affiliationsManager;
    }

    public void setFundingManager(ProfileFundingManagerReadOnly fundingManager) {
        this.fundingManager = fundingManager;
    }

    public void setPeerReviewManager(PeerReviewManagerReadOnly peerReviewManager) {
        this.peerReviewManager = peerReviewManager;
    }

    public void setWorkManager(WorkManagerReadOnly workManager) {
        this.workManager = workManager;
    }  
    
    public void setResearchResourceManager(ResearchResourceManagerReadOnly researchResourceManager) {
        this.researchResourceManager = researchResourceManager;
    }  

    @Override
    public ActivitiesSummary getActivitiesSummary(String orcid, boolean filterVersionOfIdentifiers) {
        return getActivitiesSummary(orcid, false, filterVersionOfIdentifiers);
    }

    @Override
    public ActivitiesSummary getPublicActivitiesSummary(String orcid, boolean filterVersionOfIdentifiers) {
        return getActivitiesSummary(orcid, true, filterVersionOfIdentifiers);
    }

    public ActivitiesSummary getActivitiesSummary(String orcid, boolean justPublic, boolean filterVersionOfIdentifiers) {
        ActivitiesSummary activities = new ActivitiesSummary();

        // Set distinctions
        List<DistinctionSummary> distinctionsList = affiliationsManager.getDistinctionSummaryList(orcid);
        Distinctions distinctions =  new Distinctions(affiliationsManager.groupAffiliations(distinctionsList, justPublic));
        activities.setDistinctions(distinctions);
        
        // Set educations
        List<EducationSummary> educationsList = affiliationsManager.getEducationSummaryList(orcid);
        Educations educations = new Educations(affiliationsManager.groupAffiliations(educationsList, justPublic));
        activities.setEducations(educations);

        // Set employments
        List<EmploymentSummary> employmentList = affiliationsManager.getEmploymentSummaryList(orcid);
        Employments employments = new Employments(affiliationsManager.groupAffiliations(employmentList, justPublic));
        activities.setEmployments(employments);

        // Set invited positions
        List<InvitedPositionSummary> invitedPositionsList = affiliationsManager.getInvitedPositionSummaryList(orcid);
        InvitedPositions invitedPositions = new InvitedPositions(affiliationsManager.groupAffiliations(invitedPositionsList, justPublic));
        activities.setInvitedPositions(invitedPositions);
        
        // Set memberships
        List<MembershipSummary> membershipsList = affiliationsManager.getMembershipSummaryList(orcid);
        Memberships memberships = new Memberships(affiliationsManager.groupAffiliations(membershipsList, justPublic));
        activities.setMemberships(memberships);
        
        // Set qualifications
        List<QualificationSummary> qualificationsList = affiliationsManager.getQualificationSummaryList(orcid);
        Qualifications qualifications = new Qualifications(affiliationsManager.groupAffiliations(qualificationsList, justPublic));
        activities.setQualifications(qualifications);
        
        // Set services
        List<ServiceSummary> servicesList = affiliationsManager.getServiceSummaryList(orcid);
        Services services = new Services(affiliationsManager.groupAffiliations(servicesList, justPublic));
        activities.setServices(services);
        
        // Set fundings
        List<FundingSummary> fundingSummaries = fundingManager.getFundingSummaryList(orcid);
        Fundings fundings = fundingManager.groupFundings(fundingSummaries, justPublic);        
        activities.setFundings(fundings);

        // Set peer reviews
        List<PeerReviewSummary> peerReviewSummaries = peerReviewManager.getPeerReviewSummaryList(orcid);
        PeerReviews peerReviews = peerReviewManager.groupPeerReviews(peerReviewSummaries, justPublic);        
        activities.setPeerReviews(peerReviews);

        // Set works
        List<WorkSummary> workSummaries = workManager.getWorksSummaryList(orcid);
        
        // Should we filter the version-of identifiers before grouping?
        if(filterVersionOfIdentifiers) {
            for(WorkSummary w : workSummaries) {
                if(w.getExternalIdentifiers() != null && !w.getExternalIdentifiers().getExternalIdentifier().isEmpty()) {
                    Iterator<ExternalID> it = w.getExternalIdentifiers().getExternalIdentifier().iterator();
                    while(it.hasNext()) {
                        ExternalID extId = it.next();
                        if(Relationship.VERSION_OF.equals(extId.getRelationship())) {
                            it.remove();
                        }
                    }
                }
            }
        }
        
        Works works = workManager.groupWorks(workSummaries, justPublic);        
        activities.setWorks(works);
        
        List<ResearchResourceSummary> researchResourceSummary = researchResourceManager.getResearchResourceSummaryList(orcid); 
        ResearchResources researchResources = researchResourceManager.groupResearchResources(researchResourceSummary, justPublic);
        activities.setResearchResources(researchResources);

        return activities;
    }
}
