package org.orcid.core.manager.v3;

import java.util.ArrayList;
import java.util.List;

import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;

public interface ResearchResourceManager extends ResearchResourceManagerReadOnly{

    ResearchResource createResearchResource(String orcid, ResearchResource rr, boolean isApiRequest);
    ResearchResource updateResearchResource(String orcid, ResearchResource rr, boolean isApiRequest);
    
    /**
     * Deletes a given researchResource if and only if it belongs to the given user.
     * If the researchResource exists but it doesn't belong to this user, it will not
     * delete it
     * 
     * @param orcid
     *            the researchResource owner
     * @param researchResourceId
     *            The researchResource id
     * @return 
     * */
    boolean checkSourceAndRemoveResearchResource(String orcid, Long researchResourceId);

    /**
     * Updates the display index of a given research resource
     * 
     * @param orcid
     *            The researchResource owner
     * @param researchResourceId
     *            The researchResource id
     * @return true if it was able to update the display index
     * */
    boolean updateToMaxDisplay(String orcid, Long researchResourceId);

    /**
     * Updates the visibility of a list of existing research resource
     * 
     * @param researchResourceIds
     *            The ids of the researchResource that will be updated
     * @param visibility
     *            The new visibility value for the research resource
     * @return true if the relationship was updated
     * */
    public boolean updateVisibilities(String orcid, ArrayList<Long> researchResourceIds, Visibility visibility);
    
    /**
     * Removes all research resources that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all research resources will be
     *            removed.
     */
    void removeAllResearchResources(String orcid);
    
    /** Remove a selection of rrs.
     * 
     * @param effectiveUserOrcid
     * @param rrIds
     */
    void removeResearchResources(String effectiveUserOrcid, ArrayList<Long> rrIds);
    
}
