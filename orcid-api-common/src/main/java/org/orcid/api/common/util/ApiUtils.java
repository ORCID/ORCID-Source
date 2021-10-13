package org.orcid.api.common.util;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.web.filters.ApiVersionFilter;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public class ApiUtils {
    
    @Resource
    private OrcidUrlManager orcidUrlManager;
    
    @Resource
    private LocaleManager localeManager;
    
    public String getApiVersion() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String apiVersion = (String) requestAttributes.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);
        return apiVersion;
    }
    
    public Response buildApiResponse(String target, String putcode, String exception) {
        try {
            URI responseUri = new URI((orcidUrlManager.getApiBaseUrl() + "/" + getApiVersion() + "/" + target + "/" + putcode));
            return Response.created(responseUri).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage(exception), e);
        }
    }
}
