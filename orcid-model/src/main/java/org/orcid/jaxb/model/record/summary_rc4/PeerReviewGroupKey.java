package org.orcid.jaxb.model.record.summary_rc4;

import org.apache.commons.lang3.StringUtils;
import org.orcid.jaxb.model.record_rc4.GroupAble;

public class PeerReviewGroupKey implements GroupAble {    
    public static String KEY_NAME = "peer-review"; 
    protected String groupId;
    
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    } 
    
    @Override
    public boolean isGroupAble() {
        if(StringUtils.isBlank(groupId))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
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
        PeerReviewGroupKey other = (PeerReviewGroupKey) obj;
        if (groupId == null) {
            if (other.groupId != null)
                return false;
        } else if (!groupId.equals(other.groupId))
            return false;
        return true;

    }        
}
