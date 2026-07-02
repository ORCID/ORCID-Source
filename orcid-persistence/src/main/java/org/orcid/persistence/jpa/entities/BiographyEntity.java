package org.orcid.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

/**
 * The persistent class for the name database table.
 * 
 */
@Entity
@Table(name = "biography")
public class BiographyEntity extends BaseEntity<Long> implements OrcidAware {
    /**
     * 
     */
    private static final long serialVersionUID = -7348260374645942620L;
    private Long id;
    private String biography;   
    private String visibility;
    private String orcid;
    
    /**
     * @return the id of the name
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "biography_seq")
    @SequenceGenerator(name = "biography_seq", sequenceName = "biography_seq", allocationSize = 1)
    public Long getId() {
        return id;
    }

    /**
     * @param id the id of the name
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the orcid
     */
    @Column(name = "orcid")
    public String getOrcid() {
        return orcid;
    }

    /**
     * @param orcid the orcid to set
     */
    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    
    @Column(name = "biography")    
    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    @Column
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
