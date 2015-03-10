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
package org.orcid.jaxb.model.record.summary;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.record.FundingExternalIdentifier;
import org.orcid.jaxb.model.record.WorkExternalIdentifier;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "externalIdentifierType", "externalIdentifierId" })
@XmlRootElement(name = "identifier", namespace = "http://www.orcid.org/ns/activities")
public class Identifier {

    @XmlElement(name = "external-identifier-type", namespace = "http://www.orcid.org/ns/activities")
    private String externalIdentifierType;
    @XmlElement(name = "external-identifier-id", namespace = "http://www.orcid.org/ns/activities")
    private String externalIdentifierId;

    public String getExternalIdentifierType() {
        return externalIdentifierType;
    }

    public void setExternalIdentifierType(String externalIdentifierType) {
        this.externalIdentifierType = externalIdentifierType;
    }

    public String getExternalIdentifierId() {
        return externalIdentifierId;
    }

    public void setExternalIdentifierId(String externalIdentifierId) {
        this.externalIdentifierId = externalIdentifierId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((externalIdentifierId == null) ? 0 : externalIdentifierId.hashCode());
        result = prime * result + ((externalIdentifierType == null) ? 0 : externalIdentifierType.hashCode());
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
        Identifier other = (Identifier) obj;
        if (externalIdentifierId == null) {
            if (other.externalIdentifierId != null)
                return false;
        } else if (!externalIdentifierId.equals(other.externalIdentifierId))
            return false;
        if (externalIdentifierType == null) {
            if (other.externalIdentifierType != null)
                return false;
        } else if (!externalIdentifierType.equals(other.externalIdentifierType))
            return false;
        return true;
    }

    public static Identifier fromWorkExternalIdentifier(WorkExternalIdentifier workExtId) {
        if ((workExtId.getWorkExternalIdentifierId() == null || workExtId.getWorkExternalIdentifierId().getContent() == null || workExtId.getWorkExternalIdentifierId()
                .getContent().isEmpty())
                && workExtId.getWorkExternalIdentifierType() == null) {
            return null;
        }
        Identifier result = new Identifier();
        result.setExternalIdentifierId(workExtId.getWorkExternalIdentifierId().getContent());
        result.setExternalIdentifierType(workExtId.getWorkExternalIdentifierType().name());
        return result;
    }

    public static Identifier fromFundingExternalIdentifier(FundingExternalIdentifier fundingExtId) {
        Identifier result = new Identifier();
        if(fundingExtId.getValue() != null && !fundingExtId.getValue().isEmpty())
            result.setExternalIdentifierId(fundingExtId.getValue());
        else if(fundingExtId.getUrl() != null)
            result.setExternalIdentifierId(fundingExtId.getUrl().getValue());
        result.setExternalIdentifierType(fundingExtId.getType().name());
        return result;
    }
}
