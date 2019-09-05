package org.orcid.core.utils.statistics;

public enum StatisticsEnum {
    KEY_LIVE_IDS("liveIds"), 
    KEY_IDS_WITH_EDUCATION_QUALIFICATION("idsWithEducationQualification"),
    KEY_IDS_WITH_EMPLOYMENT("idsWithEmployment"),
    KEY_IDS_WITH_INVITED_POSITION_DISTINCTION("idsWithInvitedPositionDistinction"),
    KEY_IDS_WITH_MEMBERSHIP_SERVICE("idsWithMembershipService"),
    KEY_IDS_WITH_EXTERNAL_ID("idsWithExternalId"),
    KEY_IDS_WITH_FUNDING("idsWithFunding"),
    KEY_IDS_WITH_PEER_REVIEW("idsWithPeerReview"),
    KEY_IDS_WITH_PERSON_ID("idsWithPersonId"),
    KEY_IDS_WITH_RESEARCH_RESOURCE("idsWithResearchResource"),
    KEY_IDS_WITH_VERIFIED_EMAIL("idsWithVerifiedEmail"),
    KEY_IDS_WITH_WORKS("idsWithWorks"),
    KEY_NUMBER_OF_WORKS("works"),
    KEY_WORKS_WITH_DOIS("worksWithDois"),
    KEY_UNIQUE_DOIS("uniqueDois"),
    KEY_NUMBER_OF_EMPLOYMENT("employment"),
    KEY_EMPLOYMENT_UNIQUE_ORG("employmentUniqueOrg"),
    KEY_NUMBER_OF_EDUCATION_QUALIFICATION("educationQualification"),
    KEY_EDUCATION_QUALIFICATION_UNIQUE_ORG("educationQualificationUniqueOrg"),
    KEY_NUMBER_OF_INVITED_POSITION_DISTINCTION("invitedPositionDistinction"),
    KEY_INVITED_POSITION_DISTINCTION_UNIQUE_ORG("invitedPositionDistinctionUniqueOrg"),
    KEY_NUMBER_OF_MEMBERSHIP_SERVICE("membershipService"),
    KEY_MEMBERSHIP_SERVICE_UNIQUE_ORG("membershipServiceUniqueOrg"),
    KEY_NUMBER_OF_FUNDING("funding"),
    KEY_FUNDING_UNIQUE_ORG("fundingUniqueOrg"),
    KEY_NUMBER_OF_PEER_REVIEW("peerReview"),
    KEY_NUMBER_OF_PERSON_ID("personId"),
    KEY_NUMBER_OF_RESEARCH_RESOURCE("researchResource"),;
    
    /** For use as allowable values list for swagger
     * Annoyingly this can only be an inline static final if we want it to work
     * There is a unit test to check it correctly contains all values in declared order
     */
    public static final String allowableSwaggerValues = "liveIds,idsWithEducationQualification,idsWithEmployment,idsWithInvitedPositionDistinction,idsWithMembershipService,idsWithExternalId,idsWithFunding,idsWithPeerReview,idsWithPersonId,idsWithResearchResource,idsWithVerifiedEmail,idsWithWorks,works,worksWithDois,uniqueDois,employment,employmentUniqueOrg,educationQualification,educationQualificationUniqueOrg,invitedPositionDistinction,invitedPositionDistinctionUniqueOrg,membershipService,membershipServiceUniqueOrg,funding,fundingUniqueOrg,peerReview,personId,researchResource";
    
    private final String value;

    StatisticsEnum(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    //not sure if this is used silently, so not removed yet.
    @Deprecated
    public static StatisticsEnum fromValue(String v) {
        for (StatisticsEnum c : StatisticsEnum.values()) {
            if (c.value.equals(v.toLowerCase())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
    
    /** Method called by JAX-RS when parsing path parameters
     * 
     * @param v the path param
     * @return the matching enum 
     */
    public static StatisticsEnum fromString(String v) {
        for (StatisticsEnum c : StatisticsEnum.values()) {
            if (c.value().equalsIgnoreCase(v.trim())) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
        
}
