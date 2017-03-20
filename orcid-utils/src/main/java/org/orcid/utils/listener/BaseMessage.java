package org.orcid.utils.listener;

import java.util.Map;

public abstract class BaseMessage {
    /**
     * immutable map ready for transport
     * 
     */
    public final Map<String, String> map;
    
    protected BaseMessage(Map<String, String> map) {
        //TODO: Include the type to throw exception in case we get an invalid mesage type
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
}
