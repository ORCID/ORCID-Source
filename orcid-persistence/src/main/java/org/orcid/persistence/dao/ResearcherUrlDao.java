package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;

import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;

public interface ResearcherUrlDao extends GenericDao<ResearcherUrlEntity, Long> {

    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid, long lastModified);
    
    public List<ResearcherUrlEntity> getPublicResearcherUrls(String orcid, long lastModified);

    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @param visibility
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid, String visibility);
    
    /**
     * Deleted a researcher url from database
     * @param orcid
     * @param id
     * @return true if the researcher url was successfully deleted
     * */
    public boolean deleteResearcherUrl(String orcid, long id);

    /**
     * Retrieve a researcher url from database
     * @param orcid
     * @param id
     * @return the ResearcherUrlEntity associated with the parameter id
     * */
    public ResearcherUrlEntity getResearcherUrl(String orcid, Long id);
    
    /**
     * Updates an existing researcher url
     * @param id
     * @param newUrl
     * @return true if the researcher url was updated
     * */
    public boolean updateResearcherUrl(long id, String newUrl);
    
    /**
     * Removes all researcher urls that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all researcher urls will be
     *            removed.
     */
    void removeAllResearcherUrls(String orcid);

    List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients);

    void correctClientSource(List<BigInteger> ids);

    List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients);

    void correctUserSource(List<BigInteger> ids);

    List<BigInteger> getIdsForUserOBOUpdate(String clientDetailsId, int max);

    void updateUserOBODetails(List<BigInteger> ids);

    List<BigInteger> getIdsForUserOBORecords(String clientDetailsId, int max);

    void revertUserOBODetails(List<BigInteger> ids);

    public List<BigInteger> getIdsForUserOBORecords(int max);

    List<BigInteger> getIdsOfResearcherUrlsReferencingClientProfiles(int max, List<String> clientProfileOrcidIds);

    boolean updateVisibility(String orcid, Visibility visibility);
}
