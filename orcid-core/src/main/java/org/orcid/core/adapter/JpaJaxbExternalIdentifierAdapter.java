package org.orcid.core.adapter;

import java.util.Collection;

import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

public interface JpaJaxbExternalIdentifierAdapter {
    ExternalIdentifierEntity toExternalIdentifierEntity(PersonExternalIdentifier externalIdentifier);

    PersonExternalIdentifier toExternalIdentifier(ExternalIdentifierEntity entity);
    
    PersonExternalIdentifiers toExternalIdentifierList(Collection<ExternalIdentifierEntity> entities);
    
    ExternalIdentifierEntity toExternalIdentifierEntity(PersonExternalIdentifier externalIdentifier, ExternalIdentifierEntity existing);
}
