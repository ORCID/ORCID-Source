package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.common.Organization;
import org.orcid.jaxb.model.v3.rc1.common.PublicationDate;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.GroupAble;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.WorkType;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.pojo.ResearchResource;
import org.orcid.pojo.grouping.ActivityGroup;
import org.orcid.pojo.ResearchResourceGroupPojo;

public class ResearchResourceGroupPojo extends ActivityGroup {

    private static final long serialVersionUID = 1L;

    private List<ResearchResource> researchResources;

    private ResearchResource defaultResearchResource;

    public List<ResearchResource> getResearchResources() {
        return researchResources;
    }

    public void setResearchResources(List<ResearchResource> researchResources) {
        this.researchResources = researchResources;
    }

    public ResearchResource getDefaultResearchResource() {
        return defaultResearchResource;
    }

    public void setdefaultResearchResource(ResearchResource defaultResearchResource) {
        this.defaultResearchResource = defaultResearchResource;
    }

    public static ResearchResourceGroupPojo valueOf(org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceGroup researchResourceGroup, int id, String orcid) {
        ResearchResourceGroupPojo group = new ResearchResourceGroupPojo();
        group.setGroupId(id);
        group.setResearchResources(new ArrayList<>());

        Long maxDisplayIndex = null;
        for (ResearchResourceSummary researchResourceSummary : researchResourceGroup.getResearchResourceSummary()) {
            ResearchResource researchResource = getResearchResource(researchResourceSummary);
            group.getResearchResources().add(researchResource);

            Long displayIndex = Long.parseLong(researchResourceSummary.getDisplayIndex());
            if (maxDisplayIndex == null || displayIndex > maxDisplayIndex) {
                maxDisplayIndex = displayIndex;
                group.setActivePutCode(researchResourceSummary.getPutCode());
                group.setActiveVisibility(researchResourceSummary.getVisibility().name());
                group.setdefaultResearchResource(researchResource);
            }

            if (researchResourceSummary.getSource().retrieveSourcePath().equals(orcid)) {
                group.setUserVersionPresent(true);
            }

        }

        if (researchResourceGroup.getIdentifiers() != null) {
            List<ActivityExternalIdentifier> activityExternalIdentifiersList = new ArrayList<ActivityExternalIdentifier>();
            for (ExternalID extId : researchResourceGroup.getIdentifiers().getExternalIdentifier()) {
                activityExternalIdentifiersList.add(ActivityExternalIdentifier.valueOf(extId));
            }
            group.setExternalIdentifiers(activityExternalIdentifiersList);
        }
        return group;
    }

    private static ResearchResource getResearchResource(ResearchResourceSummary researchResourceSummary) {
        ResearchResource researchResource = new ResearchResource();
        
        if(researchResourceSummary.getProposal().getHosts() != null) {
            List<Org> hosts = new ArrayList<>();
            for (Organization organization : researchResourceSummary.getProposal().getHosts().getOrganization()) {
                hosts.add(Org.valueOf(organization));
            }
            researchResource.setHosts(hosts);
        }
        
        if(researchResourceSummary.getProposal().getExternalIdentifiers() != null) {
            List<ActivityExternalIdentifier> activityExternalIdentifiers = new ArrayList<>();
            for (GroupAble groupable : researchResourceSummary.getProposal().getExternalIdentifiers().getExternalIdentifier()) {
                ExternalID externalID = (ExternalID) groupable;
                ActivityExternalIdentifier activityExternalIdentifier = ActivityExternalIdentifier.valueOf(externalID);
                activityExternalIdentifiers.add(activityExternalIdentifier);
            }
            researchResource.setExternalIdentifiers(activityExternalIdentifiers);
        }
        
        
        researchResource.setCreatedDate(Date.valueOf(researchResourceSummary.getCreatedDate()));
        
        if(researchResourceSummary.getProposal().getStartDate() != null) {
            researchResource.setStartDate(Date.valueOf(researchResourceSummary.getProposal().getStartDate()));
        }
        
        if(researchResourceSummary.getProposal().getEndDate() != null) {
            researchResource.setEndDate(Date.valueOf(researchResourceSummary.getProposal().getEndDate()));
        }
        
        if(researchResourceSummary.getProposal().getTitle().getTitle() != null) {
            researchResource.setTitle(researchResourceSummary.getProposal().getTitle().getTitle().getContent());
        }
        
        if(researchResourceSummary.getProposal().getTitle().getTranslatedTitle() != null) {
            researchResource.setTranslatedTitle(researchResourceSummary.getProposal().getTitle().getTranslatedTitle().getContent());
            researchResource.setTranslatedTitleLanguageCode(researchResourceSummary.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
        }  
          
        if(researchResourceSummary.getProposal().getUrl() != null) {
            researchResource.setUrl(researchResourceSummary.getProposal().getUrl().getValue());
        }
        
        if(researchResourceSummary.getPutCode() != null) {
            researchResource.setPutCode(researchResourceSummary.getPutCode().toString());
        }
        
        if(researchResourceSummary.getDisplayIndex() != null) {
            researchResource.setDisplayIndex(researchResourceSummary.getDisplayIndex());
        }

        if(researchResourceSummary.getSource() != null) {
            // Set source
            researchResource.setSource(researchResourceSummary.getSource().retrieveSourcePath());
            if(researchResourceSummary.getSource().getSourceName() != null) {
                researchResource.setSourceName(researchResourceSummary.getSource().getSourceName().getContent());
            }
        }
        
        if(researchResourceSummary.getVisibility() != null) {
            researchResource.setVisibility(Visibility.valueOf(researchResourceSummary.getVisibility()));
        }
        return researchResource;
    }

}
