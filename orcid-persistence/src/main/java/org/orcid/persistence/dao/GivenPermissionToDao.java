package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface GivenPermissionToDao extends GenericDao<GivenPermissionToEntity, Long> {

    List<GivenPermissionToEntity> findByGiver(String giverOrcid);
    
    List<GivenPermissionByEntity> findByReceiver(String receiverOrcid);
    
    GivenPermissionToEntity findByGiverAndReceiverOrcid(String giverOrcid, String receiverOrcid);

    void remove(String giverOrcid, String receiverOrcid);

}
