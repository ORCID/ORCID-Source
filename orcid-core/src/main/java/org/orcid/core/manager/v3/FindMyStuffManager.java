package org.orcid.core.manager.v3;

import java.util.List;
import java.util.Map;

import org.orcid.persistence.jpa.entities.FindMyStuffHistoryEntity;
import org.orcid.pojo.FindMyStuffResult;

public interface FindMyStuffManager {

    /** Invokes all finders for a given ORCID
     * 
     * @param orcid
     * @return a map of serviceName->result for all finders that found something
     */
    Map<String, FindMyStuffResult> find(String orcid);

    /** For each service, invoke finder if:
     * 
     * 1. user does not have existing permissions with SP
     * 2. user has not opted out of find my stuff
     * 3. we've not attempted to find in the last week for that user
     * 
     * For each result, create a notification and return the details.
     * 
     * @param orcid
     * @return a map of serviceName->result for all finders that found something
     */
    List<FindMyStuffResult> findIfAppropriate(String orcid);

    /** Checks to see if user has opted out of find my stuff, when the find was run, and other historical details.
     *  
     * @param orcid
     * @return one entry per SP (if present)
     */
    List<FindMyStuffHistoryEntity> getHistory(String orcid);

    /** Marks a finder as actioned in the DB (used for reporting)
     * 
     * @param orcid
     * @param finderName
     */
    void markAsActioned(String orcid, String finderName);
    
    String buildAuthorizationUrl(String clientId);

    void markOptOut(String orcid, String finderName, boolean state);

}