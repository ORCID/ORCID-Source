package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the name database table.
 * 
 */
@Entity
@Table(name = "record_name")
public class RecordNameEntity extends BaseEntity<Long> {
    private static final long serialVersionUID = -219497844494612167L;
    private Long id;
    private String creditName;
    private String givenNames;
    private String familyName;
    private String orcid;
    private String visibility;
    
    /**
     * @return the id of the name
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "record_name_seq")
    @SequenceGenerator(name = "record_name_seq", sequenceName = "record_name_seq")
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
     * @return the creditName
     */
    @Column(name = "credit_name")
    public String getCreditName() {
        return creditName;
    }

    /**
     * @param creditName the creditName to set
     */
    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }
    
    /**
     * @return the givenName
     */
    @Column(name = "given_names")
    public String getGivenNames() {
        return givenNames;
    }

    /**
     * @param givenName the givenName to set
     */
    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }
    
    /**
     * @return the familyName
     */
    @Column(name = "family_name")
    public String getFamilyName() {
        return familyName;
    }

    /**
     * @param familyName the familyName to set
     */
    public void setFamilyName(String familyName) {
        this.familyName = familyName;
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
    
    @Column
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }
}
