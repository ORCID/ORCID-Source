/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.adapter.impl.jsonidentifiers;

import java.io.Serializable;

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.pojo.ajaxForm.PojoUtil;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class used to serialise to/from JSON DB representation
 * 
 * @author tom
 *
 */
public class WorkExternalIdentifier implements Serializable, JSONIdentifierAdapter<org.orcid.jaxb.model.message.WorkExternalIdentifier, ExternalID> {

    private static final long serialVersionUID = 1L;

    // modeled as an RC1 external identifier for DB serialization
    // test by creating this and a RC1 version and checking they serialise to
    // the same JSON
    @JsonProperty("relationship")
    protected String relationship;
    @JsonProperty("url")
    protected Url url;
    @JsonProperty("workExternalIdentifierType")
    protected String workExternalIdentifierType;
    @JsonProperty("workExternalIdentifierId")
    protected WorkExternalIdentifierId workExternalIdentifierId;

    public class WorkExternalIdentifierId implements Serializable {
        private static final long serialVersionUID = 1L;
        public String content;

        public WorkExternalIdentifierId() {
        }

        public WorkExternalIdentifierId(String value) {
            this.content = value;
        }
    }

    @JsonIgnore
    private ExternalIdentifierTypeConverter conv = new ExternalIdentifierTypeConverter();

    public WorkExternalIdentifier() {

    }

    public WorkExternalIdentifier(org.orcid.jaxb.model.message.WorkExternalIdentifier messagePojo) {
        if (messagePojo.getWorkExternalIdentifierType() != null) {
            this.setWorkExternalIdentifierType(messagePojo.getWorkExternalIdentifierType().value());
        }
        if (messagePojo.getWorkExternalIdentifierId() != null && !PojoUtil.isEmpty(messagePojo.getWorkExternalIdentifierId().getContent())) {
            this.setWorkExternalIdentifierId(new WorkExternalIdentifierId(messagePojo.getWorkExternalIdentifierId().getContent()));
        }
    }

    public WorkExternalIdentifier(ExternalID recordPojo) {
        if (recordPojo.getType() != null) {
            this.setWorkExternalIdentifierType(recordPojo.getType());
        }

        if (recordPojo.getUrl() != null) {
            this.setUrl(new Url(recordPojo.getUrl().getValue()));
        }

        if (!PojoUtil.isEmpty(recordPojo.getValue())) {
            this.setWorkExternalIdentifierId(new WorkExternalIdentifierId(recordPojo.getValue()));
        }

        if (recordPojo.getRelationship() != null) {
            this.setRelationship(recordPojo.getRelationship().value());
        }
    }

    public org.orcid.jaxb.model.message.WorkExternalIdentifier toMessagePojo() {
        org.orcid.jaxb.model.message.WorkExternalIdentifier messagePojo = new org.orcid.jaxb.model.message.WorkExternalIdentifier();
        try {
            messagePojo.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(conv.convertFrom(this.getWorkExternalIdentifierType(), null)));
        } catch (Exception e) {
            messagePojo.setWorkExternalIdentifierType(WorkExternalIdentifierType.OTHER_ID);
        }
        messagePojo.setWorkExternalIdentifierId(new org.orcid.jaxb.model.message.WorkExternalIdentifierId());
        if (this.getWorkExternalIdentifierId() != null)
            messagePojo.getWorkExternalIdentifierId().setContent(this.getWorkExternalIdentifierId().content);
        return messagePojo;
    }

    public ExternalID toRecordPojo() {
        ExternalID id = new ExternalID();
        if (this.getWorkExternalIdentifierType() == null)
            id.setType(WorkExternalIdentifierType.OTHER_ID.value());
        else
            id.setType(conv.convertFrom(this.getWorkExternalIdentifierType(), null));
        if (this.getWorkExternalIdentifierId() != null)
            id.setValue(this.getWorkExternalIdentifierId().content);
        if (this.url != null)
            id.setUrl(new org.orcid.jaxb.model.common_v2.Url(this.getUrl().value));
        if (this.getRelationship() != null)
            id.setRelationship(Relationship.fromValue(conv.convertFrom(this.getRelationship(), null)));
        return id;
    }

    public String toDBJSONString() {
        return JsonUtils.convertToJsonString(this);
    }

    public static WorkExternalIdentifier fromDBJSONString(String dbJSON) {
        return JsonUtils.readObjectFromJsonString(dbJSON, WorkExternalIdentifier.class);
    }

    public String getWorkExternalIdentifierType() {
        return workExternalIdentifierType;
    }

    public void setWorkExternalIdentifierType(String workExternalIdentifierType) {
        if (workExternalIdentifierType != null)
            this.workExternalIdentifierType = conv.convertTo(workExternalIdentifierType, null);
    }

    public WorkExternalIdentifierId getWorkExternalIdentifierId() {
        return workExternalIdentifierId;
    }

    public void setWorkExternalIdentifierId(WorkExternalIdentifierId workExternalIdentifierId) {
        this.workExternalIdentifierId = workExternalIdentifierId;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        if (relationship != null)
            this.relationship = conv.convertTo(relationship, null);
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

}
