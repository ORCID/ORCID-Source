package org.orcid.core.manager.v3;

import java.util.List;

import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkBulk;
import org.orcid.pojo.WorkGroupingSuggestion;

public interface WorkManager extends WorkManagerReadOnly {
    
    /**
     * Updates the visibility of an existing work
     * 
     * @param workId
     *            The id of the work that will be updated
     * @param visibility
     *            The new visibility value for the profile work relationship
     * @return true if the relationship was updated
     * */
    boolean updateVisibilities(String orcid, List<Long> workIds, Visibility visibility);
 
    /**
     * Removes a work.
     * 
     * @param workId
     *            The id of the work that will be removed from the client
     *            profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the work was deleted
     * */
    boolean removeWorks(String clientOrcid, List<Long> workIds);
    
    /**
     * Removes all works that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all works will be
     *            removed.
     */
    void removeAllWorks(String orcid);
    
    /**
     * Sets the display index of the new work
     * @param orcid     
     *          The work owner
     * @param workId
     *          The work id
     * @return true if the work index was correctly set                  
     * */
    boolean updateToMaxDisplay(String orcid, Long workId);        

    /**
     * Add a new work to the work table
     * 
     * @param work
     *            The work that will be persited
     * @param isApiRequest
     *          Does the request comes from the API?
     * @return the work already persisted on database
     * */
    Work createWork(String orcid, Work work, boolean isApiRequest);

    /**
     * Add a list of works to the given profile
     * 
     * @param works
     *            The list of works that want to be added
     * @param orcid
     *            The id of the user we want to add the works to
     * 
     * @return the work bulk with the put codes of the new works or the error
     *         that indicates why a work can't be added
     */
    WorkBulk createWorks(String orcid, WorkBulk work);
    
    /**
     * Edits an existing work
     * 
     * @param work
     *            The work to be edited
     * @param isApiRequest
     *          Does the request comes from the API? 
     * @return The updated entity
     * */
    Work updateWork(String orcid, Work work, boolean isApiRequest); 
    
    boolean checkSourceAndRemoveWork(String orcid, Long workId);

    /**
     * Groups the collection of works matching the specified work ID list
     * 
     * @param workIds
     */
    void createNewWorkGroup(List<Long> workIds, String orcid);    
    
    /**
     * Returns work grouping suggestions for a profile
     * @param orcid
     * @return List of WorkGroupingSuggestion objects
     */
    List<WorkGroupingSuggestion> getGroupingSuggestions(String orcid);
    
    /**
     * Groups the specified works and marks the grouping suggestion as accepted
     * @param groupingSuggestion
     */
    void acceptGroupingSuggestion(WorkGroupingSuggestion groupingSuggestion);
}
