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

import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.notification.permission.ExternalIdentifier;

/**
 * 
 * @author Will Simpson
 *
 */
public final class SingleWorkExternalIdentifierFromJsonConverter extends BidirectionalConverter<ExternalIdentifier, String> {

    @Override
    public String convertTo(ExternalIdentifier source, Type<String> destinationType) {
        WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier workExternalIdentifier = new WorkExternalIdentifier();
        workExternalIdentifiers.getWorkExternalIdentifier().add(workExternalIdentifier);
        workExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(source.getExternalIdentifierId()));
        workExternalIdentifier.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(source.getExternalIdentifierType()));
        return JsonUtils.convertToJsonString(workExternalIdentifiers);
    }

    @Override
    public ExternalIdentifier convertFrom(String source, Type<ExternalIdentifier> destinationType) {
        WorkExternalIdentifiers workExternalIdentifiers = JsonUtils.readObjectFromJsonString(source, WorkExternalIdentifiers.class);
        List<WorkExternalIdentifier> workExternalIdentifierList = workExternalIdentifiers.getWorkExternalIdentifier();
        if (workExternalIdentifierList.isEmpty()) {
            return null;
        }
        WorkExternalIdentifier workExternalIdentifier = workExternalIdentifierList.get(0);
        ExternalIdentifier extId = new ExternalIdentifier();
        extId.setExternalIdentifierId(workExternalIdentifier.getWorkExternalIdentifierId().getContent());
        extId.setExternalIdentifierType(workExternalIdentifier.getWorkExternalIdentifierType().value());
        return extId;
    }

}