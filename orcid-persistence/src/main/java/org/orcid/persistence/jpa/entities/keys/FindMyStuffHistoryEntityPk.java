package org.orcid.persistence.jpa.entities.keys;

import java.io.Serializable;

import org.orcid.persistence.jpa.entities.ProfileEntity;

public class FindMyStuffHistoryEntityPk implements Serializable{

    private static final long serialVersionUID = 1L;
    private String orcid;
    private String finderName;
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((finderName == null) ? 0 : finderName.hashCode());
        result = prime * result + ((orcid == null) ? 0 : orcid.hashCode());
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
        FindMyStuffHistoryEntityPk other = (FindMyStuffHistoryEntityPk) obj;
        if (finderName == null) {
            if (other.finderName != null)
                return false;
        } else if (!finderName.equals(other.finderName))
            return false;
        if (orcid == null) {
            if (other.orcid != null)
                return false;
        } else if (!orcid.equals(other.orcid))
            return false;
        return true;
    }
    public FindMyStuffHistoryEntityPk() {
        
    }
    public FindMyStuffHistoryEntityPk(String orcid, String finderName) {
        super();
        this.setOrcid(orcid);
        this.setFinderName(finderName);
    }
    public String getFinderName() {
        return finderName;
    }
    public void setFinderName(String finderName) {
        this.finderName = finderName;
    }
    public String getOrcid() {
        return orcid;
    }
    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    
}
