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
package org.orcid.core.adapter.v3.impl.jsonidentifiers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.ExternalIDs;

public class EducationExternalIdentifiers implements Serializable, JSONIdentifierAdapter<ExternalIDs> {

    private static final long serialVersionUID = 1L;
    protected List<EducationExternalIdentifier> educationExternalIdentifier;

    public EducationExternalIdentifiers() {
    }

    public EducationExternalIdentifiers(ExternalIDs recordPojo) {
        if (recordPojo != null && !recordPojo.getExternalIdentifier().isEmpty()) {
            for (ExternalID recordEi : recordPojo.getExternalIdentifier()) {
                this.getEducationExternalIdentifier().add(new EducationExternalIdentifier(recordEi));
            }
        }
    }

    public List<EducationExternalIdentifier> getEducationExternalIdentifier() {
        if (educationExternalIdentifier == null)
            educationExternalIdentifier = new ArrayList<EducationExternalIdentifier>();
        return educationExternalIdentifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((educationExternalIdentifier == null) ? 0 : educationExternalIdentifier.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EducationExternalIdentifiers other = (EducationExternalIdentifiers) obj;
        if (educationExternalIdentifier == null) {
            if (other.educationExternalIdentifier != null)
                return false;
        } else {
            if (other.educationExternalIdentifier == null)
                return false;
            else if (!(educationExternalIdentifier.containsAll(other.educationExternalIdentifier) && other.educationExternalIdentifier.containsAll(educationExternalIdentifier)
                    && other.educationExternalIdentifier.size() == educationExternalIdentifier.size())) {
                return false;
            }
        }
        return true;
    }

    public ExternalIDs toRecordPojo() {
        ExternalIDs result = new ExternalIDs();
        if (!this.getEducationExternalIdentifier().isEmpty()) {
            for (EducationExternalIdentifier fei : this.getEducationExternalIdentifier()) {
                result.getExternalIdentifier().add(fei.toRecordPojo());
            }
        }
        return result;
    }

    public String toDBJSONString() {
        return JsonUtils.convertToJsonString(this);
    }

    public static EducationExternalIdentifiers fromDBJSONString(String dbJSON) {
        return JsonUtils.readObjectFromJsonString(dbJSON, EducationExternalIdentifiers.class);
    }

}
