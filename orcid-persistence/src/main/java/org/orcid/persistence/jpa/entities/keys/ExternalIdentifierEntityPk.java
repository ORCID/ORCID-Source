package org.orcid.persistence.jpa.entities.keys;

import java.io.Serializable;

import org.orcid.persistence.jpa.entities.ProfileEntity;

public class ExternalIdentifierEntityPk implements Serializable {

    private static final long serialVersionUID = 1L;

    private String externalIdReference;

    private ProfileEntity owner;

    public ExternalIdentifierEntityPk() {

    }

    public ExternalIdentifierEntityPk(String externalIdReference, ProfileEntity owner) {
        super();
        this.externalIdReference = externalIdReference;
        this.owner = owner;
    }

    public String getExternalIdReference() {
        return externalIdReference;
    }

    public void setExternalIdReference(String externalIdReference) {
        this.externalIdReference = externalIdReference;
    }

    public ProfileEntity getOwner() {
        return owner;
    }

    public void setOwner(ProfileEntity owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExternalIdentifierEntityPk)) {
            return false;
        }

        ExternalIdentifierEntityPk that = (ExternalIdentifierEntityPk) o;

        if (!externalIdReference.equals(that.externalIdReference)) {
            return false;
        }
        if (!owner.equals(that.owner)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = externalIdReference.hashCode();
        result = 31 * result + owner.hashCode();
        return result;
    }
}
