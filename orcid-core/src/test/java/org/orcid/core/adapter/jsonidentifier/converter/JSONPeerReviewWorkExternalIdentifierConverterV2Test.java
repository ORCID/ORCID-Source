package org.orcid.core.adapter.jsonidentifier.converter;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.adapter.jsonidentifier.JSONUrl;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier.WorkExternalIdentifierId;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.OrcidJUnit4ClassRunner;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

@RunWith(OrcidJUnit4ClassRunner.class)
@Ignore
public class JSONPeerReviewWorkExternalIdentifierConverterV2Test {

    private ExternalIdentifierTypeConverter conv = new ExternalIdentifierTypeConverter();

    @Test
    public void testConvertTo(ExternalID source, Type<String> destinationType) {
//        JSONWorkExternalIdentifier jsonWorkExternalIdentifier = new JSONWorkExternalIdentifier();
//        if (source.getType() != null) {
//            jsonWorkExternalIdentifier.setWorkExternalIdentifierType(source.getType());
//        }
//        if (source.getUrl() != null) {
//            jsonWorkExternalIdentifier.setUrl(new JSONUrl(source.getUrl().getValue()));
//        }
//        if (!PojoUtil.isEmpty(source.getValue())) {
//            jsonWorkExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(source.getValue()));
//        }
//        if (source.getRelationship() != null) {
//            jsonWorkExternalIdentifier.setRelationship(source.getRelationship().value());
//        }
//        return JsonUtils.convertToJsonString(jsonWorkExternalIdentifier);
    }

    @Test
    public void testConvertFrom(String source, Type<ExternalID> destinationType) {
//        JSONWorkExternalIdentifier workExternalIdentifier = JsonUtils.readObjectFromJsonString(source, JSONWorkExternalIdentifier.class);
//        ExternalID id = new ExternalID();
//        if (workExternalIdentifier.getWorkExternalIdentifierType() == null) {
//            id.setType(WorkExternalIdentifierType.OTHER_ID.value());
//        } else {
//            id.setType(conv.convertFrom(workExternalIdentifier.getWorkExternalIdentifierType(), null));
//        }
//        if (workExternalIdentifier.getWorkExternalIdentifierId() != null) {
//            id.setValue(workExternalIdentifier.getWorkExternalIdentifierId().content);
//        }
//        if (workExternalIdentifier.getUrl() != null) {
//            id.setUrl(new Url(workExternalIdentifier.getUrl().getValue()));
//        }
//        if (workExternalIdentifier.getRelationship() != null) {
//            id.setRelationship(Relationship.fromValue(conv.convertFrom(workExternalIdentifier.getRelationship(), null)));
//        }
//        return id;
    }

}
