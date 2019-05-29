package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.Query;

import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class OtherNameDaoImpl extends GenericDaoImpl<OtherNameEntity, Long> implements OtherNameDao {

    private static final String PUBLIC_VISIBILITY = "PUBLIC";

    public OtherNameDaoImpl() {
        super(OtherNameEntity.class);
    }

    /**
     * Get other names for an specific orcid account
     * @param orcid          
     * @return
     *           The list of other names related with the specified orcid profile
     * */
    @Override
    @SuppressWarnings("unchecked")
    @Cacheable(value = "dao-other-names", key = "#orcid.concat('-').concat(#lastModified)")
    public List<OtherNameEntity> getOtherNames(String orcid, long lastModified) {
        Query query = entityManager.createQuery("FROM OtherNameEntity WHERE profile.id=:orcid order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        return query.getResultList();
    }
    
    @Override
    @Cacheable(value = "public-other-names", key = "#orcid.concat('-').concat(#lastModified)")
    public List<OtherNameEntity> getPublicOtherNames(String orcid, long lastModified) {
        return getOtherNames(orcid, PUBLIC_VISIBILITY);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OtherNameEntity> getOtherNames(String orcid, String visibility) {
        Query query = entityManager.createQuery("FROM OtherNameEntity WHERE profile.id=:orcid AND visibility=:visibility order by displayIndex desc, dateCreated asc");
        query.setParameter("orcid", orcid);
        query.setParameter("visibility", visibility);
        return query.getResultList();
    }
    
    /**
     * Update other name entity with new values
     * @param otherName
     * @return
     *          true if the other name was sucessfully updated, false otherwise
     * */
    @Override
    @Transactional
    public boolean updateOtherName(OtherNameEntity otherName) {
        throw new UnsupportedOperationException("This opperation is not supported yet");
    }

    /**
     * Create other name for the specified account
     * @param orcid
     * @param displayName
     * @return
     *          true if the other name was successfully created, false otherwise 
     * */
    @Override
    @Transactional
    public boolean addOtherName(String orcid, String displayName) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO other_name (other_name_id, date_created, last_modified, display_name, orcid) VALUES (nextval('other_name_seq'), now(), now(), :displayName, :orcid)");
        query.setParameter("orcid", orcid);
        query.setParameter("displayName", displayName);
        return query.executeUpdate() > 0 ? true : false;
    }

    /**
     * Delete other name from database
     * @param otherName
     * @return 
     *          true if the other name was successfully deleted, false otherwise
     * */
    @Override
    @Transactional
    public boolean deleteOtherName(OtherNameEntity otherName) {
        Assert.notNull(otherName);
        Query query = entityManager.createQuery("DELETE FROM OtherNameEntity WHERE id=:id");
        query.setParameter("id", otherName.getId());
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    public OtherNameEntity getOtherName(String orcid, Long putCode) {
        Query query = entityManager.createQuery("FROM OtherNameEntity WHERE profile.id=:orcid and id=:id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", putCode);
        return (OtherNameEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    public void removeAllOtherNames(String orcid) {
        Query query = entityManager.createQuery("delete from OtherNameEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForClientSourceCorrection(int limit, List<String> nonPublicClients) {
        Query query = entityManager.createNativeQuery("SELECT other_name_id FROM other_name WHERE client_source_id = source_id AND client_source_id IN :nonPublicClients");
        query.setParameter("nonPublicClients", nonPublicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctClientSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE other_name SET client_source_id = source_id, source_id = NULL where other_name_id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserSourceCorrection(int limit, List<String> publicClients) {
        Query query = entityManager.createNativeQuery("SELECT other_name_id FROM other_name WHERE client_source_id = source_id AND client_source_id IN :publicClients");
        query.setParameter("publicClients", publicClients);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void correctUserSource(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE other_name SET source_id = client_source_id, client_source_id = NULL where other_name_id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigInteger> getIdsForUserOBOUpdate(String clientDetailsId, int max) {
        Query query = entityManager.createNativeQuery("SELECT other_name_id FROM other_name WHERE client_source_id = :clientDetailsId AND assertion_origin_source_id IS NULL");
        query.setParameter("clientDetailsId", clientDetailsId);
        query.setMaxResults(max);
        return query.getResultList();
    }

    @Override
    @Transactional
    public void updateUserOBODetails(List<BigInteger> ids) {
        Query query = entityManager.createNativeQuery("UPDATE other_name SET assertion_origin_source_id = orcid where other_name_id IN :ids");
        query.setParameter("ids", ids);
        query.executeUpdate();
    }
}
