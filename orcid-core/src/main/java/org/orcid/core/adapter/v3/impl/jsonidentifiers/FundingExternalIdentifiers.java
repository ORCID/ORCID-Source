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

import org.orcid.core.adapter.impl.jsonidentifiers.JSONIdentifierAdapter;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.dev1.record.Relationship;

public class FundingExternalIdentifiers implements Serializable, JSONIdentifierAdapter<org.orcid.jaxb.model.message.FundingExternalIdentifiers, ExternalIDs> {

    private static final long serialVersionUID = 1L;
    protected List<FundingExternalIdentifier> fundingExternalIdentifier;

    public FundingExternalIdentifiers() {
    }

    public FundingExternalIdentifiers(org.orcid.jaxb.model.message.FundingExternalIdentifiers messagePojo) {
        if (messagePojo != null && !messagePojo.getFundingExternalIdentifier().isEmpty()) {
            for (org.orcid.jaxb.model.message.FundingExternalIdentifier messageFei : messagePojo.getFundingExternalIdentifier()) {
                this.getFundingExternalIdentifier().add(new FundingExternalIdentifier(messageFei));
            }

            for (FundingExternalIdentifier fei : this.getFundingExternalIdentifier()) {
                if (fei.getRelationship() == null) {
                    fei.setRelationship(Relationship.SELF.value());
                }
            }
        }
    }

    public FundingExternalIdentifiers(ExternalIDs recordPojo) {
        if (recordPojo != null && !recordPojo.getExternalIdentifier().isEmpty()) {
            for (ExternalID recordEi : recordPojo.getExternalIdentifier()) {
                this.getFundingExternalIdentifier().add(new FundingExternalIdentifier(recordEi));
            }
        }
    }

    public List<FundingExternalIdentifier> getFundingExternalIdentifier() {
        if (fundingExternalIdentifier == null)
            fundingExternalIdentifier = new ArrayList<FundingExternalIdentifier>();
        return fundingExternalIdentifier;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fundingExternalIdentifier == null) ? 0 : fundingExternalIdentifier.hashCode());
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
        FundingExternalIdentifiers other = (FundingExternalIdentifiers) obj;
        if (fundingExternalIdentifier == null) {
            if (other.fundingExternalIdentifier != null)
                return false;
        } else {
            if (other.fundingExternalIdentifier == null)
                return false;
            else if (!(fundingExternalIdentifier.containsAll(other.fundingExternalIdentifier) && other.fundingExternalIdentifier.containsAll(fundingExternalIdentifier)
                    && other.fundingExternalIdentifier.size() == fundingExternalIdentifier.size())) {
                return false;
            }
        }
        return true;
    }

    public org.orcid.jaxb.model.message.FundingExternalIdentifiers toMessagePojo() {
        org.orcid.jaxb.model.message.FundingExternalIdentifiers result = new org.orcid.jaxb.model.message.FundingExternalIdentifiers();
        if (!this.getFundingExternalIdentifier().isEmpty()) {
            for (FundingExternalIdentifier fei : this.getFundingExternalIdentifier()) {
                result.getFundingExternalIdentifier().add(fei.toMessagePojo());
            }
        }
        return result;
    }

    public ExternalIDs toRecordPojo() {
        ExternalIDs result = new ExternalIDs();
        if (!this.getFundingExternalIdentifier().isEmpty()) {
            for (FundingExternalIdentifier fei : this.getFundingExternalIdentifier()) {
                result.getExternalIdentifier().add(fei.toRecordPojo());
            }
        }
        return result;
    }

    public String toDBJSONString() {
        return JsonUtils.convertToJsonString(this);
    }

    public static FundingExternalIdentifiers fromDBJSONString(String dbJSON) {
        return JsonUtils.readObjectFromJsonString(dbJSON, FundingExternalIdentifiers.class);
    }

}
