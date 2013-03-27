/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.forms;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;

public class CurrentWorkExternalId {

    private String id;

    private String type;

    public CurrentWorkExternalId() {
    }

    public CurrentWorkExternalId(WorkExternalIdentifier workExternalIdentifier) {
        id = workExternalIdentifier.getWorkExternalIdentifierId().getContent();
        type = workExternalIdentifier.getWorkExternalIdentifierType().value();
    }

    public WorkExternalIdentifier getWorkExternalIdentifier() {
        WorkExternalIdentifier workExternalIdentifier = new WorkExternalIdentifier();
        workExternalIdentifier.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(type));
        workExternalIdentifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(id));
        return workExternalIdentifier;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isBlank() {
        return (StringUtils.isBlank(id) && StringUtils.isBlank(type));
    }

    public boolean isValid() {
        try {
            WorkExternalIdentifierType.fromValue(type);
        } catch (IllegalArgumentException e) {
            // ignore, we know that this is not valid
            return false;
        }
        return (StringUtils.isNotBlank(id) && StringUtils.isNotBlank(type));
    }
}
