package org.orcid.pojo;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class OrgDisambiguatedExternalIdentifiers implements Serializable {
    private static final long serialVersionUID = 6180119835799291621L;
    private String preferred;
    private String identifierType;
    private Set<String> all;

    public String getPreferred() {
        return preferred;
    }

    public void setPreferred(String preferred) {
        this.preferred = preferred;
    }

    public String getIdentifierType() {
        return identifierType;
    }

    public void setIdentifierType(String identifierType) {
        this.identifierType = identifierType;
    }

    public Set<String> getAll() {
        if(all == null) {
            all = new TreeSet<String>();
        }
        return all;
    }

    public void setAll(TreeSet<String> all) {
        this.all = all;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((all == null) ? 0 : all.hashCode());
        result = prime * result + ((identifierType == null) ? 0 : identifierType.hashCode());
        result = prime * result + ((preferred == null) ? 0 : preferred.hashCode());
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
        OrgDisambiguatedExternalIdentifiers other = (OrgDisambiguatedExternalIdentifiers) obj;
        if (all == null) {
            if (other.all != null)
                return false;
        } else if (!all.equals(other.all))
            return false;
        if (identifierType == null) {
            if (other.identifierType != null)
                return false;
        } else if (!identifierType.equals(other.identifierType))
            return false;
        if (preferred == null) {
            if (other.preferred != null)
                return false;
        } else if (!preferred.equals(other.preferred))
            return false;
        return true;
    }

}
