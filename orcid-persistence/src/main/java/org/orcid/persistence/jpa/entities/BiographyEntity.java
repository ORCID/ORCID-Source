package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

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
    @SequenceGenerator(name = "biography_seq", sequenceName = "biography_seq")
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
