package org.orcid.persistence.jpa.entities;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/** Represents a research resource.
 * 
 * @author tom
 *
 */
@Entity
@Table(name = "research_resource")
public class ResearchResourceEntity extends SourceAwareEntity<Long> implements Comparable<ResearchResourceEntity>, ProfileAware, DisplayIndexInterface {
    
    private static final long serialVersionUID = 1L;
    private Long id;
    private ProfileEntity profile;
    private StartDateEntity startDate;
    private EndDateEntity endDate;
    private String visibility;    

    private String proposalType = "proposal"; //currently only "proposal" is supported.
    private String url;
    private String externalIdentifiersJson;
    private String title;
    private String translatedTitle;
    private String translatedTitleLanguageCode;
    private Long displayIndex; 

    private List<OrgEntity> hosts;
    private List<ResearchResourceItemEntity> resourceItems;
    
    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "research_resource_seq")
    @SequenceGenerator(name = "research_resource_seq", sequenceName = "research_resource_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    @ManyToOne(cascade = { CascadeType.REFRESH }, fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false)
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }
    
    @Column(name = "title", nullable = false)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "translated_title")
    public String getTranslatedTitle() {
        return translatedTitle;
    }

    public void setTranslatedTitle(String translatedTitle) {
        this.translatedTitle = translatedTitle;
    }

    @Column(name = "translated_title_language_code", length = 25)
    public String getTranslatedTitleLanguageCode() {
        return translatedTitleLanguageCode;
    }

    public void setTranslatedTitleLanguageCode(String translatedTitleLanguageCode) {
        this.translatedTitleLanguageCode = translatedTitleLanguageCode;
    }
    
    @Column(name = "proposal_type")
    public String getProposalType() {
        return proposalType;
    }

    public void setProposalType(String proposalType) {
        this.proposalType = proposalType;
    }
    
    @Column(name = "url")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
    
    @Column(name = "display_index", updatable=false)
    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }
    
    @Column(name = "external_identifiers_json")
    public String getExternalIdentifiersJson() {
        return externalIdentifiersJson;
    }

    public void setExternalIdentifiersJson(String externalIdentifiersJson) {
        this.externalIdentifiersJson = externalIdentifiersJson;
    }
    
    @OneToMany(mappedBy="researchResourceEntity", cascade = CascadeType.ALL,fetch=FetchType.LAZY )
    public List<ResearchResourceItemEntity> getResourceItems() {
        return resourceItems;
    }

    public void setResourceItems(List<ResearchResourceItemEntity> resourceItems) {
        this.resourceItems = resourceItems;
    }
    
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "research_resource_org", 
               joinColumns = { @JoinColumn(name = "research_resource_id", referencedColumnName="id") }, 
               inverseJoinColumns = { @JoinColumn(name = "org_id", referencedColumnName="id") })
    public List<OrgEntity> getHosts() {
        return hosts;
    }

    public void setHosts(List<OrgEntity> hosts) {
        this.hosts = hosts;
    }
    
    ////

    @Override
    public int compareTo(ResearchResourceEntity o) {
        // TODO Auto-generated method stub
        return 0;
    }

}
