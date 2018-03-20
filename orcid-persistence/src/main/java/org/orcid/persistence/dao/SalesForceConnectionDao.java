package org.orcid.persistence.dao;

import java.util.List;

import org.orcid.persistence.jpa.entities.SalesForceConnectionEntity;

/**
 * @author Will Simpson
 */
public interface SalesForceConnectionDao extends GenericDao<SalesForceConnectionEntity, Long> {

    SalesForceConnectionEntity findByOrcidAndAccountId(String orcid, String accountId);

    List<SalesForceConnectionEntity> findByOrcid(String orcid);

    List<SalesForceConnectionEntity> findByAccountId(String accountId);

}
