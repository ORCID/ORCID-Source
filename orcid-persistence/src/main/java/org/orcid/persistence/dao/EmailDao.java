package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.EmailEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface EmailDao extends GenericDao<EmailEntity, String> {

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean emailExists(String emailHash);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    EmailEntity findByEmail(String email);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    String findOrcidIdByEmailHash(String email);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    String findOrcidByVerifiedEmail(String email);

    @Transactional(propagation = Propagation.REQUIRED)
    void updatePrimary(String orcid, String primaryEmail);

    @Transactional(propagation = Propagation.REQUIRED)
    void addEmail(String orcid, String email, String emailHash, String visibility, String sourceId, String clientSourceId);      

    @Transactional(propagation = Propagation.REQUIRED)
    void removeEmail(String orcid, String email);
    
    @SuppressWarnings("rawtypes")
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List findIdByCaseInsensitiveEmail(List<String> emails);
    
    @Transactional(propagation = Propagation.REQUIRED)
    void addSourceToEmail(String sourceId, String email);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean verifyEmail(String email);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean isVerified(String orcid, String email);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean moveEmailToOtherAccountAsNonPrimary(String email, String origin, String destination);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<EmailEntity> findByOrcid(String orcid, long lastModified);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<EmailEntity> findByOrcid(String orcid, String visibility);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVerifySetCurrentAndPrimary(String orcid, String email);
    
    /***
     * Indicates if the given email address could be auto deprecated given the
     * ORCID rules. See
     * https://trello.com/c/ouHyr0mp/3144-implement-new-auto-deprecate-workflow-
     * for-members-unclaimed-ids
     * 
     * @param email
     *            Email address
     * @return true if the email exists, the owner is not claimed and the
     *         client source of the record allows auto deprecating records
     */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean isAutoDeprecateEnableForEmailUsingHash(String emailHash);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean isPrimaryEmail(String email);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    boolean isPrimaryEmail(String orcid, String email);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    EmailEntity findPrimaryEmail(String orcid);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean hideAllEmails(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<EmailEntity> findPublicEmails(String orcid, long lastModified);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVisibility(String orcid, String email, String visibility);   
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<String> getEmailsToHash(Integer batchSize); 
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean populateEmailHash(String email, String emailHash); 
    
    @Transactional(propagation = Propagation.REQUIRED)
    Integer clearEmailsAfterReactivation(String orcid);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List getEmailAndHash(int iteration, int batchSize);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<String> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients);

    @Transactional(propagation = Propagation.REQUIRED)
    void correctClientSource(List<String> ids);
    
    /**
     * Gets list of email entities to which quarterly emails should be sent.
     * 
     * @return
     */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<EmailEntity> getMarch2019QuarterlyEmailRecipients(int offset, int batchSize);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<String> getIdsForUserSourceCorrection(int limit, List<String> publicClients);

    @Transactional(propagation = Propagation.REQUIRED)
    void correctUserSource(List<String> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<EmailEntity> findPublicEmailsIncludeUnverified(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<String> getIdsForUserOBOUpdate(String clientDetailsId, int max);

    @Transactional(propagation = Propagation.REQUIRED)
    void updateUserOBODetails(List<String> ids);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<EmailEntity> get2019VisibilityEmailRecipients(int offset, int batchSize);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<String> getIdsForUserOBORecords(String clientDetailsId, int max);

    @Transactional(propagation = Propagation.REQUIRED)
    void revertUserOBODetails(List<String> ids);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    String findNewestVerifiedOrNewestEmail(String orcid);
    
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    String findNewestPrimaryEmail(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<String> getIdsForUserOBORecords(int max);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<String> getIdsOfEmailsReferencingClientProfiles(int max, List<String> ids);
}
