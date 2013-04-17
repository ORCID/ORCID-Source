package org.orcid.persistence.dao;

import java.util.Set;

import javax.persistence.Query;

public interface StatisticsDao {

    public long getLiveIds();

    public long getAccountsWithVerifiedEmails();
    
    public long getAccountsWithWorks();
    
    public long getNumberOfWorks();
    
    public long getNumberOfWorksWithDOIs();
}
