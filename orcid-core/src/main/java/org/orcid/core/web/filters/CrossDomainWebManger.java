package org.orcid.core.web.filters;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;

public class CrossDomainWebManger {

    @Value("${org.orcid.security.cors.allowed_domains:orcid.org}")
    private String allowedDomains;
    
    @Value("${org.orcid.security.cors.allowed_path:/public}")
    private String allowedPaths;
    
    @Value("${org.orcid.security.cors.allowed_functions}")
    private String allowedFunctions;    
    
    private List<String> domainsRegex;  
    
    public boolean allowed(HttpServletRequest request) throws MalformedURLException {
        URL url = new URL(request.getRequestURL().toString());
        String path = url.getPath();        
        
        //Check origin header
        if(!PojoUtil.isEmpty(request.getHeader("origin"))) {
            if (validateDomain(request.getHeader("origin"))) {
                return true;
            }
        }
        
        //Check referer header
        if(!PojoUtil.isEmpty(request.getHeader("referer"))) {
            if (validateDomain(request.getHeader("referer"))) {
                return true;
            }
        }        
        
        if(validatePath(path)) {
            return true;
        }
        
        if(validateFunction(path)) {
            return true;
        }
        
        return false;
    }

    private boolean validateDomain(String url) throws MalformedURLException {
        URL netUrl = new URL(url);
        String domain = netUrl.getHost();
        for (String allowedDomain : getAllowedDomainsRegex()) {
            if (domain.matches(allowedDomain)) {
                return true;
            }
        }
        return false;
    }    
    
    private boolean validatePath(String path) {
        for(String allowedPath : allowedPaths.split(",")) {
            if(path.startsWith("/" + allowedPath)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean validateFunction(String path) {
        for(String allowedFunction : allowedFunctions.split(",")) {
            if(path.contains(allowedFunction)) {
                return true;
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
        String result = domainPattern.replace(".", "\\.");
        result = domainPattern.replace("*", ".+");
        return result;
    }

}
