package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ExternalIdentifierDao extends GenericDao<ExternalIdentifierEntity, Long> {

    /**
     * Removes an external identifier from database based on his ID. The ID for
     * external identifiers consists of the "orcid" of the owner and the
     * "externalIdReference" which is an identifier of the external id.
     * 
     * @param orcid
     *            The orcid of the owner
     * @param externalIdReference
     *            Identifier of the external id.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean removeExternalIdentifier(String orcid, String externalIdReference);

    /**
     * Retrieves all external identifiers associated with the given profile
     * 
     * @param orcid
     * @return a list of all external identifiers associated with the given
     *         profile
     */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ExternalIdentifierEntity> getExternalIdentifiers(String orcid, long lastModified);

    /**
     * Retrieves all external identifiers associated with the given profile and
     * that have the given visibility
     * 
     * @param orcid
     * @param visibility
     * @return a list of all external identifiers associated with the given
     *         profile and that have the given visibility
     */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ExternalIdentifierEntity> getExternalIdentifiers(String orcid, String visibility);

    /**
     * Retrieve the external identifier that matches the give id and profile id
     * 
     * @param orcid
     * @param id
     * @return an external identifier that matches the given id and profile id
     */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    ExternalIdentifierEntity getExternalIdentifierEntity(String orcid, Long id);
    
    /**
     * Removes an external identifier from database based on his ID and the orcid.
     * 
     * @param orcid
     *            The orcid of the owner
     * @param externalIdReference
     *            Identifier of the external id.
     * @return true if an external identifier was deleted           
     */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean removeExternalIdentifier(String orcid, Long id);
    
    /**
     * Removes all external identifiers that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all external identifiers will be
     *            removed.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    void removeAllExternalIdentifiers(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<ExternalIdentifierEntity> getPublicExternalIdentifiers(String orcid, long lastModified);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVisibility(String orcid, Visibility visibility);
}
