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

public class EmploymentExternalIdentifiers implements Serializable, JSONIdentifierAdapter<ExternalIDs> {

    private static final long serialVersionUID = 1L;
    protected List<EmploymentExternalIdentifier> employmentExternalIdentifier;

    public EmploymentExternalIdentifiers() {
    }

    public EmploymentExternalIdentifiers(ExternalIDs recordPojo) {
        if (recordPojo != null && !recordPojo.getExternalIdentifier().isEmpty()) {
            for (ExternalID recordEi : recordPojo.getExternalIdentifier()) {
                this.getEmploymentExternalIdentifier().add(new EmploymentExternalIdentifier(recordEi));
            }
        }
    }

    public List<EmploymentExternalIdentifier> getEmploymentExternalIdentifier() {
        if (employmentExternalIdentifier == null)
            employmentExternalIdentifier = new ArrayList<EmploymentExternalIdentifier>();
        return employmentExternalIdentifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((employmentExternalIdentifier == null) ? 0 : employmentExternalIdentifier.hashCode());
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
        EmploymentExternalIdentifiers other = (EmploymentExternalIdentifiers) obj;
        if (employmentExternalIdentifier == null) {
            if (other.employmentExternalIdentifier != null)
                return false;
        } else {
            if (other.employmentExternalIdentifier == null)
                return false;
            else if (!(employmentExternalIdentifier.containsAll(other.employmentExternalIdentifier) && other.employmentExternalIdentifier.containsAll(employmentExternalIdentifier)
                    && other.employmentExternalIdentifier.size() == employmentExternalIdentifier.size())) {
                return false;
            }
        }
        return true;
    }

    public ExternalIDs toRecordPojo() {
        ExternalIDs result = new ExternalIDs();
        if (!this.getEmploymentExternalIdentifier().isEmpty()) {
            for (EmploymentExternalIdentifier fei : this.getEmploymentExternalIdentifier()) {
                result.getExternalIdentifier().add(fei.toRecordPojo());
            }
        }
        return result;
    }

    public String toDBJSONString() {
        return JsonUtils.convertToJsonString(this);
    }

    public static EmploymentExternalIdentifiers fromDBJSONString(String dbJSON) {
        return JsonUtils.readObjectFromJsonString(dbJSON, EmploymentExternalIdentifiers.class);
    }

}
