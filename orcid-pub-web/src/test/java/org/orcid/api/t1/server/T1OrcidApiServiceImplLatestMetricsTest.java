package org.orcid.api.t1.server;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.delegator.OrcidApiServiceDelegator;
import org.orcid.core.togglz.Features;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.togglz.junit.TogglzRule;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/orcid-t1-web-context.xml", "classpath:/orcid-t1-security-context.xml" })
public class T1OrcidApiServiceImplLatestMetricsTest {

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);
    
    @Resource
    private T1OrcidApiServiceImplRoot t1OrcidApiService;

    // mock the search results so that we don't need a db setub to check the
    // counter works as expected

    private final Response successResponse = Response.ok().build();

    private OrcidApiServiceDelegator mockServiceDelegator;

    @Before
    public void subvertDelegator() {
        mockServiceDelegator = mock(OrcidApiServiceDelegator.class);
        // view status is always fine
        when(mockServiceDelegator.viewStatusText()).thenReturn(successResponse);
        t1OrcidApiService.setOrcidApiServiceDelegator(mockServiceDelegator);
    }


    @Test
    public void testCounterUnaffectedByViewStatus() {
        Response response = t1OrcidApiService.viewStatusText();
        assertEquals(200, response.getStatus());
    }    
}
