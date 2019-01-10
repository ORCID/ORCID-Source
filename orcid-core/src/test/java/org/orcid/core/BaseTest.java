package org.orcid.core;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public abstract class BaseTest extends DBUnitTest {

    @Before
    public void beforeClass() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Utility method to obtain the actual impl class behind a proxy. Used for
     * when you need to set a dependency only exposed via the impl class, but
     * the class is proxied.
     * 
     * Taken from
     * http://www.techper.net/2009/06/05/how-to-acess-target-object-behind
     * -a-spring-proxy/
     * 
     * @param proxy
     * @param targetClass
     * @return
     * @throws Exception
     */    
    protected <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        return TargetProxyHelper.getTargetObject(proxy, targetClass);
    }
    
}
