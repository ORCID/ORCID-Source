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
package org.orcid.core.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbEducationAdapter;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.jaxb.model.record.AffiliationType;
import org.orcid.jaxb.model.record.Education;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class AffiliationsManagerImpl implements AffiliationsManager {

    @Resource
    OrgAffiliationRelationDao affiliationsDao;
    
    @Resource 
    JpaJaxbEducationAdapter jpaJaxbEducationAdapter;
    
    @Override
    public OrgAffiliationRelationEntity findAffiliationByUserAndId(String userOrcid, String affiliationId) {
        if(PojoUtil.isEmpty(userOrcid) || PojoUtil.isEmpty(affiliationId))
            return null;
        return affiliationsDao.getOrgAffiliationRelation(userOrcid, affiliationId);
    }

    @Override
    public List<OrgAffiliationRelationEntity> findAffiliationsByType(AffiliationType type) {
        if(type == null)
            return null;
        return affiliationsDao.getByType(type);
    }

    @Override
    public List<OrgAffiliationRelationEntity> findAffiliationsByUserAndType(String userOrcid, AffiliationType type) {
        if(PojoUtil.isEmpty(userOrcid) || type == null)
            return null;
        return affiliationsDao.getByUserAndType(userOrcid, type);
    }
    
    @Override
    public Education getEducationAffiliation(String userOrcid, String affiliationId) {
        OrgAffiliationRelationEntity entity = findAffiliationByUserAndId(userOrcid, affiliationId);
        return jpaJaxbEducationAdapter.toEducation(entity);
    }
}
