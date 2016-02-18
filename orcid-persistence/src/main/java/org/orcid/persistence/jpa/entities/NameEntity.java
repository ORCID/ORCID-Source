package org.orcid.persistence.jpa.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.orcid.jaxb.model.common_rc2.Visibility;

/**
 * The persistent class for the name database table.
 * 
 */
@Entity
@Table(name = "name")
public class NameEntity extends BaseEntity<Long> implements Comparable<NameEntity>, ProfileAware, SourceAware {

    /**
     * 
     */
    private static final long serialVersionUID = -219497844494612167L;
    private Long id;
    private String creditName;
    private String givenName;
    private String familyName;
    private ProfileEntity profile;
    private SourceEntity source;
    private Visibility visibility;
    private Long displayIndex;

    /**
     * @return the id of the name
     */
    @Id
    @Column(name = "name_id")
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
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "orcid", nullable = false, updatable = false)
    public ProfileEntity getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     */
    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }
    
    public SourceEntity getSource() {
        return source;
    }

    public void setSource(SourceEntity source) {
        this.source = source;
    }

    @Basic
    @Enumerated(EnumType.STRING)
    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
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
    public int compareTo(NameEntity nameEntity) {
        if (creditName != null && nameEntity != null) {
            return creditName.compareTo(nameEntity.getCreditName());
        } else {
            return 0;
        }
    }
}
