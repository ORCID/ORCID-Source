package org.orcid.persistence.jpa.entities;

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
import javax.persistence.Transient;

/**
 * orcid-entities - Dec 6, 2011 - ExternalIdentifierEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "external_identifier")
public class ExternalIdentifierEntity extends SourceAwareEntity<Long> implements Comparable<ExternalIdentifierEntity>, OrcidAware, DisplayIndexInterface {

    private static final long serialVersionUID = 1L;

    private String externalIdReference;
    private String externalIdCommonName;
    private String externalIdUrl;
    private String orcid;    
    private Long id;
    private String visibility;
    private Long displayIndex;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "external_identifier_id_seq")
    @SequenceGenerator(name = "external_identifier_id_seq", sequenceName = "external_identifier_id_seq", allocationSize = 1)
    @Override
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @Column(name = "external_id_reference", length = 255)
    public String getExternalIdReference() {
        return externalIdReference;
    }

    public void setExternalIdReference(String externalIdReference) {
        this.externalIdReference = externalIdReference;
    }

    @Column(name = "external_id_type", length = 255)
    public String getExternalIdCommonName() {
        return externalIdCommonName;
    }

    public void setExternalIdCommonName(String externalIdCommonName) {
        this.externalIdCommonName = externalIdCommonName;
    }

    /**
     * @return the owner
     */    
    @Column(name = "orcid", nullable = false)
    public String getOrcid() {
        return orcid;
    }

    /**
     * @param owner
     *            the owner to set
     */
    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Column(name = "external_id_url", length = 300)
    public String getExternalIdUrl() {
        return externalIdUrl;
    }

    public void setExternalIdUrl(String externalIdUrl) {
        this.externalIdUrl = externalIdUrl;
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
    public int compareTo(ExternalIdentifierEntity other) {
        if (other == null) {
            throw new NullPointerException("Can't compare with null");
        }

        int result = 0;

        //First check the source
        if(other.getElementSourceId() == null) {
            if(getElementSourceId() == null) {
                result = 0;
            } else {
                result = 1;
            }
        } else {
            if(getElementSourceId() == null) {
                result = -1;
            } else {
                result = getElementSourceId().compareToIgnoreCase(other.getElementSourceId());
            }
        }
        
        if (result == 0) {
            // If they are still equal, compare against the externalIdUrl
            if (other.getExternalIdUrl() == null) {
                if (externalIdUrl == null) {
                    result = 0;
                } else {
                    result = 1;
                }
            } else {
                if (externalIdUrl == null) {
                    result = -1;
                } else {
                    result = externalIdUrl.compareToIgnoreCase(other.getExternalIdUrl());
                }
            }
        }

        // If they are still equal, compare against the externalIdReference
        if (result == 0) {
            if (other.getExternalIdReference() == null) {
                if (externalIdReference == null) {
                    result = 0;
                } else {
                    result = 1;
                }
            } else {
                if (externalIdReference == null) {
                    result = -1;
                } else {
                    result = externalIdReference.compareToIgnoreCase(other.getExternalIdReference());
                }
            }
        }

        // If they are still equal, compare against the getExternalIdCommonName
        if (result == 0) {
            if(other.getExternalIdCommonName() == null) {
                if(externalIdCommonName == null) {
                    result = 0;
                } else {
                    result = 1;
                }
            } else {
                if(externalIdCommonName == null) {
                    result = -1;                   
                } else {
                    result = externalIdCommonName.compareToIgnoreCase(other.getExternalIdCommonName());
                }                   
            }
        }
        return result;
    }

    /**
     * Clean simple properties to allow entity to be reused.
     */
    public void clean() {
        externalIdCommonName = null;
        externalIdUrl = null;
        externalIdReference = null;
    }

}
