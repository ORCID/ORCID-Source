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
