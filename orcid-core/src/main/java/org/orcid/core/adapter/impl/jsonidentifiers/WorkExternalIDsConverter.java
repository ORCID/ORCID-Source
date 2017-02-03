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

import org.orcid.jaxb.model.record_v2.ExternalIDs;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class WorkExternalIDsConverter extends BidirectionalConverter<ExternalIDs, String> {

    @Override
    public ExternalIDs convertFrom(String externalIdentifiersAsString, Type<ExternalIDs> arg1) {
        WorkExternalIdentifiers ids = WorkExternalIdentifiers.fromDBJSONString(externalIdentifiersAsString);
        return ids.toRecordPojo();
    }

    @Override
    public String convertTo(ExternalIDs externalIDs, Type<String> arg1) {
        WorkExternalIdentifiers ids = new WorkExternalIdentifiers(externalIDs);
        return ids.toDBJSONString();
    }

}
