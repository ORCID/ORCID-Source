package org.orcid.core.manager.v3.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbOtherNameAdapter;
import org.orcid.core.manager.v3.read_only.OtherNameManagerReadOnly;
import org.orcid.jaxb.model.v3.release.record.OtherName;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.persistence.dao.OtherNameDao;
import org.orcid.persistence.jpa.entities.OtherNameEntity;

public class OtherNameManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements OtherNameManagerReadOnly {
       
    @Resource(name = "jpaJaxbOtherNameAdapterV3")
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
        List<OtherNameEntity> otherNameEntityList = otherNameDao.getPublicOtherNames(orcid, getLastModified(orcid));
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
