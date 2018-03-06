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
package org.orcid.listener.persistence.util;

import org.orcid.jaxb.model.record_v2.Activity;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Work;

public enum ActivityType {
    //@formatter:off
    EDUCATIONS("/educations/", "educations", "educations_status", "educations_last_indexed"), 
    EMPLOYMENTS("/employments/", "employments", "employments_status", "employments_last_indexed"), 
    FUNDINGS("/fundings/", "fundings", "fundings_status", "fundings_last_indexed"), 
    PEER_REVIEWS("/peer-reviews/", "peer-reviews", "peer_reviews_status", "peer_reviews_last_indexed"), 
    WORKS("/works/", "works", "works_status", "works_last_indexed");
    //@formatter:on

    private final String pathDiscriminator;
    private final String value;
    private final String statusColumnName;
    private final String lastIndexedColumnName;

    ActivityType(String pathDiscriminator, String value, String statusColumnName, String lastIndexedColumnName) {
        this.pathDiscriminator = pathDiscriminator;
        this.value = value;
        this.statusColumnName = statusColumnName;
        this.lastIndexedColumnName = lastIndexedColumnName;
    }

    public String getPathDiscriminator() {
        return pathDiscriminator;
    }

    public String getValue() {
        return value;
    }

    public String getStatusColumnName() {
        return statusColumnName;
    }

    public String getLastIndexedColumnName() {
        return lastIndexedColumnName;
    }

    public static ActivityType inferFromActivity(Activity a) {
        if (a.getClass().isAssignableFrom(Education.class)) {
            return EDUCATIONS;
        } else if (a.getClass().isAssignableFrom(Employment.class)) {
            return EMPLOYMENTS;
        } else if (a.getClass().isAssignableFrom(Funding.class)) {
            return FUNDINGS;
        } else if (a.getClass().isAssignableFrom(PeerReview.class)) {
            return PEER_REVIEWS;
        } else if (a.getClass().isAssignableFrom(Work.class)) {
            return WORKS;
        }

        throw new IllegalArgumentException("Unable to find activity of type " + a.getClass().toGenericString());
    }
}
