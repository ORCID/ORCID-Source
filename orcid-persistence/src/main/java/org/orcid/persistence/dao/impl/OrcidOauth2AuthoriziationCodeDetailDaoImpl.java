package org.orcid.persistence.dao.impl;

import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.orcid.persistence.aop.ExcludeFromProfileLastModifiedUpdate;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Declan Newman (declan) Date: 24/04/2012
 */
@PersistenceContext(unitName = "orcid")
public class OrcidOauth2AuthoriziationCodeDetailDaoImpl extends GenericDaoImpl<OrcidOauth2AuthoriziationCodeDetail, String> implements
        OrcidOauth2AuthoriziationCodeDetailDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidOauth2AuthoriziationCodeDetailDaoImpl.class);

    public OrcidOauth2AuthoriziationCodeDetailDaoImpl() {
        super(OrcidOauth2AuthoriziationCodeDetail.class);
    }

    @Override
    @ExcludeFromProfileLastModifiedUpdate
    public OrcidOauth2AuthoriziationCodeDetail removeAndReturn(String code) {
        OrcidOauth2AuthoriziationCodeDetail orcidOauth2AuthoriziationCodeDetail = find(code);

        if (orcidOauth2AuthoriziationCodeDetail != null) {
            if (orcidOauth2AuthoriziationCodeDetail.getClientDetailsEntity() == null) {
                LOGGER.error("The client details entity is empty for code: {}", code);
            } else if (orcidOauth2AuthoriziationCodeDetail.getClientDetailsEntity().getId() == null
                    || orcidOauth2AuthoriziationCodeDetail.getClientDetailsEntity().getId().trim().isEmpty()) {
                LOGGER.error("The client details entity dont have the client id for code: {}", code);
            }
            remove(code);
            return orcidOauth2AuthoriziationCodeDetail;
        } else {
            return null;
        }
    }

    @Override
    public boolean isPersistentToken(String code) {
        TypedQuery<OrcidOauth2AuthoriziationCodeDetail> query = entityManager.createQuery("from OrcidOauth2AuthoriziationCodeDetail where id=:code",
                OrcidOauth2AuthoriziationCodeDetail.class);
        query.setParameter("code", code);
        OrcidOauth2AuthoriziationCodeDetail result = query.getSingleResult();
        return result.isPersistent();
    }
}
