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
        WorkExternalIDConverter conv = new WorkExternalIDConverter();
        WorkExternalIdentifiers ids = JsonUtils.readObjectFromJsonString(externalIdentifiersAsString, WorkExternalIdentifiers.class);        
        for (WorkExternalIdentifier id : ids.getWorkExternalIdentifier()){
            ExternalID exid = conv.convertRC1toRC2(id);
            result.getExternalIdentifier().add(exid);
        }
        return result;
    }

    @Override
    public String convertTo(ExternalIDs externalIDs, Type<String> arg1) {
        WorkExternalIdentifiers ids = new WorkExternalIdentifiers();
        WorkExternalIDConverter conv = new WorkExternalIDConverter();
        for (ExternalID externalID : externalIDs.getExternalIdentifier()){
<<<<<<< HEAD
            WorkExternalIdentifier id = conv.convertRC2toRC1(externalID);
            ids.getExternalIdentifier().add(id);
=======
            ids.getExternalIdentifier().add(conv.convertToRC1(externalID));
>>>>>>> master
        }        
        return JsonUtils.convertToJsonString(ids);
    }

}
