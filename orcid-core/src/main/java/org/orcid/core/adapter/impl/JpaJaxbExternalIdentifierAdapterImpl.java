package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.JpaJaxbExternalIdentifierAdapter;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

import ma.glasnost.orika.MapperFacade;

public class JpaJaxbExternalIdentifierAdapterImpl implements JpaJaxbExternalIdentifierAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public ExternalIdentifierEntity toExternalIdentifierEntity(PersonExternalIdentifier externalIdentifier) {
        if (externalIdentifier == null) {
            return null;
        }
        
        ExternalIdentifierEntity result = mapperFacade.map(externalIdentifier, ExternalIdentifierEntity.class);
        if(result.getDisplayIndex() == null) {
            result.setDisplayIndex(0L);
        }
        
        return result;
    }

    @Override
    public PersonExternalIdentifier toExternalIdentifier(ExternalIdentifierEntity entity) {
        if (entity == null) {
            return null;
        }
        PersonExternalIdentifier result = mapperFacade.map(entity, PersonExternalIdentifier.class);
        result.setRelationship(Relationship.SELF);
        return result;
    }

    @Override
    public PersonExternalIdentifiers toExternalIdentifierList(Collection<ExternalIdentifierEntity> entities) {
        if (entities == null) {
            return null;
        }

        List<PersonExternalIdentifier> externalIdentifier = mapperFacade.mapAsList(entities, PersonExternalIdentifier.class);
        for(PersonExternalIdentifier e : externalIdentifier) {
            e.setRelationship(Relationship.SELF);
        }
        PersonExternalIdentifiers externalIdentifiers = new PersonExternalIdentifiers();
        externalIdentifiers.setExternalIdentifiers(externalIdentifier);
        return externalIdentifiers;
    }

    @Override
    public ExternalIdentifierEntity toExternalIdentifierEntity(PersonExternalIdentifier externalIdentifier, ExternalIdentifierEntity existing) {
        if (externalIdentifier == null) {
            return null;
        }

        mapperFacade.map(externalIdentifier, existing);

        return existing;
    }

}
