package org.orcid.internal.server;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.test.DBUnitTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.uri.UriBuilderImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-internal-api-context.xml", "classpath:orcid-internal-api-security-context.xml" })
public class InternalApiServiceDelegatorTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml");

    @Resource
    private InternalApiServiceDelegator internalApiServiceDelegator;
    
    @Before
    public void before() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.INTERNAL_PERSON_LAST_MODIFIED);
    }

    @Test
    public void viewStatusText() {
        Response response = internalApiServiceDelegator.viewStatusText();
        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals("OK I am here", String.valueOf(response.getEntity()));
    }
    
}
