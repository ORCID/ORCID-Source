package org.orcid.persistence.jpa.entities.keys;

import java.io.Serializable;

import org.orcid.persistence.jpa.entities.OrcidSocialType;

public class OrcidSocialPk implements Serializable {
    private static final long serialVersionUID = 1L;
    private String orcid;
    private OrcidSocialType type;

    public OrcidSocialPk() {

    }

    public OrcidSocialPk(String orcid, OrcidSocialType type) {
        super();
        this.orcid = orcid;
        this.type = type;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public OrcidSocialType getType() {
        return type;
    }

    public void setType(OrcidSocialType type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        OrcidSocialPk other = (OrcidSocialPk) obj;
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
            return false;
        if (type != other.type)
            return false;
        return true;
    }
}
