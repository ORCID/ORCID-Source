package org.orcid.core.adapter.mapstruct.jsonidentifier;

import org.mapstruct.Mapper;

import org.orcid.core.adapter.jsonidentifier.JSONExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONFundingExternalIdentifiers;
import org.orcid.core.adapter.jsonidentifier.JSONUrl;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.FundingExternalIdentifier;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.message.FundingExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.pojo.ajaxForm.PojoUtil;

/**
 * Custom MapStruct Mapper for converting V1 FundingExternalIdentifiers to and from JSON.
 */
@Mapper(componentModel = "spring")
public abstract class JSONFundingExternalIdentifiersMapperV1 {

    /**
     * Converts the JAXB V1 FundingExternalIdentifiers object into a JSON String for the database.
     */
    public String convertTo(FundingExternalIdentifiers messagePojo) {
        if (messagePojo == null) {
            return null;
        }

        JSONFundingExternalIdentifiers fundingExternalIdentifiers = new JSONFundingExternalIdentifiers();
        
        if (messagePojo.getFundingExternalIdentifier() != null) {
            for (FundingExternalIdentifier fundingExternalIdentifier : messagePojo.getFundingExternalIdentifier()) {
                JSONExternalIdentifier jsonExternalIdentifier = new JSONExternalIdentifier();
                
                if (fundingExternalIdentifier.getType() != null) {
                    jsonExternalIdentifier.setType(fundingExternalIdentifier.getType().value());
                }
                
                if (fundingExternalIdentifier.getUrl() != null && fundingExternalIdentifier.getUrl().getValue() != null) {
                    jsonExternalIdentifier.setUrl(new JSONUrl(fundingExternalIdentifier.getUrl().getValue()));
                }
                
                if (!PojoUtil.isEmpty(fundingExternalIdentifier.getValue())) {
                    jsonExternalIdentifier.setValue(fundingExternalIdentifier.getValue());
                }
                
                // Hardcoded to SELF per original logic
                jsonExternalIdentifier.setRelationship(Relationship.SELF.value());
                
                fundingExternalIdentifiers.getFundingExternalIdentifier().add(jsonExternalIdentifier);
            }
        }
        
        return JsonUtils.convertToJsonString(fundingExternalIdentifiers);
    }

    /**
     * Converts the database JSON String back into the JAXB V1 FundingExternalIdentifiers object.
     */
    public FundingExternalIdentifiers convertFrom(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        JSONFundingExternalIdentifiers jsonFundingExternalIdentifiers = JsonUtils.readObjectFromJsonString(source, JSONFundingExternalIdentifiers.class);
        if (jsonFundingExternalIdentifiers == null || jsonFundingExternalIdentifiers.getFundingExternalIdentifier() == null) {
            return null;
        }

        FundingExternalIdentifiers fundingExternalIdentifiers = new FundingExternalIdentifiers();
        
        for (JSONExternalIdentifier jsonFundingExternalIdentifier : jsonFundingExternalIdentifiers.getFundingExternalIdentifier()) {
            FundingExternalIdentifier fundingExternalIdentifier = new FundingExternalIdentifier();
            
            try {
                if (jsonFundingExternalIdentifier.getType() != null) {
                    fundingExternalIdentifier.setType(FundingExternalIdentifierType.fromValue(jsonFundingExternalIdentifier.getType().toLowerCase()));
                } else {
                    fundingExternalIdentifier.setType(FundingExternalIdentifierType.GRANT_NUMBER);
                }
            } catch (IllegalArgumentException e) {
                // Fallback to GRANT_NUMBER on mapping failure
                fundingExternalIdentifier.setType(FundingExternalIdentifierType.GRANT_NUMBER);
            }
            
            if (jsonFundingExternalIdentifier.getUrl() != null && jsonFundingExternalIdentifier.getUrl().getValue() != null) {
                org.orcid.jaxb.model.message.Url messageUrl = new org.orcid.jaxb.model.message.Url();
                messageUrl.setValue(jsonFundingExternalIdentifier.getUrl().getValue());
                fundingExternalIdentifier.setUrl(messageUrl);
            }
            
            fundingExternalIdentifier.setValue(jsonFundingExternalIdentifier.getValue());
            fundingExternalIdentifiers.getFundingExternalIdentifier().add(fundingExternalIdentifier);
        }
        
        return fundingExternalIdentifiers;
    }
}