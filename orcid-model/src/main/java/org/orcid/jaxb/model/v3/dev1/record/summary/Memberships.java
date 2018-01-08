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
package org.orcid.jaxb.model.v3.dev1.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "memberships", namespace = "http://www.orcid.org/ns/activities")
public class Memberships extends Affiliations<MembershipSummary> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -1045628720886435629L;

    public Memberships() {

    }

    public Memberships(List<MembershipSummary> summaries) {
        super();
        this.summaries = summaries;
    }

    @Override
    public List<MembershipSummary> getSummaries() {
        if (this.summaries == null) {
            this.summaries = new ArrayList<MembershipSummary>();
        }
        return this.summaries;
    }
}
