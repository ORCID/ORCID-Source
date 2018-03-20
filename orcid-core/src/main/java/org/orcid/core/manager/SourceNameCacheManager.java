package org.orcid.core.manager;

/**
 * @author Angel Montenegro
 * */
public interface SourceNameCacheManager {

    public String retrieve(String sourceId) throws IllegalArgumentException;
    
    public void removeAll();
    
    public void remove(String sourceId);
    
}
