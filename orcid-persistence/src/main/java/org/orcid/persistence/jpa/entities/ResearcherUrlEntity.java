package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.persistence.constants.SiteConstants;

/**
 * orcid-entities - Dec 6, 2011 - ElectronicResourceNumTypeEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "researcher_url")
public class ResearcherUrlEntity extends SourceAwareEntity<Long> implements Comparable<ResearcherUrlEntity>, OrcidAware, DisplayIndexInterface {

    private static final long serialVersionUID = -632507196189018770L;

    private Long id;
    private String url;
    private String urlName;
    private String orcid;    
    private String visibility;
    private Long displayIndex;

    public ResearcherUrlEntity() {
    }

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "researcher_url_seq")
    @SequenceGenerator(name = "researcher_url_seq", sequenceName = "researcher_url_seq", allocationSize = 1)
    @Column(name = "id")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "url", length = SiteConstants.URL_MAX_LENGTH)
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Column(name = "url_name", length = SiteConstants.URL_MAX_LENGTH)
    public String getUrlName() {
        return urlName;
    }

    public void setUrlName(String urlName) {
        this.urlName = urlName;
    }

    @Column(name = "orcid", nullable = false, updatable = false)
    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    
    @Column
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    @Column(name = "display_index")
    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }
    
    @Override
    public int compareTo(ResearcherUrlEntity other) {
        String otherUrl = other.getUrl();
        if (url == null) {
            return otherUrl == null ? 0 : -1;
        } else {
            if (url.compareTo(otherUrl) != 0)
                return url.compareTo(otherUrl);
            else {
                return otherUrl == null ? 1 : url.compareTo(otherUrl);
            }
        }
    }

}
