/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.orcid.test.DBUnitTest;
import org.orcid.test.TargetProxyHelper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/orcid-core-context.xml" })
public class BaseTest extends DBUnitTest {

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
    @SuppressWarnings( { "unchecked" })
    protected <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        return TargetProxyHelper.getTargetObject(proxy, targetClass);
    }

}
