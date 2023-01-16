package org.orcid.api.common.util;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import jakarta.ws.rs.core.Response;

import org.orcid.api.common.filter.ApiVersionFilter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class ApiUtils {
    
    @Resource
    private OrcidUrlManager orcidUrlManager;
    
    @Resource
    private LocaleManager localeManager;
    
    public static String getApiVersion() {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            String apiVersion = (String) requestAttributes.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);
            return apiVersion;            
        } catch (NullPointerException e) {
            return null;
        }
    }
    
    public Response buildApiResponse(String orcid, String target, String putcode, String exception) {
        try {
            // ORCID is null if the request relates to a group-id
            String version = getApiVersion();
            URI responseUri = new URI((orcidUrlManager.getApiBaseUrl() + (version != null ? "/v" + version : "")  + "/" + (orcid != null ? orcid + "/" : "") + target + "/" + putcode));
            return Response.created(responseUri).build();
        } catch (URISyntaxException e) {
            throw new RuntimeException(localeManager.resolveMessage(exception), e);
        }
    }
}
