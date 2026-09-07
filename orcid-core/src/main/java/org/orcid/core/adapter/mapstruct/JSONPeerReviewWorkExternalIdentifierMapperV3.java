package org.orcid.core.adapter.mapstruct;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.orcid.core.adapter.mapstruct.jsonidentifier.JSONUrl;
import org.orcid.core.adapter.mapstruct.jsonidentifier.JSONWorkExternalIdentifier;
import org.orcid.core.adapter.mapstruct.jsonidentifier.JSONWorkExternalIdentifier.WorkExternalIdentifierId;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.pojo.ajaxForm.PojoUtil;

/**
 * Custom MapStruct Mapper for converting V3 Peer Review ExternalIDs to and from JSON.
 */
@Mapper(componentModel = "spring")
public abstract class JSONPeerReviewWorkExternalIdentifierMapperV3 {

    // Automatically inject the thread-safe MapStruct type mapper
    @Autowired
    protected ExternalIdentifierTypeMapper typeMapper;

    /**
     * Converts the JAXB V3 ExternalID object into a JSON String for the database.
     */
    public String convertTo(ExternalID source) {
        if (source == null) {
            return null;
        }

        JSONWorkExternalIdentifier jsonWorkExternalIdentifier = new JSONWorkExternalIdentifier();
        
        if (source.getType() != null) {
            // Route through the injected MapStruct typeMapper
            jsonWorkExternalIdentifier.setWorkExternalIdentifierType(typeMapper.convertTo(source.getType()));
        }
        
        if (source.getUrl() != null && source.getUrl().getValue() != null) {
            jsonWorkExternalIdentifier.setUrl(new JSONUrl(source.getUrl().getValue()));
        }
        
        if (!PojoUtil.isEmpty(source.getValue())) {
            jsonWorkExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(source.getValue()));
        }
        
        if (source.getRelationship() != null) {
            // Route relationship translation through typeMapper
            jsonWorkExternalIdentifier.setRelationship(typeMapper.convertTo(source.getRelationship().value()));
        }
        
        return JsonUtils.convertToJsonString(jsonWorkExternalIdentifier);
    }

    /**
     * Converts the database JSON String back into the JAXB V3 ExternalID object.
     */
    public ExternalID convertFrom(String source) {
        if (StringUtils.isBlank(source)) {
            return null;
        }

        JSONWorkExternalIdentifier workExternalIdentifier = JsonUtils.readObjectFromJsonString(source, JSONWorkExternalIdentifier.class);
        if (workExternalIdentifier == null) {
            return null;
        }

        ExternalID id = new ExternalID();
        
        if (workExternalIdentifier.getWorkExternalIdentifierType() == null) {
            id.setType(WorkExternalIdentifierType.OTHER_ID.value());
        } else {
            // Re-engages the typeMapper for relationship resolution
            id.setType(typeMapper.convertFrom(workExternalIdentifier.getWorkExternalIdentifierType()));
        }
        
        if (workExternalIdentifier.getWorkExternalIdentifierId() != null) {
            id.setValue(workExternalIdentifier.getWorkExternalIdentifierId().content);
        }
        
        if (workExternalIdentifier.getUrl() != null && workExternalIdentifier.getUrl().getValue() != null) {
            id.setUrl(new Url(workExternalIdentifier.getUrl().getValue()));
        }
        
        if (workExternalIdentifier.getRelationship() != null) {
            String rel = typeMapper.convertFrom(workExternalIdentifier.getRelationship());
            id.setRelationship(Relationship.fromValue(rel));
        }
        
        return id;
    }
}
