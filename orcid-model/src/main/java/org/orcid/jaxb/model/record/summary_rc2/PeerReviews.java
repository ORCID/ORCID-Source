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
package org.orcid.jaxb.model.record.summary_rc2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "peerReviewGroup" })
@XmlRootElement(name = "peer-reviews", namespace = "http://www.orcid.org/ns/activities")
public class PeerReviews implements Serializable {
    
    private static final long serialVersionUID = 6779626621503362679L;
    @XmlElement(name = "group", namespace = "http://www.orcid.org/ns/activities")
    List<PeerReviewGroup> peerReviewGroup;

    public List<PeerReviewGroup> getPeerReviewGroup() {
        if (peerReviewGroup == null)
            peerReviewGroup = new ArrayList<PeerReviewGroup>();
        return peerReviewGroup;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((peerReviewGroup == null) ? 0 : peerReviewGroup.hashCode());
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
        PeerReviews other = (PeerReviews) obj;
        if (peerReviewGroup == null) {
            if (other.peerReviewGroup != null)
                return false;
        } else if (!peerReviewGroup.equals(other.peerReviewGroup))
            return false;
        return true;
    }
}
