package org.orcid.persistence.dao;

public interface StatisticsGeneratorDao {

    public long getLiveIds();
    
    public long getAccountsWithEmployment();
    
    public long getAccountsWithEducationQualification();
    
    public long getAccountsWithInvitedPositionDistinction();
    
    public long getAccountsWithMembershipService();
    
    public long getAccountsWithExternalId();
    
    public long getAccountsWithFunding();
    
    public long getAccountsWithPeerReview();
    
    public long getAccountsWithResearchResource();
    
    public long getAccountsWithPersonId();

    public long getAccountsWithVerifiedEmails();

    public long getAccountsWithWorks();

    public long getNumberOfWorks();

    public long getNumberOfUniqueDOIs();

    long getNumberOfEmployment();

    long getNumberOfEducationQualification();
    
    long getNumberOfInvitedPositionDistinction();
    
    long getNumberOfMembershipService();

    long getNumberOfFunding();
    
    public long getNumberOfPeerReview();
    
    public long getNumberOfResearchResource();
    
    public long getNumberOfPersonId();

    long getNumberOfEmploymentUniqueOrg();

    long getNumberOfEducationQualificationUniqueOrg();
    
    long getNumberOfInvitedPositionDistinctionUniqueOrg();
    
    long getNumberOfMembershipServiceUniqueOrg();

    long getNumberOfFundingUniqueOrg();
}
