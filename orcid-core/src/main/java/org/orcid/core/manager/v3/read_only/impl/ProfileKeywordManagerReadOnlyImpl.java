package org.orcid.core.manager.v3.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbKeywordAdapter;
import org.orcid.core.manager.v3.read_only.ProfileKeywordManagerReadOnly;
import org.orcid.jaxb.model.v3.release.record.Keyword;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.persistence.dao.ProfileKeywordDao;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;

public class ProfileKeywordManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ProfileKeywordManagerReadOnly {

    @Resource(name = "jpaJaxbKeywordAdapterV3")
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
        List<ProfileKeywordEntity> entities = profileKeywordDao.getPublicProfileKeywords(orcid, getLastModified(orcid));
        return adapter.toKeywords(entities);        
    }

    @Override
    public Keyword getKeyword(String orcid, Long putCode) {
        ProfileKeywordEntity entity = profileKeywordDao.getProfileKeyword(orcid, putCode);
        return adapter.toKeyword(entity);
    }
}
