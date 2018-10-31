package org.orcid.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.rc2.common.Organization;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.GroupAble;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.Date;

public class ResearchResource implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date createdDate;

    private String path;
    
    private String displayIndex;

    private String source;

    private String sourceName;

    private String putCode;

    private String url;

    private List<ActivityExternalIdentifier> externalIdentifiers;

    private String title;    
    
    private String translatedTitle;
    
    private String translatedTitleLanguageCode;
    
    private Date startDate;
   
    private Date  endDate;
    
    private Visibility visibility;
    
    private List<Org> hosts;
    
    private List<ResearchResourceItem> items;
    
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(String displayIndex) {
        this.displayIndex = displayIndex;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getPutCode() {
        return putCode;
    }

    public void setPutCode(String putCode) {
        this.putCode = putCode;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ActivityExternalIdentifier> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<ActivityExternalIdentifier> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    public String getTranslatedTitleLanguageCode() {
        return translatedTitleLanguageCode;
    }

    public void setTranslatedTitleLanguageCode(String translatedTitleLanguageCode) {
        this.translatedTitleLanguageCode = translatedTitleLanguageCode;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<Org> getHosts() {
        return hosts;
    }

    public void setHosts(List<Org> hosts) {
        this.hosts = hosts;
    }
    
    public List<ResearchResourceItem> getItems() {
        return items;
    }

    public void setItems(List<ResearchResourceItem> items) {
        this.items = items;
    }

    public static ResearchResource fromValue(org.orcid.jaxb.model.v3.rc2.record.ResearchResource resource) {
        if (resource == null)
            return null;
        
        ResearchResource researchResource = new ResearchResource();
        
        if(resource.getProposal().getHosts() != null) {
            List<Org> hosts = new ArrayList<>();
            for (Organization organization : resource.getProposal().getHosts().getOrganization()) {
                hosts.add(Org.valueOf(organization));
            }
            researchResource.setHosts(hosts);
        }
        
        if(resource.getProposal().getExternalIdentifiers() != null) {
            List<ActivityExternalIdentifier> activityExternalIdentifiers = new ArrayList<>();
            for (GroupAble groupable : resource.getProposal().getExternalIdentifiers().getExternalIdentifier()) {
                ExternalID externalID = (ExternalID) groupable;
                ActivityExternalIdentifier activityExternalIdentifier = ActivityExternalIdentifier.valueOf(externalID);
                activityExternalIdentifiers.add(activityExternalIdentifier);
            }
            researchResource.setExternalIdentifiers(activityExternalIdentifiers);
        }
        
        if(resource.getResourceItems() != null) {
            List<ResearchResourceItem> items = new ArrayList<>();
            for (org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItem item : resource.getResourceItems()) {
                ResearchResourceItem i = ResearchResourceItem.fromValue(item);
                items.add(i);
            }
            researchResource.setItems(items);
        }
        
        researchResource.setCreatedDate(Date.valueOf(resource.getCreatedDate()));
        
        if(resource.getProposal().getStartDate() != null) {
            researchResource.setStartDate(Date.valueOf(resource.getProposal().getStartDate()));
        }
        
        if(resource.getProposal().getEndDate() != null) {
            researchResource.setEndDate(Date.valueOf(resource.getProposal().getEndDate()));
        }
        
        if(resource.getProposal().getTitle().getTitle() != null) {
            researchResource.setTitle(resource.getProposal().getTitle().getTitle().getContent());
        }
        
        if(resource.getProposal().getTitle().getTranslatedTitle() != null) {
            researchResource.setTranslatedTitle(resource.getProposal().getTitle().getTranslatedTitle().getContent());
            researchResource.setTranslatedTitleLanguageCode(resource.getProposal().getTitle().getTranslatedTitle().getLanguageCode());
        }  
          
        if(resource.getProposal().getUrl() != null) {
            researchResource.setUrl(resource.getProposal().getUrl().getValue());
        }
        
        if(resource.getPutCode() != null) {
            researchResource.setPutCode(resource.getPutCode().toString());
        }
        
        if(resource.getDisplayIndex() != null) {
            researchResource.setDisplayIndex(resource.getDisplayIndex());
        }

        if(resource.getSource() != null) {
            // Set source
            researchResource.setSource(resource.getSource().retrieveSourcePath());
            if(resource.getSource().getSourceName() != null) {
                researchResource.setSourceName(resource.getSource().getSourceName().getContent());
            }
        }
        
        if(resource.getVisibility() != null) {
            researchResource.setVisibility(Visibility.valueOf(resource.getVisibility()));
        }
        
        return researchResource;
    }

}
