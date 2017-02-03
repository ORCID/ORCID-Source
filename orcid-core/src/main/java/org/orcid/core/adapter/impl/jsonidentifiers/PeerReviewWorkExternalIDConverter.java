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

import org.orcid.jaxb.model.record_v2.ExternalID;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * This class serializes a single ExternalID into a WorkExternalIdentifier. It
 * is used by peer review. Works are handled differently by
 * SingleWorkExternalIdentifierConvertor.
 * 
 * @author tom
 *
 */
public class PeerReviewWorkExternalIDConverter extends BidirectionalConverter<ExternalID, String> {

    /**
     * Uses rc1 as intermediary form
     * 
     */
    @Override
    public ExternalID convertFrom(String externalIdentifiersAsString, Type<ExternalID> arg1) {
        WorkExternalIdentifier id = WorkExternalIdentifier.fromDBJSONString(externalIdentifiersAsString);
        return id.toRecordPojo();
    }

    /**
     * Currently transforms into rc1 format
     * 
     */
    @Override
    public String convertTo(ExternalID externalID, Type<String> arg1) {
        WorkExternalIdentifier id = new WorkExternalIdentifier(externalID);
        return id.toDBJSONString();
    }

}
