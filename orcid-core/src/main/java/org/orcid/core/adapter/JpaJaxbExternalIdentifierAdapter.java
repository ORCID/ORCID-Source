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

import org.orcid.jaxb.model.record_rc2.ExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.ExternalIdentifiers;
import org.orcid.persistence.jpa.entities.ExternalIdentifierEntity;

public interface JpaJaxbExternalIdentifierAdapter {
    ExternalIdentifierEntity toExternalIdentifierEntity(ExternalIdentifier externalIdentifier);

    ExternalIdentifier toExternalIdentifier(ExternalIdentifierEntity entity);
    
    ExternalIdentifiers toExternalIdentifierList(Collection<ExternalIdentifierEntity> entities);
    
    ExternalIdentifierEntity toExternalIdentifierEntity(ExternalIdentifier externalIdentifier, ExternalIdentifierEntity existing);
}
