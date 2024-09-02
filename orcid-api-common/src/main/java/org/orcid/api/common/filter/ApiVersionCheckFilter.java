package org.orcid.api.common.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMethod;


@Provider
public class ApiVersionCheckFilter implements ContainerRequestFilter{

    @Autowired
    private LocaleManager localeManager;
    
    @Context private HttpServletRequest httpRequest;

    public static final Pattern VERSION_PATTERN = Pattern.compile("v(\\d.*?)/");

    private static final String WEBHOOKS_PATH_PATTERN = OrcidStringUtils.ORCID_STRING + "/webhook/.+";
    
    private static final String MEMBER_INFO_PATH = "member-info";
    
    public ApiVersionCheckFilter() {
    }
    
    public ApiVersionCheckFilter(HttpServletRequest req) {
        this.httpRequest = req;
    }
    
    public ApiVersionCheckFilter(LocaleManager locale, HttpServletRequest req) {
        this.httpRequest = req;
        this.localeManager = locale;
    }
    
    @Override
    public void filter(ContainerRequestContext request) {
        String path = request.getUriInfo().getPath();
        String method = request.getMethod() == null ? null : request.getMethod().toUpperCase();
        Matcher matcher = VERSION_PATTERN.matcher(path);        
        String version = null;
        if (matcher.lookingAt()) {
            version = matcher.group(1);
        }
        if(PojoUtil.isEmpty(version) && !PojoUtil.isEmpty(method) && !"oauth/token".equals(path) && !MEMBER_INFO_PATH.equals(path) && !path.matches(WEBHOOKS_PATH_PATTERN)) {
            if(!RequestMethod.GET.name().equals(method)) {
                Object params[] = {method};
                throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_missing_version.exception", params));    
            }
        } else if (version != null && version.startsWith("1.1")) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_version_disabled.1_1.exception"));            
        } else if (version != null && version.startsWith("1.2")) {
            throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_version_disabled.1_2.exception"));
        } else if(version != null && (version.startsWith("2.") || version.startsWith("3."))) {
            if(!OrcidUrlManager.isSecure(httpRequest) && !(path.endsWith("/pubStatus") || path.endsWith("/apiStatus"))) {
                throw new OrcidBadRequestException(localeManager.resolveMessage("apiError.badrequest_secure_only.exception"));
            }
        }

        return;
    }
}