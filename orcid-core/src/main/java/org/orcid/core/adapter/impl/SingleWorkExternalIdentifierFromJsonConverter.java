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

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.notification.addactivities.ExternalIdentifier;

/**
 * 
 * @author Will Simpson
 *
 */
public final class SingleWorkExternalIdentifierFromJsonConverter extends BidirectionalConverter<ExternalIdentifier, String> {

    @Override
    public String convertTo(ExternalIdentifier source, Type<String> destinationType) {
        WorkExternalIdentifier workExternalIdentifier = new WorkExternalIdentifier();
        workExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(source.getExternalIdentifierId()));
        workExternalIdentifier.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(source.getExternalIdentifierType()));
        return JsonUtils.convertToJsonString(workExternalIdentifier);
    }

    @Override
    public ExternalIdentifier convertFrom(String source, Type<ExternalIdentifier> destinationType) {
        WorkExternalIdentifier workExternalIdentifier = JsonUtils.readObjectFromJsonString(source, WorkExternalIdentifier.class);
        ExternalIdentifier extId = new ExternalIdentifier();
        extId.setExternalIdentifierId(workExternalIdentifier.getWorkExternalIdentifierId().getContent());
        extId.setExternalIdType(workExternalIdentifier.getWorkExternalIdentifierType().value());
        return extId;
    }

}