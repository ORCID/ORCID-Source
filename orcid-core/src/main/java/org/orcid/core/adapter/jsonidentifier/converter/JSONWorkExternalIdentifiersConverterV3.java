package org.orcid.core.adapter.jsonidentifier.converter;

import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier.WorkExternalIdentifierId;
import org.apache.commons.lang.StringUtils;
import org.orcid.core.adapter.jsonidentifier.JSONUrl;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifiers;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
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

    private PIDNormalizationService norm;
    private LocaleManager localeManager;
    
    public JSONWorkExternalIdentifiersConverterV3(PIDNormalizationService norm, LocaleManager localeManager){
        this.norm=norm;
        this.localeManager=localeManager;
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
                }
                if (normalised == null || StringUtils.isEmpty(normalised)){
                    id.setNormalizedError(new TransientError(localeManager.resolveMessage("transientError.normalization_failed.code"),localeManager.resolveMessage("transientError.normalization_failed.message",id.getType(),workExternalIdentifier.getWorkExternalIdentifierId().content )));
                }
                
                id.setNormalizedUrl(new TransientNonEmptyString(norm.generateNormalisedURL(id.getType(), workExternalIdentifier.getWorkExternalIdentifierId().content)));
                if (StringUtils.isEmpty(id.getNormalizedUrl().getValue())){
                    id.setNormalizedUrlError(new TransientError(localeManager.resolveMessage("transientError.normalization_failed.code"),localeManager.resolveMessage("transientError.normalization_failed.message",id.getType(),workExternalIdentifier.getWorkExternalIdentifierId().content )));
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
