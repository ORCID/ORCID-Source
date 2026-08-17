package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.impl.MapperFacadeSupport;
import org.orcid.core.adapter.v3.JpaJaxbQualificationAdapter;
import org.orcid.jaxb.model.v3.release.record.Qualification;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class JpaJaxbQualificationAdapterImpl implements JpaJaxbQualificationAdapter {

    private final MapperFacadeSupport mapperFacade = new MapperFacadeSupport();

    public void setMapperFacade(Object mapperFacade) {
        this.mapperFacade.setMapperFacade(mapperFacade);
    }

    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Qualification qualification) {
        if(qualification == null)
            return null;
        return mapperFacade.map(qualification, OrgAffiliationRelationEntity.class);
    }

    @Override
    public Qualification toQualification(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, Qualification.class);
    }

    public QualificationSummary toQualificationSummary(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, QualificationSummary.class);
    }
    
    @Override
    public List<Qualification> toQualification(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, Qualification.class);
    }

    @Override
    public List<QualificationSummary> toQualificationSummary(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, QualificationSummary.class);
    }
    
    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Qualification qualification, OrgAffiliationRelationEntity existing) {
        if (qualification == null) {
            return null;
        }
        mapperFacade.map(qualification, existing);
        return existing;
    }
}
