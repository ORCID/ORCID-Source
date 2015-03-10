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
package org.orcid.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FundingExternalIdentifiers implements Serializable {
    private static final long serialVersionUID = 1L;
    protected List<FundingExternalIdentifier> fundingExternalIdentifier;

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
            else if (!(fundingExternalIdentifier.containsAll(other.fundingExternalIdentifier) && other.fundingExternalIdentifier.containsAll(fundingExternalIdentifier) && other.fundingExternalIdentifier
                    .size() == fundingExternalIdentifier.size())) {
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

    public org.orcid.jaxb.model.record.FundingExternalIdentifiers toRecordPojo() {
        org.orcid.jaxb.model.record.FundingExternalIdentifiers result = new org.orcid.jaxb.model.record.FundingExternalIdentifiers();
        if (!this.getFundingExternalIdentifier().isEmpty()) {
            for (FundingExternalIdentifier fei : this.getFundingExternalIdentifier()) {
                result.getExternalIdentifier().add(fei.toRecordPojo());
            }
        }
        return result;
    }

    public static FundingExternalIdentifiers fromMessagePojo(org.orcid.jaxb.model.message.FundingExternalIdentifiers messagePojo) {
        if (messagePojo == null)
            return null;
        FundingExternalIdentifiers result = new FundingExternalIdentifiers();
        if (!messagePojo.getFundingExternalIdentifier().isEmpty()) {
            for (org.orcid.jaxb.model.message.FundingExternalIdentifier messageFei : messagePojo.getFundingExternalIdentifier()) {
                result.getFundingExternalIdentifier().add(FundingExternalIdentifier.fromMessagePojo(messageFei));
            }
        }

        return result;
    }

    public static FundingExternalIdentifiers fromRecordPojo(org.orcid.jaxb.model.record.FundingExternalIdentifiers recordPojo) {
        if (recordPojo == null)
            return null;
        FundingExternalIdentifiers result = new FundingExternalIdentifiers();
        if (!recordPojo.getExternalIdentifier().isEmpty()) {
            for (org.orcid.jaxb.model.record.FundingExternalIdentifier recordEi : recordPojo.getExternalIdentifier()) {                               
                result.getFundingExternalIdentifier().add(FundingExternalIdentifier.fromRecordPojo(recordEi));
            }
        }

        return result;
    }

}
