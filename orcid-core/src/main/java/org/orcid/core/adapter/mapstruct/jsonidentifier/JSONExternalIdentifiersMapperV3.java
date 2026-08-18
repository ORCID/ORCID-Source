package org.orcid.core.adapter.mapstruct.jsonidentifier;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.jsonidentifier.JSONExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONExternalIdentifiers;
import org.orcid.core.adapter.jsonidentifier.JSONUrl;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.message.FundingExternalIdentifierType;
import org.orcid.jaxb.model.v3.release.common.TransientError;
import org.orcid.jaxb.model.v3.release.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.pojo.ajaxForm.PojoUtil;

/**
 * Custom MapStruct Mapper for converting general V3 ExternalIDs to and from JSON, 
 * including PID normalization and localized error handling.
 */
@Mapper(componentModel = "spring")
public abstract class JSONExternalIdentifiersMapperV3 {

    @Autowired
    protected ExternalIdentifierTypeMapper typeMapper;

    @Autowired
    protected PIDNormalizationService norm;

    @Autowired
    protected LocaleManager localeManager;

    /**
     * Converts the JAXB V3 ExternalIDs object into a JSON String for the database.
     */
    public String convertTo(ExternalIDs source) {
        if (source == null || source.getExternalIdentifier() == null) {
            return null;
        }

        JSONExternalIdentifiers jsonExternalIdentifiers = new JSONExternalIdentifiers();
        
        for (ExternalID externalID : source.getExternalIdentifier()) {
            JSONExternalIdentifier jsonExternalIdentifier = new JSONExternalIdentifier();
            
            if (externalID.getType() != null) {
                jsonExternalIdentifier.setType(typeMapper.convertTo(externalID.getType()));
            }

            if (externalID.getUrl() != null && externalID.getUrl().getValue() != null) {
                jsonExternalIdentifier.setUrl(new JSONUrl(externalID.getUrl().getValue()));
            }

            if (!PojoUtil.isEmpty(externalID.getValue())) {
                jsonExternalIdentifier.setValue(externalID.getValue());
            }

            if (externalID.getRelationship() != null) {
                jsonExternalIdentifier.setRelationship(typeMapper.convertTo(externalID.getRelationship().value()));
            }
            
            jsonExternalIdentifiers.getExternalIdentifier().add(jsonExternalIdentifier);
        }
        
        return JsonUtils.convertToJsonString(jsonExternalIdentifiers);
    }

    /**
     * Converts the database JSON String back into the JAXB V3 ExternalIDs object.
     */
    public ExternalIDs convertFrom(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        JSONExternalIdentifiers externalIdentifiers = JsonUtils.readObjectFromJsonString(source, JSONExternalIdentifiers.class);
        if (externalIdentifiers == null || externalIdentifiers.getExternalIdentifier() == null) {
            return null;
        }

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
                String normalised = norm.normalise(id.getType(), externalIdentifier.getValue());
                
                if (normalised != null && !normalised.trim().isEmpty()) {
                    id.setNormalized(new TransientNonEmptyString(normalised));
                }
                
                if (StringUtils.isBlank(normalised)) {
                    id.setNormalizedError(new TransientError(
                            localeManager.resolveMessage("transientError.normalization_failed.code"),
                            localeManager.resolveMessage("transientError.normalization_failed.message", id.getType(), externalIdentifier.getValue())
                    ));
                }
            }
            
            if (externalIdentifier.getRelationship() != null) {
                String rel = typeMapper.convertFrom(externalIdentifier.getRelationship());
                id.setRelationship(Relationship.fromValue(rel));
            }
            
            externalIDs.getExternalIdentifier().add(id);
        }
        
        return externalIDs;
    }
}
