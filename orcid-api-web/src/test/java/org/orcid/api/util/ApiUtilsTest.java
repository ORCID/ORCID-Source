package org.orcid.api.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.filter.ApiVersionFilter;
import org.orcid.api.common.util.ApiUtils;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-api-web-context.xml" })
public class ApiUtilsTest {

    @Resource(name = "apiUtils")
    private ApiUtils apiUtils;

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
