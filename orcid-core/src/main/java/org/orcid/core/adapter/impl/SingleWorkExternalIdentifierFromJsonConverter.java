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
import org.orcid.jaxb.model.record_rc2.ExternalID;

/**
 * 
 * @author Will Simpson
 *
 */
public final class SingleWorkExternalIdentifierFromJsonConverter extends BidirectionalConverter<ExternalID, String> {

    @Override
    public String convertTo(ExternalID source, Type<String> destinationType) {
        WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
        WorkExternalIdentifier workExternalIdentifier = new WorkExternalIdentifier();
        workExternalIdentifiers.getWorkExternalIdentifier().add(workExternalIdentifier);
        workExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(source.getValue()));
        //TODO: work with non-schema types
        try{
            workExternalIdentifier.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(source.getType()));
        }catch (IllegalArgumentException e){
            throw e;
        }
        return JsonUtils.convertToJsonString(workExternalIdentifiers);
    }

    @Override
    public ExternalID convertFrom(String source, Type<ExternalID> destinationType) {
        WorkExternalIdentifiers workExternalIdentifiers = JsonUtils.readObjectFromJsonString(source, WorkExternalIdentifiers.class);
        List<WorkExternalIdentifier> workExternalIdentifierList = workExternalIdentifiers.getWorkExternalIdentifier();
        if (workExternalIdentifierList.isEmpty()) {
            return null;
        }
        WorkExternalIdentifier workExternalIdentifier = workExternalIdentifierList.get(0);
        ExternalID extId = new ExternalID();
        extId.setValue(workExternalIdentifier.getWorkExternalIdentifierId().getContent());
        extId.setType(workExternalIdentifier.getWorkExternalIdentifierType().value()); //should this be name?
        return extId;
    }

}