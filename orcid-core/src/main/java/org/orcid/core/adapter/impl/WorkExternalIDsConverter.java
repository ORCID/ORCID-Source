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

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common_rc1.Url;
import org.orcid.jaxb.model.record_rc1.Relationship;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifier;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class WorkExternalIDsConverter extends BidirectionalConverter<ExternalIDs, String> {

    /** Uses rc1 as middle stage
     * 
     */
    @Override
    public ExternalIDs convertFrom(String externalIdentifiersAsString, Type<ExternalIDs> arg1) {
        ExternalIDs result = new ExternalIDs();
        WorkExternalIdentifiers ids = JsonUtils.readObjectFromJsonString(externalIdentifiersAsString, WorkExternalIdentifiers.class);        
        for (WorkExternalIdentifier id : ids.getWorkExternalIdentifier()){
            ExternalID exid = new ExternalID();
            exid.setType(id.getWorkExternalIdentifierType().value());
            if (id.getRelationship() != null){
                exid.setRelationship(org.orcid.jaxb.model.record_rc2.Relationship.fromValue(id.getRelationship().value()));                
            }
            if (id.getUrl() != null){
                exid.setUrl(new org.orcid.jaxb.model.common_rc2.Url(id.getUrl().getValue()));                
            }
            exid.setValue(id.getWorkExternalIdentifierId().getContent());
            result.getExternalIdentifier().add(exid);
        }
        return result;
    }

    @Override
    public String convertTo(ExternalIDs externalIDs, Type<String> arg1) {
        WorkExternalIdentifiers ids = new WorkExternalIdentifiers();
        WorkExternalIDConverter conv = new WorkExternalIDConverter();
        for (ExternalID externalID : externalIDs.getExternalIdentifier()){
            ids.getExternalIdentifier().add(conv.convertToRC1(externalID));
        }        
        return JsonUtils.convertToJsonString(ids);
    }

}
