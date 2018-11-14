package org.orcid.core.adapter.jsonidentifier.converter;

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

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class JSONWorkExternalIdentifiersConverterV2 extends BidirectionalConverter<ExternalIDs, String> {
    
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
            if(workExternalIdentifier.getRelationship() == null || !org.orcid.jaxb.model.v3.rc2.record.Relationship.VERSION_OF.name().equals(workExternalIdentifier.getRelationship())) {
                ExternalID id = new ExternalID();
                if (workExternalIdentifier.getWorkExternalIdentifierType() == null) {
                    id.setType(WorkExternalIdentifierType.OTHER_ID.value());
                } else {
                    id.setType(conv.convertFrom(workExternalIdentifier.getWorkExternalIdentifierType(), null));
                }
                if (workExternalIdentifier.getWorkExternalIdentifierId() != null) {
                    id.setValue(workExternalIdentifier.getWorkExternalIdentifierId().content);
                } 
                if (workExternalIdentifier.getUrl() != null) {
                    id.setUrl(new Url(workExternalIdentifier.getUrl().getValue()));
                }
                if (workExternalIdentifier.getRelationship() != null) {
                    id.setRelationship(Relationship.fromValue(conv.convertFrom(workExternalIdentifier.getRelationship(), null)));
                }
                externalIDs.getExternalIdentifier().add(id);
            }                        
        }
        return externalIDs;
    }

}
