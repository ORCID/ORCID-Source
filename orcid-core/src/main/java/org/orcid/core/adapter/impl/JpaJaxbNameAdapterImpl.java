package org.orcid.core.adapter.impl;

import org.orcid.core.adapter.JpaJaxbNameAdapter;
import org.orcid.jaxb.model.record_v2.Name;
import org.orcid.persistence.jpa.entities.RecordNameEntity;

public class JpaJaxbNameAdapterImpl implements JpaJaxbNameAdapter {

    private final MapperFacadeSupport mapperFacade = new MapperFacadeSupport();

    public void setMapperFacade(Object mapperFacade) {
        this.mapperFacade.setMapperFacade(mapperFacade);
    }
    
    @Override
    public RecordNameEntity toRecordNameEntity(Name name) {
        if(name == null){
            return null;
        }
        
        return mapperFacade.map(name, RecordNameEntity.class);
    }

    @Override
    public Name toName(RecordNameEntity entity) {
        if(entity == null) {
            return null;
        }
        
        return mapperFacade.map(entity, Name.class);
    }

    @Override
    public RecordNameEntity toRecordNameEntity(Name name, RecordNameEntity existing) {
        if(name == null){
            return null;
        }
        mapperFacade.map(name, existing);
        return existing;
    }

}
