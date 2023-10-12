package org.orcid.core.common.manager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.orcid.core.common.manager.impl.EmailDomainManagerImpl;
import org.orcid.persistence.dao.EmailDomainDao;
import org.orcid.test.TargetProxyHelper;

public class EmailDomainManagerTest {
    @Mock
    private EmailDomainDao emailDomainDaoMock;

    @Mock
    private EmailDomainDao emailDomainDaoReadOnlyMock;
    
    EmailDomainManager edm = new EmailDomainManagerImpl();
    
    @Before
    public void before(){
        TargetProxyHelper.injectIntoProxy(edm, "emailDomainDao", emailDomainDaoMock);
        TargetProxyHelper.injectIntoProxy(edm, "emailDomainDaoReadOnly", emailDomainDaoReadOnlyMock);
    }
    
    @Test
    public void createEmailDomainTest() {
        // Check null domain
        // Check empty domain
        // Check null category
        // Check good value
    }
    
    @Test
    public void updateCategory() {
        
    }

    @Test
    public void findByEmailDoman() {
        
    }

    @Test
    public void findByCategory() {
        
    }
}
