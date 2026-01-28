package org.orcid.core.adapter.jsonidentifier.converter;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.adapter.jsonidentifier.JSONUrl;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier.WorkExternalIdentifierId;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifiers;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverService;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.v3.release.common.TransientError;
import org.orcid.jaxb.model.v3.release.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.pojo.ajaxForm.PojoUtil;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class JSONWorkExternalIdentifiersConverterV3 extends BidirectionalConverter<ExternalIDs, String> {

    private final PIDNormalizationService norm;
    private final String normalizationFailedErrorCode;
    private final String normalizationFailedErrorDescription;

    public JSONWorkExternalIdentifiersConverterV3(PIDNormalizationService norm, PIDResolverService resolverService, LocaleManager localeManager) {
        this.norm = norm;
        // API errors are not localized
        normalizationFailedErrorCode = localeManager.resolveMessage("transientError.normalization_failed.code");
        normalizationFailedErrorDescription = localeManager.resolveMessage("transientError.normalization_failed.message");
    }
    
    private ExternalIdentifierTypeConverter conv = new ExternalIdentifierTypeConverter();

    @Override
    public String convertTo(ExternalIDs source, Type<String> destinationType) {
        JSONWorkExternalIdentifiers jsonWorkExternalIdentifiers = new JSONWorkExternalIdentifiers();
        for (ExternalID externalID : source.getExternalIdentifier()) {
            JSONWorkExternalIdentifier jsonWorkExternalIdentifier = new JSONWorkExternalIdentifier();
            if (externalID.getType() != null) {
                jsonWorkExternalIdentifier.setWorkExternalIdentifierType(conv.convertTo(externalID.getType(), null));
            }

            if (externalID.getUrl() != null) {
                jsonWorkExternalIdentifier.setUrl(new JSONUrl(externalID.getUrl().getValue()));
            }

            if (!PojoUtil.isEmpty(externalID.getValue())) {
                jsonWorkExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(externalID.getValue()));
            }

            if (externalID.getRelationship() != null) {
                jsonWorkExternalIdentifier.setRelationship(conv.convertTo(externalID.getRelationship().value(), null));
            }
            jsonWorkExternalIdentifiers.getWorkExternalIdentifier().add(jsonWorkExternalIdentifier);
        }
        return JsonUtils.convertToJsonString(jsonWorkExternalIdentifiers);
    }

    @Override
    public ExternalIDs convertFrom(String source, Type<ExternalIDs> destinationType) {
        JSONWorkExternalIdentifiers workExternalIdentifiers = JsonUtils.readObjectFromJsonString(source, JSONWorkExternalIdentifiers.class);
        ExternalIDs externalIDs = new ExternalIDs();
        for (JSONWorkExternalIdentifier workExternalIdentifier : workExternalIdentifiers.getWorkExternalIdentifier()) {
            ExternalID id = new ExternalID();
            if (workExternalIdentifier.getWorkExternalIdentifierType() == null) {
                id.setType(WorkExternalIdentifierType.OTHER_ID.value());
            } else {
                id.setType(conv.convertFrom(workExternalIdentifier.getWorkExternalIdentifierType(), null));
            }
            if (workExternalIdentifier.getWorkExternalIdentifierId() != null) {
                id.setValue(workExternalIdentifier.getWorkExternalIdentifierId().content);
                //note, uses API type name.
                String normalised = norm.normalise(id.getType(), workExternalIdentifier.getWorkExternalIdentifierId().content);
                if (normalised != null && !normalised.trim().isEmpty()) {
                    id.setNormalized(new TransientNonEmptyString(normalised));
                } else {
                    id.setNormalizedError(new TransientError(normalizationFailedErrorCode, normalizationFailedErrorDescription.replace("{0}", id.getType()).replace("{1}", workExternalIdentifier.getWorkExternalIdentifierId().content)));
                }
                
                if (workExternalIdentifier.getUrl() != null) {
                    try {
                        String normalizedUrl = norm.generateNormalisedURL(id.getType(), workExternalIdentifier.getUrl().getValue());
                        if (!StringUtils.isBlank(normalizedUrl)) {
                            id.setNormalizedUrl(new TransientNonEmptyString(normalizedUrl));
                        }
                    } catch (IllegalArgumentException e) {
                        // Do not populate the URL
                    }
                } else {
                    try {
                        String normalizedUrl = norm.generateNormalisedURL(id.getType(), workExternalIdentifier.getWorkExternalIdentifierId().content);
                        if (!StringUtils.isBlank(normalizedUrl)) {
                            id.setNormalizedUrl(new TransientNonEmptyString(normalizedUrl));
                        }
                    } catch (IllegalArgumentException e) {
                        // Do not populate the URL
                    }
                }
                
                if (id.getNormalizedUrl() == null || StringUtils.isEmpty(id.getNormalizedUrl().getValue())){
                    id.setNormalizedUrlError(new TransientError(normalizationFailedErrorCode, normalizationFailedErrorDescription.replace("{0}", id.getType()).replace("{1}", workExternalIdentifier.getWorkExternalIdentifierId().content)));
                }                
            }
            if (workExternalIdentifier.getUrl() != null) {
                id.setUrl(new Url(workExternalIdentifier.getUrl().getValue()));
            }
            if (workExternalIdentifier.getRelationship() != null) {
                id.setRelationship(Relationship.fromValue(conv.convertFrom(workExternalIdentifier.getRelationship(), null)));
            }
            externalIDs.getExternalIdentifier().add(id);
        }
        return externalIDs;
    }

}
