package org.orcid.utils.listener;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public abstract class BaseMessage {
    /**
     * immutable map ready for transport
     * 
     */
    public final Map<String, String> map;

    /** Note this uses immutable maps for two reasons - 
     * one, immutablity
     * two, guava maps have symmetrical equals and hashcode implementations
     * 
     * @param map
     */
    protected BaseMessage(ImmutableMap<String, String> map) {
        this.map = map;
    }

    public String getOrcid() {
        return map.get(MessageConstants.ORCID.value);
    }

    /**
     * The map that is sent over the wire
     * 
     * @return
     */
    public Map<String, String> getMap() {
        return map;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((map == null) ? 0 : map.hashCode());
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
        BaseMessage other = (BaseMessage) obj;
        if (map == null) {
            if (other.map != null)
                return false;
        } else if (!map.equals(other.map))
            return false;
        return true;
    }
    
    
}
