package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.rc2.common.Organization;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.GroupAble;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;

public class ResearchResourceItem {
    
    private String resourceName;

    private String resourceType;

    private List<Org> hosts;

    private List<ActivityExternalIdentifier> externalIdentifiers;
    
    private List<ResearchResourceItem> items;

    private String url;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public List<Org> getHosts() {
        return hosts;
    }

    public void setHosts(List<Org> hosts) {
        this.hosts = hosts;
    }

    public List<ActivityExternalIdentifier> getExternalIdentifiers() {
        return externalIdentifiers;
    }

    public void setExternalIdentifiers(List<ActivityExternalIdentifier> externalIdentifiers) {
        this.externalIdentifiers = externalIdentifiers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<ResearchResourceItem> getItems() {
        return items;
    }

    public void setItems(List<ResearchResourceItem> items) {
        this.items = items;
    }
    
    public static ResearchResourceItem fromValue(org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItem item) {
        if (item == null)
            return null;
        
        ResearchResourceItem researchResourceItem = new ResearchResourceItem();
        
        if(item.getHosts() != null) {
            List<Org> hosts = new ArrayList<>();
            for (Organization organization : item.getHosts().getOrganization()) {
                hosts.add(Org.valueOf(organization));
            }
            researchResourceItem.setHosts(hosts);
        }
        
        if(item.getExternalIdentifiers() != null) {
            List<ActivityExternalIdentifier> activityExternalIdentifiers = new ArrayList<>();
            for (GroupAble groupable : item.getExternalIdentifiers().getExternalIdentifier()) {
                ExternalID externalID = (ExternalID) groupable;
                ActivityExternalIdentifier activityExternalIdentifier = ActivityExternalIdentifier.valueOf(externalID);
                activityExternalIdentifiers.add(activityExternalIdentifier);
            }
            researchResourceItem.setExternalIdentifiers(activityExternalIdentifiers);
        }
        
        if(item.getResourceName() != null) {
            researchResourceItem.setResourceName(item.getResourceName());
        }
        
        if(item.getResourceType() != null) {
            researchResourceItem.setResourceType(item.getResourceType());
        }
        
        if(item.getUrl() != null) {
            researchResourceItem.setUrl(item.getUrl().getValue());
        }
        
        return researchResourceItem;
    }
    
}
