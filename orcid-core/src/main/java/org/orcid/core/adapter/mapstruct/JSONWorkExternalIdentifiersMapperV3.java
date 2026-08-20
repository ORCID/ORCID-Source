package org.orcid.core.adapter.mapstruct;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;

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

/**
 * Custom MapStruct Mapper for converting V3 ExternalIDs to and from JSON, 
 * including PID normalization logic.
 */
@Mapper(componentModel = "spring")
public abstract class JSONWorkExternalIdentifiersMapperV3 {

    @Autowired
    protected ExternalIdentifierTypeMapper typeMapper;

    @Autowired
    protected PIDNormalizationService norm;

    @Autowired
    protected PIDResolverService resolverService;

    @Autowired
    protected LocaleManager localeManager;

    protected String normalizationFailedErrorCode;
    protected String normalizationFailedErrorDescription;

    /**
     * Initializes the localized error messages once during Spring startup.
     */
    @PostConstruct
    public void init() {
        // API errors are not localized
        this.normalizationFailedErrorCode = localeManager.resolveMessage("transientError.normalization_failed.code");
        this.normalizationFailedErrorDescription = localeManager.resolveMessage("transientError.normalization_failed.message");
    }

    /**
     * Converts the JAXB V3 ExternalIDs object into a JSON String for the database.
     */
    public String convertTo(ExternalIDs source) {
        if (source == null || source.getExternalIdentifier() == null) {
            return null;
        }

        JSONWorkExternalIdentifiers jsonWorkExternalIdentifiers = new JSONWorkExternalIdentifiers();
        
        for (ExternalID externalID : source.getExternalIdentifier()) {
            JSONWorkExternalIdentifier jsonWorkExternalIdentifier = new JSONWorkExternalIdentifier();
            
            if (externalID.getType() != null) {
                jsonWorkExternalIdentifier.setWorkExternalIdentifierType(typeMapper.convertTo(externalID.getType()));
            }

            if (externalID.getUrl() != null && externalID.getUrl().getValue() != null) {
                jsonWorkExternalIdentifier.setUrl(new JSONUrl(externalID.getUrl().getValue()));
            }

            if (!PojoUtil.isEmpty(externalID.getValue())) {
                jsonWorkExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(externalID.getValue()));
            }

            if (externalID.getRelationship() != null) {
                jsonWorkExternalIdentifier.setRelationship(typeMapper.convertTo(externalID.getRelationship().value()));
            }
            
            jsonWorkExternalIdentifiers.getWorkExternalIdentifier().add(jsonWorkExternalIdentifier);
        }
        
        return JsonUtils.convertToJsonString(jsonWorkExternalIdentifiers);
    }

    /**
     * Converts the database JSON String back into the JAXB V3 ExternalIDs object,
     * applying PID normalization and validation.
     */
    public ExternalIDs convertFrom(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        JSONWorkExternalIdentifiers workExternalIdentifiers = JsonUtils.readObjectFromJsonString(source, JSONWorkExternalIdentifiers.class);
        if (workExternalIdentifiers == null || workExternalIdentifiers.getWorkExternalIdentifier() == null) {
            return null;
        }

        ExternalIDs externalIDs = new ExternalIDs();
        
        for (JSONWorkExternalIdentifier workExternalIdentifier : workExternalIdentifiers.getWorkExternalIdentifier()) {
            ExternalID id = new ExternalID();
            
            if (workExternalIdentifier.getWorkExternalIdentifierType() == null) {
                id.setType(WorkExternalIdentifierType.OTHER_ID.value());
            } else {
                id.setType(typeMapper.convertTo(workExternalIdentifier.getWorkExternalIdentifierType()));
            }
            
            if (workExternalIdentifier.getWorkExternalIdentifierId() != null) {
                id.setValue(workExternalIdentifier.getWorkExternalIdentifierId().content);
                
                // note, uses API type name.
                String normalised = norm.normalise(id.getType(), workExternalIdentifier.getWorkExternalIdentifierId().content);
                if (normalised != null && !normalised.trim().isEmpty()) {
                    id.setNormalized(new TransientNonEmptyString(normalised));
                } else {
                    id.setNormalizedError(new TransientError(
                            normalizationFailedErrorCode, 
                            normalizationFailedErrorDescription
                                    .replace("{0}", id.getType() != null ? id.getType() : "")
                                    .replace("{1}", workExternalIdentifier.getWorkExternalIdentifierId().content)
                    ));
                }
                
                if (workExternalIdentifier.getUrl() != null && workExternalIdentifier.getUrl().getValue() != null) {
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
                
                if (id.getNormalizedUrl() == null || StringUtils.isEmpty(id.getNormalizedUrl().getValue())) {
                    id.setNormalizedUrlError(new TransientError(
                            normalizationFailedErrorCode, 
                            normalizationFailedErrorDescription
                                    .replace("{0}", id.getType() != null ? id.getType() : "")
                                    .replace("{1}", workExternalIdentifier.getWorkExternalIdentifierId().content)
                    ));
                }                
            }
            
            if (workExternalIdentifier.getUrl() != null && workExternalIdentifier.getUrl().getValue() != null) {
                id.setUrl(new Url(workExternalIdentifier.getUrl().getValue()));
            }
            
            if (workExternalIdentifier.getRelationship() != null) {
                id.setRelationship(Relationship.fromValue(typeMapper.convertTo(workExternalIdentifier.getRelationship())));
            }
            
            externalIDs.getExternalIdentifier().add(id);
        }
        
        return externalIDs;
    }
}
