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
package org.orcid.core.adapter.v3;

import java.util.Collection;

import org.orcid.jaxb.model.v3.dev1.record.OtherName;
import org.orcid.jaxb.model.v3.dev1.record.OtherNames;
import org.orcid.persistence.jpa.entities.OtherNameEntity;

public interface JpaJaxbOtherNameAdapter {

    OtherNameEntity toOtherNameEntity(OtherName otherName);

    OtherName toOtherName(OtherNameEntity entity);

    OtherNames toOtherNameList(Collection<OtherNameEntity> entities);
    
    OtherNames toMinimizedOtherNameList(Collection<OtherNameEntity> entities);

    OtherNameEntity toOtherNameEntity(OtherName otherName, OtherNameEntity existing);
}
