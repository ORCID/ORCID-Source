package org.orcid.core.manager.v3.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbExternalIdentifierAdapter;
import org.orcid.core.manager.v3.read_only.ExternalIdentifierManagerReadOnly;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.persistence.dao.ExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

public class ExternalIdentifierManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ExternalIdentifierManagerReadOnly {
    
    @Resource(name = "jpaJaxbExternalIdentifierAdapterV3")
    protected JpaJaxbExternalIdentifierAdapter jpaJaxbExternalIdentifierAdapter;
    
    protected ExternalIdentifierDao externalIdentifierDao;

    public void setExternalIdentifierDao(ExternalIdentifierDao externalIdentifierDao) {
        this.externalIdentifierDao = externalIdentifierDao;
    }

    @Override
    public PersonExternalIdentifiers getPublicExternalIdentifiers(String orcid) {
        List<ExternalIdentifierEntity> externalIdentifiers = externalIdentifierDao.getPublicExternalIdentifiers(orcid, getLastModified(orcid));
        return jpaJaxbExternalIdentifierAdapter.toExternalIdentifierList(externalIdentifiers);
    }

    @Override
    public PersonExternalIdentifiers getExternalIdentifiers(String orcid) {
        List<ExternalIdentifierEntity> externalIdentifiers = externalIdentifierDao.getExternalIdentifiers(orcid, getLastModified(orcid));
        return jpaJaxbExternalIdentifierAdapter.toExternalIdentifierList(externalIdentifiers);
    }

    @Override
    public PersonExternalIdentifier getExternalIdentifier(String orcid, Long id) {  
        ExternalIdentifierEntity entity = externalIdentifierDao.getExternalIdentifierEntity(orcid, id);
        return jpaJaxbExternalIdentifierAdapter.toExternalIdentifier(entity);
    }
}
