package org.orcid.core.manager.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbKeywordAdapter;
import org.orcid.core.manager.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

public class ProfileKeywordManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ProfileKeywordManagerReadOnly {

    @Resource
    protected JpaJaxbKeywordAdapter adapter;

    protected ProfileKeywordDao profileKeywordDao;        
    
    public void setProfileKeywordDao(ProfileKeywordDao profileKeywordDao) {
        this.profileKeywordDao = profileKeywordDao;
    }        
    
    @Override
    public Keywords getKeywords(String orcid) {
        List<ProfileKeywordEntity> entities = profileKeywordDao.getProfileKeywords(orcid, getLastModified(orcid));
        return adapter.toKeywords(entities);        
    }

    @Override
    public Keywords getPublicKeywords(String orcid) {
        List<ProfileKeywordEntity> entities = profileKeywordDao.getProfileKeywords(orcid, Visibility.PUBLIC);
        return adapter.toKeywords(entities);        
    }

    @Override
    public Keyword getKeyword(String orcid, Long putCode) {
        ProfileKeywordEntity entity = profileKeywordDao.getProfileKeyword(orcid, putCode);
        return adapter.toKeyword(entity);
    }
}
