package org.orcid.core.adapter.jsonidentifier.converter;

import org.orcid.core.adapter.jsonidentifier.JSONUrl;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier.WorkExternalIdentifierId;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.pojo.ajaxForm.PojoUtil;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class JSONPeerReviewWorkExternalIdentifierConverterV3 extends BidirectionalConverter<ExternalID, String> {

    private ExternalIdentifierTypeConverter conv = new ExternalIdentifierTypeConverter();

    @Override
    public String convertTo(ExternalID source, Type<String> destinationType) {
        JSONWorkExternalIdentifier jsonWorkExternalIdentifier = new JSONWorkExternalIdentifier();
        if (source.getType() != null) {
            jsonWorkExternalIdentifier.setWorkExternalIdentifierType(conv.convertTo(source.getType(), null));
        }
        if (source.getUrl() != null) {
            jsonWorkExternalIdentifier.setUrl(new JSONUrl(source.getUrl().getValue()));
        }
        if (!PojoUtil.isEmpty(source.getValue())) {
            jsonWorkExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(source.getValue()));
        }
        if (source.getRelationship() != null) {
            jsonWorkExternalIdentifier.setRelationship(conv.convertTo(source.getRelationship().value(), null));
        }
        return JsonUtils.convertToJsonString(jsonWorkExternalIdentifier);
    }

    @Override
    public ExternalID convertFrom(String source, Type<ExternalID> destinationType) {
        JSONWorkExternalIdentifier workExternalIdentifier = JsonUtils.readObjectFromJsonString(source, JSONWorkExternalIdentifier.class);
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
        return id;
    }

}
