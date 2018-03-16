package org.orcid.persistence.dao;

public interface StatisticsGeneratorDao {

    public long getLiveIds();
    
    public long getAccountsWithEducation();
    
    public long getAccountsWithEmployment();
    
    public long getAccountsWithExternalId();
    
    public long getAccountsWithFunding();
    
    public long getAccountsWithPeerReview();
    
    public long getAccountsWithPersonId();

    public long getAccountsWithVerifiedEmails();

    public long getAccountsWithWorks();

    public long getNumberOfWorks();

    public long getNumberOfUniqueDOIs();

    long getNumberOfEmployment();

    long getNumberOfEducation();

    long getNumberOfFunding();
    
    public long getNumberOfPeerReview();
    
    public long getNumberOfPersonId();

    long getNumberOfEmploymentUniqueOrg();

    long getNumberOfEducationUniqueOrg();

    long getNumberOfFundingUniqueOrg();
}
