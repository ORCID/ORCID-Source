package org.orcid.core.manager.v3;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.orcid.test.OrcidJUnit4ClassRunner;

@SuiteClasses({
    RecordManagerTest.class, 
    NotificationManagerTest.class,
    EmailManagerReadOnlyTest.class, 
    OrcidSecurityManager_EmailTest.class,
    ExternalIdentifierManagerTest.class,
    
    ProfileFundingManagerTest.class,
    OrcidSecurityManager_WorkBulkTest.class, 
    PersonalDetailsManagerTest.class,
    
    OrcidSecurityManager_ActivitiesSummaryTest.class, 
    PeerReviewManagerTest.class, 
    ProfileKeywordManagerTest.class, 
    
    RecordNameManagerV3Test.class, 
    PersonDetailsManagerTest.class, 
    SpamManagerTest.class, 
    GroupIdRecordManagerTest.class, 
    OrgManagerTest.class,
    ResearcherUrlManagerTest.class, 
    EmailManagerTest.class, 
    GroupingSuggestionManagerTest.class, 
    ClientManagerTest.class,
    ClientManagerReadOnlyTest.class, 
    ResearchResourceManagerTest.class
    
})
@RunWith(Suite.class)
public class AATest {

}
