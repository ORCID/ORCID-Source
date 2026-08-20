package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.JpaJaxbEmailAdapter;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.persistence.jpa.entities.EmailEntity;

public class JpaJaxbEmailAdapterImpl implements JpaJaxbEmailAdapter {

    private final MapperFacadeSupport mapperFacade = new MapperFacadeSupport();
    
    public void setMapperFacade(Object mapperFacade) {
        this.mapperFacade.setMapperFacade(mapperFacade);
    }
    
    @Override
    public EmailEntity toEmailEntity(Email email) {
        if(email == null) {
            return null;
        }
        return mapperFacade.map(email, EmailEntity.class);
    }

    @Override
    public Email toEmail(EmailEntity entity) {
        if(entity == null) {
            return null;
        }
        
        return mapperFacade.map(entity, Email.class);
    }

    @Override
    public List<Email> toEmailList(Collection<EmailEntity> entities) {
        if(entities == null) {
            return null;
        }
        return mapperFacade.mapAsList(entities, Email.class);
    }

    @Override
    public EmailEntity toEmailEntity(Email email, EmailEntity existing) {
        if(email == null) {
            return null;
        }
        
        mapperFacade.map(email, existing);
        return existing;
    }

}
