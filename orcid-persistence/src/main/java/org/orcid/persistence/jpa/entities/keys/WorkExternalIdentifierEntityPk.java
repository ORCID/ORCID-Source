package org.orcid.persistence.jpa.entities.keys;

import java.io.Serializable;

import javax.persistence.Embeddable;

/**
 * @author Declan Newman (declan) Date: 07/08/2012
 */
@Embeddable
public class WorkExternalIdentifierEntityPk implements Serializable {

    private static final long serialVersionUID = 1L;
    private String identifier;
    private String identifierType;
    private Long work;

    public WorkExternalIdentifierEntityPk() {
    }

    public WorkExternalIdentifierEntityPk(String identifier, String identifierType, Long work) {
        this.identifier = identifier;
        this.identifierType = identifierType;
        this.work = work;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public Long getWork() {
        return work;
    }

    public void setWork(Long work) {
        this.work = work;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof WorkExternalIdentifierEntityPk)) {
            return false;
        }

        WorkExternalIdentifierEntityPk that = (WorkExternalIdentifierEntityPk) o;

        if (!identifier.equals(that.identifier)) {
            return false;
        }
        if (!identifierType.equals(that.identifierType)) {
            return false;
        }
        if (!work.equals(that.work)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = identifier.hashCode();
        result = 31 * result + identifierType.hashCode();
        result = 31 * result + work.hashCode();
        return result;
    }
}
