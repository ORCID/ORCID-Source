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
package org.orcid.core.manager.read_only.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbOtherNameAdapter;
import org.orcid.core.manager.read_only.OtherNameManagerReadOnly;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.springframework.cache.annotation.Cacheable;

public class OtherNameManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements OtherNameManagerReadOnly {
       
    @Resource
    protected JpaJaxbOtherNameAdapter jpaJaxbOtherNameAdapter;

    protected OtherNameDao otherNameDao;
    
    public void setOtherNameDao(OtherNameDao otherNameDao) {
        this.otherNameDao = otherNameDao;
    }

    @Override
    @Cacheable(value = "other-names", key = "#orcid.concat('-').concat(#lastModified)")
    public OtherNames getOtherNames(String orcid, long lastModified) {
        return getOtherNames(orcid, null);
    }
    
    @Override
    @Cacheable(value = "public-other-names", key = "#orcid.concat('-').concat(#lastModified)")
    public OtherNames getPublicOtherNames(String orcid, long lastModified) {
        return getOtherNames(orcid, Visibility.PUBLIC);        
    }
    
    private OtherNames getOtherNames(String orcid, Visibility visibility) {
        List<OtherNameEntity> otherNameEntityList = new ArrayList<OtherNameEntity>();
        if(visibility == null) {
            otherNameEntityList = otherNameDao.getOtherNames(orcid, getLastModified(orcid));
        } else {
            otherNameEntityList = otherNameDao.getOtherNames(orcid, visibility);
        }
        
        return jpaJaxbOtherNameAdapter.toOtherNameList(otherNameEntityList);
    }
    
    @Override
    @Cacheable(value = "minimized-other-names", key = "#orcid.concat('-').concat(#lastModified)")
    public OtherNames getMinimizedOtherNames(String orcid, long lastModified) {
        List<OtherNameEntity> otherNameEntityList = otherNameDao.getOtherNames(orcid, lastModified);
        return jpaJaxbOtherNameAdapter.toMinimizedOtherNameList(otherNameEntityList);
    }
    

    @Override
    public OtherName getOtherName(String orcid, Long putCode) {        
        OtherNameEntity otherNameEntity = otherNameDao.getOtherName(orcid, putCode);
        return jpaJaxbOtherNameAdapter.toOtherName(otherNameEntity);
    }
}
