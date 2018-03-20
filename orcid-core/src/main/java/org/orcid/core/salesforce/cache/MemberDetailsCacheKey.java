package org.orcid.core.salesforce.cache;

import java.io.Serializable;

/**
 * 
 * @author Will Simpson
 *
 */
public class MemberDetailsCacheKey implements Serializable {

    private static final long serialVersionUID = 1L;

    private String memberId;
    private String consortiumLeadId;
    private String releaseName;

    public MemberDetailsCacheKey(String memberId, String consortiumLeadId, String releaseName) {
        this.memberId = memberId;
        this.consortiumLeadId = consortiumLeadId;
        this.releaseName = releaseName;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getConsotiumLeadId() {
        return consortiumLeadId;
    }

    public String getReleaseName() {
        return releaseName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((consortiumLeadId == null) ? 0 : consortiumLeadId.hashCode());
        result = prime * result + ((memberId == null) ? 0 : memberId.hashCode());
        result = prime * result + ((releaseName == null) ? 0 : releaseName.hashCode());
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
        MemberDetailsCacheKey other = (MemberDetailsCacheKey) obj;
        if (consortiumLeadId == null) {
            if (other.consortiumLeadId != null)
                return false;
        } else if (!consortiumLeadId.equals(other.consortiumLeadId))
            return false;
        if (memberId == null) {
            if (other.memberId != null)
                return false;
        } else if (!memberId.equals(other.memberId))
            return false;
        if (releaseName == null) {
            if (other.releaseName != null)
                return false;
        } else if (!releaseName.equals(other.releaseName))
            return false;
        return true;
    }

}
