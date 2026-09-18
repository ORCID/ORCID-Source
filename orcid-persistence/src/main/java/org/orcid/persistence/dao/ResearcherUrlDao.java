package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.List;

import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ResearcherUrlDao extends GenericDao<ResearcherUrlEntity, Long> {

    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid, long lastModified);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    public List<ResearcherUrlEntity> getPublicResearcherUrls(String orcid, long lastModified);

    /**
     * Return the list of researcher urls associated to a specific profile
     * @param orcid
     * @param visibility
     * @return 
     *          the list of researcher urls associated with the orcid profile
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    public List<ResearcherUrlEntity> getResearcherUrls(String orcid, String visibility);
    
    /**
     * Deleted a researcher url from database
     * @param orcid
     * @param id
     * @return true if the researcher url was successfully deleted
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean deleteResearcherUrl(String orcid, long id);

    /**
     * Retrieve a researcher url from database
     * @param orcid
     * @param id
     * @return the ResearcherUrlEntity associated with the parameter id
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    public ResearcherUrlEntity getResearcherUrl(String orcid, Long id);
    
    /**
     * Updates an existing researcher url
     * @param id
     * @param newUrl
     * @return true if the researcher url was updated
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean updateResearcherUrl(long id, String newUrl);
    
    /**
     * Removes all researcher urls that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all researcher urls will be
     *            removed.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    void removeAllResearcherUrls(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients);

    @Transactional(propagation = Propagation.REQUIRED)
    void correctClientSource(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients);

    @Transactional(propagation = Propagation.REQUIRED)
    void correctUserSource(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserOBOUpdate(String clientDetailsId, int max);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateUserOBODetails(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsForUserOBORecords(String clientDetailsId, int max);

    @Transactional(propagation = Propagation.REQUIRED)
    void revertUserOBODetails(List<BigInteger> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    public List<BigInteger> getIdsForUserOBORecords(int max);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getIdsOfResearcherUrlsReferencingClientProfiles(int max, List<String> clientProfileOrcidIds);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVisibility(String orcid, Visibility visibility);
}
