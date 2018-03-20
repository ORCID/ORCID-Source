package org.orcid.persistence.jpa.entities.keys;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * @author Declan Newman (declan) Date: 13/02/2012
 */
@Embeddable
public class OrcidGrantedAuthorityPk implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String profileEntity;
    private String authority;

    public OrcidGrantedAuthorityPk() {
    }

    public OrcidGrantedAuthorityPk(String profileEntity, String authority) {
        this.profileEntity = profileEntity;
        this.authority = authority;
    }

    public String getProfileEntity() {
        return profileEntity;
    }

    public void setProfileEntity(String profileEntity) {
        this.profileEntity = profileEntity;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        OrcidGrantedAuthorityPk that = (OrcidGrantedAuthorityPk) o;

        if (!authority.equals(that.authority))
            return false;
        if (!profileEntity.equals(that.profileEntity))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = profileEntity.hashCode();
        result = 31 * result + authority.hashCode();
        return result;
    }
}
