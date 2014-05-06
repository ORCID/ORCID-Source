package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.orcid.persistence.dao.FundingSubTypeToIndexDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class FundingSubTypeToIndexDaoImpl implements FundingSubTypeToIndexDao {
    
    private static final Logger LOG = LoggerFactory.getLogger(FundingSubTypeToIndexDaoImpl.class);
    
    @PersistenceContext(unitName = "orcid")
    protected EntityManager entityManager;
    
    @Transactional
    public void addSubTypes(String subtype, String orcid) {
        if(!exists(subtype)) {
            Query query = entityManager.createNativeQuery("INSERT INTO funding_subtype_to_index(orcid, subtype) values(:orcid, :subtype)");
            query.setParameter("orcid", orcid);
            query.setParameter("subtype", subtype.trim());
            query.executeUpdate();
        } else {
            LOG.debug("Subtype %s already exists", subtype);
        }
    }
    
    @Transactional
    public void removeSubTypes(String subtype) {
        Query query = entityManager.createNativeQuery("DELETE FROM funding_subtype_to_index WHERE subtype=:subtype");
        query.setParameter("subtype", subtype.trim());
        query.executeUpdate();
    }
    
    @Transactional
    public void removeSubTypes(String subtype, String orcid) {
        Query query = entityManager.createNativeQuery("DELETE FROM funding_subtype_to_index WHERE subtype=:subtype and orcid=:orcid");
        query.setParameter("subtype", subtype.trim());
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }
    
    private boolean exists(String subtype) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM funding_subtype_to_index WHERE subtype=:subtype");
        query.setParameter("subtype", subtype.trim());
        BigInteger result = (BigInteger)query.getSingleResult();
        return result.longValue() > 0;
    } 
    
    @SuppressWarnings("unchecked")
    public List<String> getSubTypes() {
        Query query = entityManager
                .createNativeQuery("SELECT subtype FROM funding_subtype_to_index");
        return query.getResultList(); 
    }
}
