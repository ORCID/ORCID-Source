package org.orcid.persistence.dao.impl;

import org.orcid.persistence.aop.UpdateProfileLastModifiedAndIndexingStatus;
import org.orcid.persistence.dao.ProfileInterstitialFlagDao;
import org.orcid.persistence.jpa.entities.ProfileInterstitialFlagEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.math.BigInteger;
import java.util.List;

public class ProfileInterstitialFlagDaoImpl extends GenericDaoImpl<ProfileInterstitialFlagEntity, Long>
        implements ProfileInterstitialFlagDao {

    public ProfileInterstitialFlagDaoImpl() {
        super(ProfileInterstitialFlagEntity.class);
    }

    @Override
    @Transactional
    public ProfileInterstitialFlagEntity addInterstitialFlag(String orcid, String interstitialName) {
        ProfileInterstitialFlagEntity e = new ProfileInterstitialFlagEntity();
        e.setInterstitialName(interstitialName);
        e.setOrcid(orcid);
        entityManager.persist(e);
        return e;
    }

    @Override
    public boolean hasInterstitialFlag(String orcid, String interstitialName) {
        Query query = entityManager.createNativeQuery(
                "select count(*) from profile_interstitial_flag where orcid = :orcid and interstitial_name = :interstitialName");
        query.setParameter("orcid", orcid);
        query.setParameter("interstitialName", interstitialName);
        long result = ((BigInteger) query.getSingleResult()).longValue();
        return result > 0;
    }

    @Override
    public List<ProfileInterstitialFlagEntity> findByOrcid(String orcid) {
        TypedQuery<ProfileInterstitialFlagEntity> query = entityManager.createQuery(
                "from ProfileInterstitialFlagEntity where orcid = :orcid", ProfileInterstitialFlagEntity.class);
        query.setParameter("orcid", orcid);
        List<ProfileInterstitialFlagEntity> results = query.getResultList();
        return results.isEmpty() ? null : results;
    }
}
