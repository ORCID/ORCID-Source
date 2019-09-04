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

public enum ActivityType {
    //@formatter:off
    DISTINCTIONS("/distinctions/", "distinctions", "distinctions_status", "distinctions_last_indexed"),
    EDUCATIONS("/educations/", "educations", "educations_status", "educations_last_indexed"), 
    EMPLOYMENTS("/employments/", "employments", "employments_status", "employments_last_indexed"), 
    FUNDINGS("/fundings/", "fundings", "fundings_status", "fundings_last_indexed"), 
    INVITED_POSITIONS("/invited-positions/", "invited-positions", "invited_positions_status", "invited_positions_last_indexed"),
    MEMBERSHIP("/membership/", "membership", "membership_status", "membership_last_indexed"),
    PEER_REVIEWS("/peer-reviews/", "peer-reviews", "peer_reviews_status", "peer_reviews_last_indexed"), 
    QUALIFICATIONS("/qualifications/", "qualifications", "qualifications_status", "qualifications_last_indexed"),
    RESEARCH_RESOURCES("/research-resources/", "research-resources", "research_resources_status", "research_resources_last_indexed"),
    SERVICES("/services/", "services", "services_status", "services_last_indexed"),
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

    public static ActivityType inferFromActivity(org.orcid.jaxb.model.record_v2.Activity a) {
        if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.record_v2.Education.class)) {
            return EDUCATIONS;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.record_v2.Employment.class)) {
            return EMPLOYMENTS;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.record_v2.Funding.class)) {
            return FUNDINGS;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.record_v2.PeerReview.class)) {
            return PEER_REVIEWS;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.record_v2.Work.class)) {
            return WORKS;
        }

        throw new IllegalArgumentException("Unable to find activity of type " + a.getClass().toGenericString());
    }
    
    public static ActivityType inferFromActivity(org.orcid.jaxb.model.v3.release.record.Activity a) {
        if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.release.record.Distinction.class)) {
            return DISTINCTIONS;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.release.record.Education.class)) {
            return EDUCATIONS;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.release.record.Employment.class)) {
            return EMPLOYMENTS;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.release.record.Funding.class)) {
            return FUNDINGS;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.release.record.InvitedPosition.class)) {
            return INVITED_POSITIONS;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.release.record.Membership.class)) {
            return MEMBERSHIP;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.release.record.PeerReview.class)) {
            return PEER_REVIEWS;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.release.record.Qualification.class)) {
            return QUALIFICATIONS;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.release.record.ResearchResource.class)) {
            return RESEARCH_RESOURCES;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.release.record.Service.class)) {
            return SERVICES;
        } else if (a.getClass().isAssignableFrom(org.orcid.jaxb.model.v3.release.record.Work.class)) {
            return WORKS;
        }

        throw new IllegalArgumentException("Unable to find activity of type " + a.getClass().toGenericString());
    }
}
