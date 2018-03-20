package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;

import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class OtherNameDaoImpl extends GenericDaoImpl<OtherNameEntity, Long> implements OtherNameDao {

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
        return getOtherNames(orcid, Visibility.PUBLIC);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<OtherNameEntity> getOtherNames(String orcid, org.orcid.jaxb.model.common_v2.Visibility visibility) {
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
}
