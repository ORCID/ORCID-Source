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
import org.orcid.jaxb.model.record_v2.ExternalIDs;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * This class serializes a single ExternalID into a WorkExternalIdentifiers with
 * one item - it is used in the works table. On the way back, it takes a
 * WorkExternalIdentifiers and returns the first WorkExternalIdentifier as an
 * ExternalID
 * 
 * There is a similar class (ExternalIDConvertor) which does not wrap the
 * workExternalIdentifier in a WorkExternalIdentifiers - that is used for peer
 * reviews.
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
    }

    @Override
    public ExternalID convertFrom(String source, Type<ExternalID> destinationType) {
        WorkExternalIdentifiers ids = WorkExternalIdentifiers.fromDBJSONString(source);
        WorkExternalIdentifier id = ids.getWorkExternalIdentifier().get(0);
        return id.toRecordPojo();
    }

}