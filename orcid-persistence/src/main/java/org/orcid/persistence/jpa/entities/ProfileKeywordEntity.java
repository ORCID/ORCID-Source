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

import org.orcid.jaxb.model.common_v2.Visibility;

/**
 * orcid-entities - Dec 6, 2011 - ProfileInstitutionEntity
 * 
 * @author Declan Newman (declan)
 */
@Entity
@Table(name = "profile_keyword")
public class ProfileKeywordEntity extends SourceAwareEntity<Long> implements Comparable<ProfileKeywordEntity>, ProfileAware, DisplayIndexInterface {

    private static final long serialVersionUID = -3187757614938904392L;

    private Long id;
    private ProfileEntity profile;
    private String keywordName;
    private Visibility visibility;    
    private Long displayIndex;

    /**
     * @return the id of the other_name
     */
    @Id    
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "keyword_seq")
    @SequenceGenerator(name = "keyword_seq", sequenceName = "keyword_seq")
    public Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id of the other_name
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the profile
     */    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_orcid", nullable = false)
    public ProfileEntity getProfile() {
        return profile;
    }

    /**
     * @param profile
     *            the profile to set
     */
    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    /**
     * @return the institutionEntity
     */
    @Column(name = "keywords_name", length = 255)
    public String getKeywordName() {
        return keywordName;
    }

    /**
     * @param keywordName
     *            the institutionEntity to set
     */
    public void setKeywordName(String keywordName) {
        this.keywordName = keywordName;
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
    
    //TODO: this should include source.  To meet the Set contract it should include everything that equals does.
    @Override
    public int compareTo(ProfileKeywordEntity profileKeywordEntity) {
        if (keywordName != null && profileKeywordEntity != null) {
            return keywordName.compareTo(profileKeywordEntity.getKeywordName());
        } else {
            return 0;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((displayIndex == null) ? 0 : displayIndex.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((keywordName == null) ? 0 : keywordName.hashCode());
        result = prime * result + ((profile == null) ? 0 : profile.hashCode());
        result = prime * result + ((sourceId == null) ? 0 : sourceId.hashCode());
        result = prime * result + ((clientSourceId == null) ? 0 : clientSourceId.hashCode());
        result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProfileKeywordEntity other = (ProfileKeywordEntity) obj;
        if (displayIndex == null) {
            if (other.displayIndex != null)
                return false;
        } else if (!displayIndex.equals(other.displayIndex))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (keywordName == null) {
            if (other.keywordName != null)
                return false;
        } else if (!keywordName.equals(other.keywordName))
            return false;
        if (profile == null) {
            if (other.profile != null)
                return false;
        } else if (!profile.equals(other.profile))
            return false;
        if (visibility != other.visibility)
            return false;
        return true;
    }   
}
