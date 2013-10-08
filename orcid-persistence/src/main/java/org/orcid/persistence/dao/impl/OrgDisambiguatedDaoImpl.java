/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrgDisambiguatedDaoImpl extends GenericDaoImpl<OrgDisambiguatedEntity, Long> implements OrgDisambiguatedDao {

    public OrgDisambiguatedDaoImpl() {
        super(OrgDisambiguatedEntity.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<OrgDisambiguatedEntity> getOrgs(String searchTerm, int firstResult, int maxResults) {
//        We might want to sort by popularity 
//                Query query = entityManager.createNativeQuery(
//                "select od.* from org_disambiguated od join org_affiliation_relation oa on od.id = oa.org_id" +
//                "  where lower(name) like '%' || lower(:searchTerm) || '%'" +
//                "  group by od.id order by count(*) DESC" 
//               , OrgDisambiguatedEntity.class);

        TypedQuery<OrgDisambiguatedEntity> query = entityManager.createQuery(
                "from OrgDisambiguatedEntity where lower(name) like '%' || lower(:searchTerm) || '%' order by name", OrgDisambiguatedEntity.class);
        query.setParameter("searchTerm", searchTerm);
        query.setFirstResult(firstResult);
        query.setMaxResults(maxResults);
         
        return query.getResultList();
    }

}