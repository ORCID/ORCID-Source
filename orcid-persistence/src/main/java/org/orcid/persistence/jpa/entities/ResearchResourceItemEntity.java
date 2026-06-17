package org.orcid.persistence.jpa.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "research_resource_item")
public class ResearchResourceItemEntity {

    private static final long serialVersionUID = 1L;
    private Long id;

    private String resourceName;
    private String resourceType;
    private List<OrgEntity> hosts;
    private String externalIdentifiersJson;
    private String url;
    private int itemOrder;
    
    private ResearchResourceEntity researchResourceEntity;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="research_resource_id", nullable=false)
    public ResearchResourceEntity getResearchResourceEntity() {
        return researchResourceEntity;
    }

    public void setResearchResourceEntity(ResearchResourceEntity researchResourceEntity) {
        this.researchResourceEntity = researchResourceEntity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "research_resource_item_seq")
    @SequenceGenerator(name = "research_resource_item_seq", sequenceName = "research_resource_item_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name = "resource_name")
    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Column(name = "resource_type")
    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    @Column(name = "external_identifiers_json")
    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }

    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }

    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    @Column(name = "item_index")
    public int getItemOrder() {
        return itemOrder;
    }

    public void setItemOrder(int itemOrder) {
        this.itemOrder = itemOrder;
    }

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "research_resource_item_org", 
               joinColumns = { @JoinColumn(name = "research_resource_item_id") }, 
               inverseJoinColumns = { @JoinColumn(name = "org_id") })
    @OrderColumn(name="org_index")
    public List<OrgEntity> getHosts() {
        return hosts;
    }

    public void setHosts(List<OrgEntity> hosts) {
        this.hosts = hosts;
    }

}
