package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.impl.MapperFacadeSupport;
import org.orcid.jaxb.model.v3.release.record.Service;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.core.adapter.v3.JpaJaxbServiceAdapter;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class JpaJaxbServiceAdapterImpl implements JpaJaxbServiceAdapter {

    private final MapperFacadeSupport mapperFacade = new MapperFacadeSupport();

    public void setMapperFacade(Object mapperFacade) {
        this.mapperFacade.setMapperFacade(mapperFacade);
    }

    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Service service) {
        if(service == null)
            return null;
        return mapperFacade.map(service, OrgAffiliationRelationEntity.class);
    }

    @Override
    public Service toService(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, Service.class);
    }

    public ServiceSummary toServiceSummary(OrgAffiliationRelationEntity entity) {
        if(entity == null)
            return null;
        return mapperFacade.map(entity, ServiceSummary.class);
    }
    
    @Override
    public List<Service> toService(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, Service.class);
    }

    @Override
    public List<ServiceSummary> toServiceSummary(Collection<OrgAffiliationRelationEntity> entities) {
        if(entities == null)
            return null;
        return mapperFacade.mapAsList(entities, ServiceSummary.class);
    }
    
    @Override
    public OrgAffiliationRelationEntity toOrgAffiliationRelationEntity(Service service, OrgAffiliationRelationEntity existing) {
        if (service == null) {
            return null;
        }
        mapperFacade.map(service, existing);
        return existing;
    }
}
