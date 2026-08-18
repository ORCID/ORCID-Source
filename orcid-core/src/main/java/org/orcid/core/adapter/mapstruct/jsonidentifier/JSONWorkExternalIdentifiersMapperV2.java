package org.orcid.core.adapter.mapstruct.jsonidentifier;

import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.orcid.core.adapter.jsonidentifier.JSONUrl;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier.WorkExternalIdentifierId;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifiers;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.pojo.ajaxForm.PojoUtil;

/**
 * Custom MapStruct Mapper for converting V2 ExternalIDs to and from JSON.
 */
@Mapper(componentModel = "spring")
public abstract class JSONWorkExternalIdentifiersMapperV2 {

    @Autowired
    protected ExternalIdentifierTypeMapper typeMapper;

    /**
     * Converts the JAXB ExternalIDs object into a JSON String for the database.
     */
    public String convertTo(ExternalIDs source) {
        if (source == null || source.getExternalIdentifier() == null) {
            return null;
        }

        JSONWorkExternalIdentifiers jsonWorkExternalIdentifiers = new JSONWorkExternalIdentifiers();
        
        for (ExternalID externalID : source.getExternalIdentifier()) {
            JSONWorkExternalIdentifier jsonWorkExternalIdentifier = new JSONWorkExternalIdentifier();
            
            if (externalID.getType() != null) {
                // Route through the injected MapStruct typeMapper instead of Orika
                jsonWorkExternalIdentifier.setWorkExternalIdentifierType(typeMapper.convertTo(externalID.getType()));
            }

            if (externalID.getUrl() != null && externalID.getUrl().getValue() != null) {
                jsonWorkExternalIdentifier.setUrl(new JSONUrl(externalID.getUrl().getValue()));
            }

            if (!PojoUtil.isEmpty(externalID.getValue())) {
                jsonWorkExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(externalID.getValue()));
            }

            if (externalID.getRelationship() != null) {
                // Route relationship translation through typeMapper
                jsonWorkExternalIdentifier.setRelationship(typeMapper.convertTo(externalID.getRelationship().value()));
            }
            
            jsonWorkExternalIdentifiers.getWorkExternalIdentifier().add(jsonWorkExternalIdentifier);
        }
        
        return JsonUtils.convertToJsonString(jsonWorkExternalIdentifiers);
    }

    /**
     * Converts the database JSON String back into the JAXB ExternalIDs object.
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
            // Filter out VERSION_OF relationships
            if (workExternalIdentifier.getRelationship() == null || !org.orcid.jaxb.model.common.Relationship.VERSION_OF.name().equals(workExternalIdentifier.getRelationship())) {
                ExternalID id = new ExternalID();
                
                if (workExternalIdentifier.getWorkExternalIdentifierType() == null) {
                    id.setType(WorkExternalIdentifierType.OTHER_ID.value());
                } else {
                    id.setType(typeMapper.convertTo(workExternalIdentifier.getWorkExternalIdentifierType()));
                }
                
                if (workExternalIdentifier.getWorkExternalIdentifierId() != null) {
                    id.setValue(workExternalIdentifier.getWorkExternalIdentifierId().content);
                } 
                
                if (workExternalIdentifier.getUrl() != null && workExternalIdentifier.getUrl().getValue() != null) {
                    id.setUrl(new Url(workExternalIdentifier.getUrl().getValue()));
                }
                
                if (workExternalIdentifier.getRelationship() != null) {
                    String rel = typeMapper.convertTo(workExternalIdentifier.getRelationship());
                    id.setRelationship(Relationship.fromValue(rel));
                }
                
                externalIDs.getExternalIdentifier().add(id);
            }                        
        }
        
        return externalIDs;
    }
}