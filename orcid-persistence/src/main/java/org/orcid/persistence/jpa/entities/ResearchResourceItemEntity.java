package org.orcid.persistence.jpa.entities;

import java.util.Set;

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

@Entity
@Table(name = "research_resource_item")
public class ResearchResourceItemEntity {

    private static final long serialVersionUID = 1L;
    private Long id;
    private Long researchResourceId;

    private String resourceName;
    private String resourceType;
    private Set<OrgEntity> hosts;
    private String externalIdentifiersJson;
    private String url;
    
    private ResearchResourceEntity researchResourceEntity;

    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="research_resource_id", nullable=false)
    public ResearchResourceEntity getResearchResourceEntity() {
        return researchResourceEntity;
    }

    public void setResearchResourceEntity(ResearchResourceEntity researchResourceEntity) {
        this.researchResourceEntity = researchResourceEntity;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "research_resource_item_seq")
    @SequenceGenerator(name = "research_resource_item_seq", sequenceName = "research_resource_item_seq")
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

}
