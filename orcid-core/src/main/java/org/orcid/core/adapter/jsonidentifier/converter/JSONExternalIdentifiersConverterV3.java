package org.orcid.core.adapter.jsonidentifier.converter;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.adapter.jsonidentifier.JSONExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONExternalIdentifiers;
import org.orcid.core.adapter.jsonidentifier.JSONUrl;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.v3.rc1.common.TransientError;
import org.orcid.jaxb.model.v3.rc1.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.pojo.ajaxForm.PojoUtil;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class JSONExternalIdentifiersConverterV3 extends BidirectionalConverter<ExternalIDs, String> {
    
    private ExternalIdentifierTypeConverter conv = new ExternalIdentifierTypeConverter();
    
    private PIDNormalizationService norm;

    private LocaleManager localeManager;
    
    public JSONExternalIdentifiersConverterV3(PIDNormalizationService norm, LocaleManager localeManager) {
        this.norm = norm;
        this.localeManager = localeManager;
    }

    @Override
    public String convertTo(ExternalIDs source, Type<String> destinationType) {
        JSONExternalIdentifiers jsonExternalIdentifiers = new JSONExternalIdentifiers();
        for (ExternalID externalID : source.getExternalIdentifier()) {
            JSONExternalIdentifier jsonExternalIdentifier = new JSONExternalIdentifier();
            if (externalID.getType() != null) {
                jsonExternalIdentifier.setType(conv.convertTo(externalID.getType(), null));
            }

            if (externalID.getUrl() != null) {
                jsonExternalIdentifier.setUrl(new JSONUrl(externalID.getUrl().getValue()));
            }

            if (!PojoUtil.isEmpty(externalID.getValue())) {
                jsonExternalIdentifier.setValue(externalID.getValue());
            }

            if (externalID.getRelationship() != null) {
                jsonExternalIdentifier.setRelationship(conv.convertTo(externalID.getRelationship().value(), null));
            }
            jsonExternalIdentifiers.getExternalIdentifier().add(jsonExternalIdentifier);
        }
        return JsonUtils.convertToJsonString(jsonExternalIdentifiers);
    }

    @Override
    public ExternalIDs convertFrom(String source, Type<ExternalIDs> destinationType) {
        JSONExternalIdentifiers externalIdentifiers = JsonUtils.readObjectFromJsonString(source, JSONExternalIdentifiers.class);
        ExternalIDs externalIDs = new ExternalIDs();
        for (JSONExternalIdentifier externalIdentifier : externalIdentifiers.getExternalIdentifier()) {
            ExternalID id = new ExternalID();
            if (externalIdentifier.getType() == null) {
                id.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
            } else {
                id.setType(externalIdentifier.getType().toLowerCase());
            }
            if (externalIdentifier.getUrl() != null && !PojoUtil.isEmpty(externalIdentifier.getUrl().getValue())) {
                Url url = new Url(externalIdentifier.getUrl().getValue());
                id.setUrl(url);
            }

            if (!PojoUtil.isEmpty(externalIdentifier.getValue())) {
                id.setValue(externalIdentifier.getValue());
                id.setNormalized(new TransientNonEmptyString(norm.normalise(id.getType(), externalIdentifier.getValue())));
                if (StringUtils.isEmpty(id.getNormalized().getValue())){
                    id.setNormalizedError(new TransientError(localeManager.resolveMessage("transientError.normalization_failed.code"),localeManager.resolveMessage("transientError.normalization_failed.message", id.getType(), externalIdentifier.getValue())));
                }
            }

            if (externalIdentifier.getRelationship() != null) {
                id.setRelationship(Relationship.fromValue(conv.convertFrom(externalIdentifier.getRelationship(), null)));
            }
            externalIDs.getExternalIdentifier().add(id);
        }
        return externalIDs;
    }

}
