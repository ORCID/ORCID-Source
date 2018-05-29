package org.orcid.core.manager;

import java.util.ArrayList;

import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;

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
     * */
    void removeResearchResource(String orcid, Long researchResourceId);

    /**
     * Updates the display index of a given peer review
     * 
     * @param orcid
     *            The researchResource owner
     * @param researchResourceId
     *            The researchResource id
     * @return true if it was able to update the display index
     * */
    boolean updateToMaxDisplay(String orcid, Long researchResourceId);

    /**
     * Updates the visibility of a list of existing peer review
     * 
     * @param researchResourceIds
     *            The ids of the researchResource that will be updated
     * @param visibility
     *            The new visibility value for the peer review
     * @return true if the relationship was updated
     * */
    public boolean updateVisibilities(String orcid, ArrayList<Long> researchResourceIds, Visibility visibility);
    
    /**
     * Removes all peer reviews that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all peer reviews will be
     *            removed.
     */
    void removeAllResearchResources(String orcid);
}
