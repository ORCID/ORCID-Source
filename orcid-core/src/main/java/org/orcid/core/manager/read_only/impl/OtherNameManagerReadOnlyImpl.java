package org.orcid.core.manager.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbOtherNameAdapter;
import org.orcid.core.manager.read_only.OtherNameManagerReadOnly;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;

public class OtherNameManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements OtherNameManagerReadOnly {
       
    @Resource
    protected JpaJaxbOtherNameAdapter jpaJaxbOtherNameAdapter;

    protected OtherNameDao otherNameDao;
    
    public void setOtherNameDao(OtherNameDao otherNameDao) {
        this.otherNameDao = otherNameDao;
    }

    @Override
    public OtherNames getOtherNames(String orcid) {
        List<OtherNameEntity> otherNameEntityList = otherNameDao.getOtherNames(orcid, getLastModified(orcid));
        return jpaJaxbOtherNameAdapter.toOtherNameList(otherNameEntityList);
    }
    
    @Override
    public OtherNames getPublicOtherNames(String orcid) {
        List<OtherNameEntity> otherNameEntityList = otherNameDao.getOtherNames(orcid, Visibility.PUBLIC);
        return jpaJaxbOtherNameAdapter.toOtherNameList(otherNameEntityList);
    }
  
    @Override
    public OtherNames getMinimizedOtherNames(String orcid) {
        List<OtherNameEntity> otherNameEntityList = otherNameDao.getOtherNames(orcid, getLastModified(orcid));
        return jpaJaxbOtherNameAdapter.toMinimizedOtherNameList(otherNameEntityList);
    }
    

    @Override
    public OtherName getOtherName(String orcid, Long putCode) {        
        OtherNameEntity otherNameEntity = otherNameDao.getOtherName(orcid, putCode);
        return jpaJaxbOtherNameAdapter.toOtherName(otherNameEntity);
    }
}
