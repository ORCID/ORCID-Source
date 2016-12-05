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
package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.JpaJaxbEmailAdapter;
import org.orcid.jaxb.model.record_rc4.Email;
import org.orcid.persistence.jpa.entities.EmailEntity;

public class JpaJaxbEmailAdapterImpl implements JpaJaxbEmailAdapter {

    private MapperFacade mapperFacade;
    
    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
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
