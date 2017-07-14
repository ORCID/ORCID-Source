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
package org.orcid.core.manager.v3.read_only.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbKeywordAdapter;
import org.orcid.core.manager.v3.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.record.Keyword;
import org.orcid.jaxb.model.v3.dev1.record.Keywords;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.springframework.cache.annotation.Cacheable;

public class ProfileKeywordManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ProfileKeywordManagerReadOnly {

    @Resource
    protected JpaJaxbKeywordAdapter adapter;

    protected ProfileKeywordDao profileKeywordDao;        
    
    public void setProfileKeywordDao(ProfileKeywordDao profileKeywordDao) {
        this.profileKeywordDao = profileKeywordDao;
    }        
    
    @Override
    @Cacheable(value = "keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public Keywords getKeywords(String orcid, long lastModified) {
        return getKeywords(orcid, null);
    }

    @Override
    @Cacheable(value = "public-keywords", key = "#orcid.concat('-').concat(#lastModified)")
    public Keywords getPublicKeywords(String orcid, long lastModified) {
        return getKeywords(orcid, Visibility.PUBLIC);
    }

    private Keywords getKeywords(String orcid, Visibility visibility) {
        List<ProfileKeywordEntity> entities = new ArrayList<ProfileKeywordEntity>();
        if(visibility == null) {
            entities = profileKeywordDao.getProfileKeywors(orcid, getLastModified(orcid));
        } else {
            entities = profileKeywordDao.getProfileKeywors(orcid, org.orcid.jaxb.model.common_v2.Visibility.fromValue(Visibility.PUBLIC.value()));
        }
        
        return adapter.toKeywords(entities);        
    }       

    @Override
    public Keyword getKeyword(String orcid, Long putCode) {
        ProfileKeywordEntity entity = profileKeywordDao.getProfileKeyword(orcid, putCode);
        return adapter.toKeyword(entity);
    }
}
