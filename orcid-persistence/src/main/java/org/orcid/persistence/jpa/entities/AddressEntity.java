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
 * 
 * @author Angel Montenegro
 * 
 */
@Entity
@Table(name = "address")
public class AddressEntity extends SourceAwareEntity<Long> implements ProfileAware, DisplayIndexInterface {
    private static final long serialVersionUID = -331185018871126442L;
    private Long id;
    private String iso2Country;
    private String visibility;    
    private ProfileEntity user;
    private Long displayIndex;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "address_seq")
    @SequenceGenerator(name = "address_seq", sequenceName = "address_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "iso2_country", length = 2)
    public String getIso2Country() {
        return iso2Country;
    }

    public void setIso2Country(String iso2Country) {
        this.iso2Country = iso2Country;
    }

    @Column(name = "visibility")
    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public void setUser(ProfileEntity user) {
        this.user = user;
    }
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "orcid", nullable = false)
    public ProfileEntity getUser() {
        return user;
    }
    
    @Transient
    @Override
    public ProfileEntity getProfile() {
        return user;
    }

    @Column(name = "display_index")
    public Long getDisplayIndex() {
        return displayIndex;
    }

    public void setDisplayIndex(Long displayIndex) {
        this.displayIndex = displayIndex;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((iso2Country == null) ? 0 : iso2Country.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
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
        AddressEntity other = (AddressEntity) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (iso2Country != other.iso2Country)
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        if (visibility != other.visibility)
            return false;
        return true;
    }

}
