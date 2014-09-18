/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao.impl;

import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.orcid.persistence.aop.ExcludeFromProfileLastModifiedUpdate;
import org.orcid.persistence.dao.OrcidOauth2AuthoriziationCodeDetailDao;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 24/04/2012
 */
@PersistenceContext(unitName = "orcid")
public class OrcidOauth2AuthoriziationCodeDetailDaoImpl extends GenericDaoImpl<OrcidOauth2AuthoriziationCodeDetail, String> implements
        OrcidOauth2AuthoriziationCodeDetailDao {

    public OrcidOauth2AuthoriziationCodeDetailDaoImpl() {
        super(OrcidOauth2AuthoriziationCodeDetail.class);
    }

    @Override
    @ExcludeFromProfileLastModifiedUpdate
    public OrcidOauth2AuthoriziationCodeDetail removeAndReturn(String code) {
        OrcidOauth2AuthoriziationCodeDetail orcidOauth2AuthoriziationCodeDetail = find(code);
        if (orcidOauth2AuthoriziationCodeDetail != null) {
            remove(code);
            return orcidOauth2AuthoriziationCodeDetail;
        } else {
            return null;
        }
    }
    
    @Override
    public boolean isPersistentToken(String code) {
        TypedQuery<OrcidOauth2AuthoriziationCodeDetail> query = entityManager.createQuery(
                "from OrcidOauth2AuthoriziationCodeDetail where id=:code", OrcidOauth2AuthoriziationCodeDetail.class);
        query.setParameter("code", code);
        OrcidOauth2AuthoriziationCodeDetail result = query.getSingleResult();
        return result.isPersistent();
    }
}
