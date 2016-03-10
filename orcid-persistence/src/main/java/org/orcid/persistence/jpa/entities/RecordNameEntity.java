package org.orcid.persistence.jpa.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.jaxb.model.message.Visibility;

/**
 * The persistent class for the name database table.
 * 
 */
@Entity
@Table(name = "record_name")
public class RecordNameEntity extends BaseEntity<Long> implements ProfileAware {
    private static final long serialVersionUID = -219497844494612167L;
    private Long id;
    private String creditName;
    private String givenName;
    private String familyName;
    private ProfileEntity profile;
    private Visibility visibility;
    
    /**
     * @return the id of the name
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "name_seq")
    @SequenceGenerator(name = "name_seq", sequenceName = "name_seq")
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
    @Column(name = "given_name")
    public String getGivenName() {
        return givenName;
    }

    /**
     * @param givenName the givenName to set
     */
    public void setGivenName(String givenName) {
        this.givenName = givenName;
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
     * @return the profile
     */
    @OneToOne(optional = false, mappedBy = "recordNameEntity")
    public ProfileEntity getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }
    
    @Basic
    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
}
