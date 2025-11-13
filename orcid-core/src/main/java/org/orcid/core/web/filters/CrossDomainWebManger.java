package org.orcid.core.web.filters;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;

public class CrossDomainWebManger {

    private static final String LOCALHOST = "localhost";

    Pattern p = Pattern.compile("^/userStatus\\.json|^/lang\\.json|^/oauth/userinfo|^/oauth/jwks|^/\\.well-known/openid-configuration");
    
    @Value("${org.orcid.security.cors.allowed_domains:localhost}")
    private String allowedDomains;

    private List<String> domainsRegex;

    public boolean allowed(HttpServletRequest request) throws URISyntaxException {
        String path = OrcidUrlManager.getPathWithoutContextPath(request);

        // Check origin header
        if (!PojoUtil.isEmpty(request.getHeader("origin"))) {
            // If it is a valid domain, allow
            if (validateDomain(request.getHeader("origin"))) {
                return true;
            } 
        }

        // If it is and invalid domain, validate the path
        if (validatePath(path)) {
            return true;
        }
        
        return false;
    }

    public boolean validateDomain(String url) throws URISyntaxException {
        URI uri = new URI(url);
        String domain = uri.getHost();
        if(domain != null) {
            for (String allowedDomain : getAllowedDomainsRegex()) {
                if (domain.matches(allowedDomain)) {
                    return true;
                }
            }
        }
        return false;
    }

    private List<String> getAllowedDomainsRegex() {
        if (domainsRegex == null) {
            domainsRegex = new ArrayList<String>();
            for (String allowedDomain : allowedDomains.split(",")) {
                String regex = transformPatternIntoRegex(allowedDomain);
                domainsRegex.add(regex);
            }
        }

        return domainsRegex;
    }

    private String transformPatternIntoRegex(String domainPattern) {
        return domainPattern.replace(".", "\\.");
    }

    public boolean validatePath(String path) {
        Matcher m = p.matcher(path);
        if (m.matches()) {
            return true;
        }
        return false;
    }
}
