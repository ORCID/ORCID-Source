package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.orcid.persistence.dao.OrcidSocialDao;
import org.orcid.persistence.jpa.entities.OrcidSocialEntity;
import org.orcid.persistence.jpa.entities.OrcidSocialType;
import org.orcid.persistence.jpa.entities.keys.OrcidSocialPk;
import org.springframework.transaction.annotation.Transactional;

public class OrcidSocialDaoImpl extends GenericDaoImpl<OrcidSocialEntity, OrcidSocialPk> implements OrcidSocialDao {

    public OrcidSocialDaoImpl() {
        super(OrcidSocialEntity.class);
    }

    @Override
    @Transactional
    public void save(String orcid, OrcidSocialType type, String encryptedCredentials) {
        Query query = entityManager
                .createNativeQuery("INSERT INTO orcid_social(orcid, type, encrypted_credentials, date_created, last_modified) values(:orcid,:type,:credentials,now(),now())");
        query.setParameter("orcid", orcid);
        query.setParameter("type", type.name());
        query.setParameter("credentials", encryptedCredentials);
        query.executeUpdate();
    }

    @Override
    @Transactional
    public void delete(String orcid, OrcidSocialType type) {
        Query query = entityManager.createNativeQuery("DELETE FROM orcid_social WHERE orcid=:orcid AND type=:type");
        query.setParameter("orcid", orcid);
        query.setParameter("type", type.name());
        query.executeUpdate();
    }

    @Override
    public boolean isEnabled(String orcid, OrcidSocialType type) {
        Query query = entityManager.createNativeQuery("SELECT * FROM orcid_social WHERE orcid=:orcid AND type=:type");
        query.setParameter("orcid", orcid);
        query.setParameter("type", type.name());
        try {
            query.getSingleResult();
            return true;
        } catch (NoResultException nre) {
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateLatestRunDate(String orcid, OrcidSocialType type) {
        Query query = entityManager.createNativeQuery("UPDATE orcid_social SET last_run = now() WHERE orcid=:orcid AND type=:type");
        query.setParameter("orcid", orcid);
        query.setParameter("type", type.name());
        return query.executeUpdate() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OrcidSocialEntity> getRecordsToTweet() {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM orcid_social where type='TWITTER' AND (last_run is NULL OR last_run < (NOW() - CAST('1' as INTERVAL HOUR)))", OrcidSocialEntity.class);
        return query.getResultList();
    }
}
