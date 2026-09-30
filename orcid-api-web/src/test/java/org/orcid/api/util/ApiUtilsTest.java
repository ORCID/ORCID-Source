package org.orcid.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import jakarta.ws.rs.core.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.api.common.filter.ApiVersionFilter;
import org.orcid.api.common.util.ApiUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * {@link ApiUtils} composes a Location URI out of the API base URL, the version
 * stamped on the current request by {@link ApiVersionFilter}, and the orcid,
 * target and put code it is handed. That is string work over a thread-local, so
 * this runs on mocks; the api-web context it used to boot supplied only the
 * bean.
 *
 * <p>
 * {@link OrcidUrlManager} is a real instance rather than a mock because
 * {@code getApiBaseUrl()} is a plain getter -- stubbing it would replace one
 * line of production code with an equivalent line of test code and gain
 * nothing. It is given the {@code org.orcid.core.apiBaseUri} that
 * {@code orcid-test/.../test-core.properties} carries, which is the value the
 * expected URLs below were written against.
 *
 * <p>
 * The version is read from {@link RequestContextHolder}, which is a static
 * thread-local: it is cleared before and after each test so a version set by
 * one method cannot decide the outcome of another, here or in any test class
 * sharing the JVM.
 */
@RunWith(MockitoJUnitRunner.class)
public class ApiUtilsTest {

    @Mock
    private LocaleManager localeManager;

    @InjectMocks
    private ApiUtils apiUtils = new ApiUtils();

    private String getLocationFromResponse(Response response) {
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        return String.valueOf(resultWithPutCode.get(0));
    }
    
    @Before
    public void before() {
        RequestContextHolder.resetRequestAttributes();

        OrcidUrlManager orcidUrlManager = new OrcidUrlManager();
        orcidUrlManager.setApiBaseUrl("https://localhost:8443/orcid-api-web");
        ReflectionTestUtils.setField(apiUtils, "orcidUrlManager", orcidUrlManager);
    }

    @After
    public void after() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void testGetApiVersion() {        
        String ver = ApiUtils.getApiVersion();
        assertNull(ver);

        RequestAttributes attrs = new ServletRequestAttributes(new MockHttpServletRequest());
        attrs.setAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME, "3.0", RequestAttributes.SCOPE_REQUEST);
        RequestContextHolder.setRequestAttributes(attrs);

        ver = ApiUtils.getApiVersion();
        assertEquals("3.0", ver);
    }

    @Test
    public void testBuildApiResponse() {
        Response response = apiUtils.buildApiResponse("0000-0001-2345-6789", "work", "122345", "apiError.creatework_response.exception");
        String location = getLocationFromResponse(response);
        assertEquals("https://localhost:8443/orcid-api-web/0000-0001-2345-6789/work/122345", location);

        response = apiUtils.buildApiResponse(null, "group-id-record", "5", "apiError.creategroupidrecord_response.exception");
        location = getLocationFromResponse(response);
        assertEquals("https://localhost:8443/orcid-api-web/group-id-record/5", location);

        RequestAttributes attrs = new ServletRequestAttributes(new MockHttpServletRequest());
        attrs.setAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME, "2.0", RequestAttributes.SCOPE_REQUEST);
        RequestContextHolder.setRequestAttributes(attrs);

        response = apiUtils.buildApiResponse("0000-0000-0000-0000", "peer-review", "01", "apiError.createpeerreview_response.exception");
        location = getLocationFromResponse(response);
        assertEquals("https://localhost:8443/orcid-api-web/v2.0/0000-0000-0000-0000/peer-review/01", location);

        response = apiUtils.buildApiResponse(null, "group-id-record", "01", "apiError.creategroupidrecord_response.exception");
        location = getLocationFromResponse(response);
        assertEquals("https://localhost:8443/orcid-api-web/v2.0/group-id-record/01", location);
    }
}
