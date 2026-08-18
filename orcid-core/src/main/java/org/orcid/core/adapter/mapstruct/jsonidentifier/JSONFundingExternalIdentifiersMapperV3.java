package org.orcid.core.adapter.mapstruct.jsonidentifier;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.jsonidentifier.JSONExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONFundingExternalIdentifiers;
import org.orcid.core.adapter.jsonidentifier.JSONUrl;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.pojo.ajaxForm.PojoUtil;

/**
 * Custom MapStruct Mapper for converting V3 FundingExternalIdentifiers to and from JSON.
 */
@Mapper(componentModel = "spring")
public abstract class JSONFundingExternalIdentifiersMapperV3 {

    // Automatically inject the thread-safe MapStruct type mapper
    @Autowired
    protected ExternalIdentifierTypeMapper typeMapper;

    /**
     * Converts the JAXB V3 ExternalIDs object into a JSON String for the database.
     */
    public String convertToDb(ExternalIDs source) {
        if (source == null || source.getExternalIdentifier() == null) {
            return null;
        }

        JSONFundingExternalIdentifiers jsonFundingExternalIdentifiers = new JSONFundingExternalIdentifiers();
        
        for (ExternalID externalID : source.getExternalIdentifier()) {
            JSONExternalIdentifier jsonExternalIdentifier = new JSONExternalIdentifier();
            
            if (externalID.getType() != null) {
                // Route through the injected MapStruct typeMapper
                jsonExternalIdentifier.setType(typeMapper.convertTo(externalID.getType()));
            }

            if (externalID.getUrl() != null && externalID.getUrl().getValue() != null) {
                jsonExternalIdentifier.setUrl(new JSONUrl(externalID.getUrl().getValue()));
            }

            if (!PojoUtil.isEmpty(externalID.getValue())) {
                jsonExternalIdentifier.setValue(externalID.getValue());
            }

            if (externalID.getRelationship() != null) {
                // Route relationship translation through typeMapper
                jsonExternalIdentifier.setRelationship(typeMapper.convertTo(externalID.getRelationship().value()));
            }
            
            jsonFundingExternalIdentifiers.getFundingExternalIdentifier().add(jsonExternalIdentifier);
        }
        
        return JsonUtils.convertToJsonString(jsonFundingExternalIdentifiers);
    }

    /**
     * Converts the database JSON String back into the JAXB V3 ExternalIDs object.
     */
    public ExternalIDs convertFrom(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        JSONFundingExternalIdentifiers fundingExternalIdentifiers = JsonUtils.readObjectFromJsonString(source, JSONFundingExternalIdentifiers.class);
        if (fundingExternalIdentifiers == null || fundingExternalIdentifiers.getFundingExternalIdentifier() == null) {
            return null;
        }

        ExternalIDs externalIDs = new ExternalIDs();
        
        for (JSONExternalIdentifier externalIdentifier : fundingExternalIdentifiers.getFundingExternalIdentifier()) {
            ExternalID id = new ExternalID();
            
            if (externalIdentifier.getType() == null) {
                id.setType(FundingExternalIdentifierType.GRANT_NUMBER.value());
            } else {
                // Maintained original logic: bypass typeMapper and lower-case directly
                id.setType(externalIdentifier.getType().toLowerCase());
            }
            
            if (externalIdentifier.getUrl() != null && !PojoUtil.isEmpty(externalIdentifier.getUrl().getValue())) {
                Url url = new Url(externalIdentifier.getUrl().getValue());
                id.setUrl(url);
            }

            if (!PojoUtil.isEmpty(externalIdentifier.getValue())) {
                id.setValue(externalIdentifier.getValue());
            }

            if (externalIdentifier.getRelationship() != null) {
                // Re-engages the typeMapper for relationship resolution
                String rel = typeMapper.convertFrom(externalIdentifier.getRelationship());
                id.setRelationship(Relationship.fromValue(rel));
            }
            
            externalIDs.getExternalIdentifier().add(id);
        }
        
        return externalIDs;
    }
}
