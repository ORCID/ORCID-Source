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
package org.orcid.core.adapter.impl.jsonidentifiers;

import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;

/** This class serializes a single ExternalID into a WorkExternalIdentifiers with one item - it is used in the works table.
 * On the way back, it takes a WorkExternalIdentifiers and returns the first WorkExternalIdentifier as an ExternalID
 * 
 * There is a similar class (ExternalIDConvertor) which does not wrap the workExternalIdentifier in a WorkExternalIdentifiers - that is used for peer reviews.
 * 
 * @author Will Simpson
 *
 */
public final class SingleWorkExternalIdentifierFromJsonConverter extends BidirectionalConverter<ExternalID, String> {

    @Override
    public String convertTo(ExternalID source, Type<String> destinationType) {
        ExternalIDs eids = new ExternalIDs();
        eids.getExternalIdentifier().add(source);
        WorkExternalIdentifiers ids = new WorkExternalIdentifiers(eids);
        return ids.toDBJSONString();
        
        /*
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
        return JsonUtils.convertToJsonString(workExternalIdentifiers);*/
    }

    @Override
    public ExternalID convertFrom(String source, Type<ExternalID> destinationType) {
        WorkExternalIdentifiers ids = WorkExternalIdentifiers.fromDBJSONString(source);
        WorkExternalIdentifier id = ids.getWorkExternalIdentifier().get(0);
        return id.toRecordPojo();
        /*
        WorkExternalIdentifiers workExternalIdentifiers = JsonUtils.readObjectFromJsonString(source, WorkExternalIdentifiers.class);
        List<WorkExternalIdentifier> workExternalIdentifierList = workExternalIdentifiers.getWorkExternalIdentifier();
        if (workExternalIdentifierList.isEmpty()) {
            return null;
        }
        WorkExternalIdentifier workExternalIdentifier = workExternalIdentifierList.get(0);
        ExternalID extId = new ExternalID();
        extId.setValue(workExternalIdentifier.getWorkExternalIdentifierId().getContent());
        extId.setType(workExternalIdentifier.getWorkExternalIdentifierType().value()); //should this be name?
        return extId;*/
    }

}