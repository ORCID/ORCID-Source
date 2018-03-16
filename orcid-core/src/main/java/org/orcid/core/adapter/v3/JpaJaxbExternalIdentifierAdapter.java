package org.orcid.core.adapter.v3;

import java.util.Collection;

import org.orcid.jaxb.model.v3.dev1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.dev1.record.PersonExternalIdentifiers;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

public interface JpaJaxbExternalIdentifierAdapter {
    ExternalIdentifierEntity toExternalIdentifierEntity(PersonExternalIdentifier externalIdentifier);

    PersonExternalIdentifier toExternalIdentifier(ExternalIdentifierEntity entity);
    
    PersonExternalIdentifiers toExternalIdentifierList(Collection<ExternalIdentifierEntity> entities);
    
    ExternalIdentifierEntity toExternalIdentifierEntity(PersonExternalIdentifier externalIdentifier, ExternalIdentifierEntity existing);
}
