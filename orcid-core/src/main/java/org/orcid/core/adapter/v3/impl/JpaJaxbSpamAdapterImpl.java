package org.orcid.core.adapter.v3.impl;

import org.orcid.core.adapter.v3.JpaJaxbSpamAdapter;
import org.orcid.jaxb.model.v3.release.record.Spam;
import org.orcid.persistence.jpa.entities.SpamEntity;

import ma.glasnost.orika.MapperFacade;

public class JpaJaxbSpamAdapterImpl implements JpaJaxbSpamAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public SpamEntity toSpamEntity(Spam spam) {
        if (spam == null) {
            return null;
        }
        return mapperFacade.map(spam, SpamEntity.class);
    }

    @Override
    public Spam toSpam(SpamEntity spamEntity) {
        if (spamEntity == null) {
            return null;
        }
        return mapperFacade.map(spamEntity, Spam.class);
    }
    
}
