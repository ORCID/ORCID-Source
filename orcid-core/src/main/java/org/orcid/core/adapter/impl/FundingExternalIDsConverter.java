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
import org.orcid.jaxb.model.record_rc2.ExternalIDs;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class FundingExternalIDsConverter extends BidirectionalConverter<ExternalIDs, String> {

    @Override
    public ExternalIDs convertFrom(String externalIdentifiersAsString, Type<ExternalIDs> type) {
        org.orcid.pojo.FundingExternalIdentifiers jpaExtIds = JsonUtils.readObjectFromJsonString(externalIdentifiersAsString, org.orcid.pojo.FundingExternalIdentifiers.class);
        ExternalIDs result = new ExternalIDs();
        for(org.orcid.pojo.FundingExternalIdentifier jpaExtId : jpaExtIds.getFundingExternalIdentifier()) {
            result.getExternalIdentifiers().add(jpaExtId.toRecordPojo());
        }
        return result;
    }

    @Override
    public String convertTo(ExternalIDs fundingExternalIdentifiers, Type<String> arg1) {
        org.orcid.pojo.FundingExternalIdentifiers jpaExternalIdentifiers = org.orcid.pojo.FundingExternalIdentifiers.fromRecordPojo(fundingExternalIdentifiers);
        return JsonUtils.convertToJsonString(jpaExternalIdentifiers);
    }
    
}