package org.orcid.core.adapter.jsonidentifier.converter;

import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifier.WorkExternalIdentifierId;
import org.orcid.core.adapter.jsonidentifier.JSONWorkExternalIdentifiers;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class JSONWorkExternalIdentifiersConverterV1 {

    private ExternalIdentifierTypeConverter conv = new ExternalIdentifierTypeConverter();

    public String convertTo(WorkExternalIdentifiers messagePojo, WorkType workType) {
        JSONWorkExternalIdentifiers workExternalIdentifiers = new JSONWorkExternalIdentifiers();
        for (WorkExternalIdentifier workExternalIdentifier : messagePojo.getWorkExternalIdentifier()) {
            JSONWorkExternalIdentifier jsonWorkExternalIdentifier = new JSONWorkExternalIdentifier();
            if (workExternalIdentifier.getWorkExternalIdentifierType() != null) {
                jsonWorkExternalIdentifier.setWorkExternalIdentifierType(workExternalIdentifier.getWorkExternalIdentifierType().value());
            }
            if (workExternalIdentifier.getWorkExternalIdentifierId() != null && !PojoUtil.isEmpty(workExternalIdentifier.getWorkExternalIdentifierId().getContent())) {
                jsonWorkExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(workExternalIdentifier.getWorkExternalIdentifierId().getContent()));
            }
            WorkExternalIdentifierType type = WorkExternalIdentifierType.valueOf(conv.convertTo(jsonWorkExternalIdentifier.getWorkExternalIdentifierType(), null));

            if (org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISSN.equals(type)) {
                if (!workType.equals(org.orcid.jaxb.model.message.WorkType.BOOK)) {
                    jsonWorkExternalIdentifier.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.PART_OF.value());
                } else {
                    jsonWorkExternalIdentifier.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.SELF.value());
                }
            } else if (org.orcid.jaxb.model.message.WorkExternalIdentifierType.ISBN.equals(type)) {
                if (workType.equals(org.orcid.jaxb.model.message.WorkType.BOOK_CHAPTER) || workType.equals(org.orcid.jaxb.model.message.WorkType.CONFERENCE_PAPER)) {
                    jsonWorkExternalIdentifier.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.PART_OF.value());
                } else {
                    jsonWorkExternalIdentifier.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.SELF.value());
                }
            } else {
                jsonWorkExternalIdentifier.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.SELF.value());
            }
            workExternalIdentifiers.getWorkExternalIdentifier().add(jsonWorkExternalIdentifier);
        }
        return JsonUtils.convertToJsonString(workExternalIdentifiers);
    }

    public WorkExternalIdentifiers convertFrom(String source) {
        JSONWorkExternalIdentifiers jsonWorkExternalIdentifiers = JsonUtils.readObjectFromJsonString(source, JSONWorkExternalIdentifiers.class);
        WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
        for (JSONWorkExternalIdentifier jsonWorkExternalIdentifier : jsonWorkExternalIdentifiers.getWorkExternalIdentifier()) {
            WorkExternalIdentifier workExternalIdentifier = new WorkExternalIdentifier();
            try {
                workExternalIdentifier.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(conv.convertFrom(jsonWorkExternalIdentifier.getWorkExternalIdentifierType(), null)));
            } catch (Exception e) {
                workExternalIdentifier.setWorkExternalIdentifierType(WorkExternalIdentifierType.OTHER_ID);
            }
            workExternalIdentifier.setWorkExternalIdentifierId(new org.orcid.jaxb.model.message.WorkExternalIdentifierId());
            if (jsonWorkExternalIdentifier.getWorkExternalIdentifierId() != null) {
                workExternalIdentifier.getWorkExternalIdentifierId().setContent(jsonWorkExternalIdentifier.getWorkExternalIdentifierId().content);
            }
            workExternalIdentifiers.getWorkExternalIdentifier().add(workExternalIdentifier);
        }
        return workExternalIdentifiers;
    }

}
