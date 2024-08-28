package org.orcid.api.common.filter;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.jetty.http.HttpStatus;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.v3.release.error.OrcidError;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class DefaultApiVersionFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultApiVersionFilter .class);

    private static final Pattern VERSION_PATTERN = Pattern.compile("/v(\\d.*?)/");

    private static final List<String> IGNORE_LIST = Arrays.asList("/resources/", "/search/", "/oauth/token", OrcidApiConstants.EXPERIMENTAL_RDF_V1 + "/", "/static/", "/openapi.json");

    private static final String WEBHOOK_PATH_REGEX = "^/" + OrcidStringUtils.ORCID_STRING + "/webhook/.+";

    public static final Pattern webhookPattern = Pattern.compile(WEBHOOK_PATH_REGEX);
    
    private static final String VERSION_3_0 = "3.0";
    private static final String VERSION_2_1 = "2.1";
    private static final String VERSION_2_0 = "2.0";

    private String V3_JSON_RESPONSE;
    private String V3_XML_RESPONSE;
    
    private String V2_JSON_RESPONSE;
    private String V2_XML_RESPONSE;
    
    @Resource
    protected OrcidUrlManager orcidUrlManager;
    
    @Resource
    private LocaleManager localeManager;

    protected Features feature;
    
    protected Boolean isPublicApi;

    public void setFeature(Features f) {
        this.feature = f;
    }
    
    public void setIsPublicApi(Boolean api) {
        this.isPublicApi = api;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getServletPath();
        
        if (IGNORE_LIST.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
        } else if (webhookPattern.matcher(path).matches()) {
            filterChain.doFilter(request, response);
        } else {
            Matcher matcher = VERSION_PATTERN.matcher(path);
            String version = null;
            if (matcher.lookingAt()) {
                version = matcher.group(1);
            }
            String baseUrl = isPublicApi ? orcidUrlManager.getPubBaseUrl() : orcidUrlManager.getApiBaseUrl();
            if (PojoUtil.isEmpty(version)) {
                if (isLODButNotJSONLD(request.getHeader("Accept"))) {
                    String redirectUri = orcidUrlManager.getPubBaseUrl() + OrcidApiConstants.EXPERIMENTAL_RDF_V1 + path;
                    response.sendRedirect(redirectUri);
                } else {
                    if (feature == null || feature.isActive()) {
                        String redirectUri = baseUrl + "/v" +VERSION_3_0 + path;
                        response.sendRedirect(redirectUri);
                    } else {
                        String redirectUri = baseUrl + "/v" + VERSION_2_0 + path;
                        response.sendRedirect(redirectUri);
                    }
                }
            } else {
                if(!version.equals(VERSION_3_0) && !version.equals(VERSION_2_0) && !version.equals(VERSION_2_1)) {
                    String url;
                    if(version.startsWith(VERSION_2_0)) {           
                        url= baseUrl + "/v" + VERSION_2_0 + path.replace("/v", "").substring(version.length());
                        version = VERSION_2_0;
                    }
                    else if(version.startsWith(VERSION_2_1)) {                  
                        url= baseUrl + "/v" + VERSION_2_1 + path.replace("/v", "").substring(version.length());
                        version = VERSION_2_1;
                    }
                    else {
                        url = baseUrl + "/v" + VERSION_3_0 + path.replace("/v", "").substring(version.length());
                        version = VERSION_3_0;
                    } 
                    
                    String redirectURLEncoded = response.encodeRedirectURL(url);
                    response.setStatus(HttpStatus.PERMANENT_REDIRECT_308); 
                    response.setHeader("Location", redirectURLEncoded);
                    LOGGER.info("Redirecting request '{}' to '{}'", httpRequest.getServletPath(), redirectURLEncoded);
                    String accept = request.getHeader("Accept") == null ? "" : request.getHeader("Accept").toLowerCase();
                    if (accept.contains("json")) {
                        response.getWriter().println(getJsonResponse(version));
                    } else {
                        try {
                            response.getWriter().println(getXmlResponse(version));
                        } catch (Exception e) {
                            LOGGER.error("Unable to generate XML message", e);
                            response.getWriter().println(localeManager.resolveMessage("apiError.9056.developerMessage", version));
                        }
                    }
                }
                else {
                    filterChain.doFilter(request, response);
                }
            }
        }
    }
    
    private boolean isLODButNotJSONLD(String accept) {
        if (accept == null)
            return false;
        return (accept.contains("n3") || accept.contains("rdf") || accept.contains("n-triples") || accept.contains("turtle"));
    }
    
    private String getXmlResponse(String version) throws JAXBException {
        if (V2_XML_RESPONSE == null) {
            this.V2_XML_RESPONSE = buildXMLResponse(VERSION_2_0);
        }

        if (V3_XML_RESPONSE == null) {
            this.V3_XML_RESPONSE = buildXMLResponse(VERSION_3_0);
        }

        return version.equals(VERSION_2_0) ? V2_XML_RESPONSE : V3_XML_RESPONSE;
    }

    private String buildXMLResponse(String version) throws JAXBException {
        OrcidError error = new OrcidError();
        error.setDeveloperMessage(localeManager.resolveMessage("apiError.9056.developerMessage", version));
        error.setUserMessage(localeManager.resolveMessage("apiError.9056.userMessage", version));
        error.setResponseCode(HttpStatus.PERMANENT_REDIRECT_308);
        error.setErrorCode(9056);

        JAXBContext context = JAXBContext.newInstance(error.getClass());
        StringWriter writer = new StringWriter();
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(error, writer);
        return writer.toString();
    }

    private String getJsonResponse(String version) {
        if (V2_JSON_RESPONSE == null) {
            V2_JSON_RESPONSE = buildJSONRespones(VERSION_2_0);
        }

        if (V3_JSON_RESPONSE == null) {
            V3_JSON_RESPONSE = buildJSONRespones(VERSION_3_0);
        }
        return version.equals(VERSION_2_0) ? V2_JSON_RESPONSE : V3_JSON_RESPONSE;
    }

    private String buildJSONRespones(String version) {
        OrcidError error = new OrcidError();
        error.setDeveloperMessage(localeManager.resolveMessage("apiError.9056.developerMessage", VERSION_2_0));
        error.setUserMessage(localeManager.resolveMessage("apiError.9056.userMessage", version));
        error.setResponseCode(HttpServletResponse.SC_MOVED_PERMANENTLY);
        error.setErrorCode(9056);
        return JsonUtils.convertToJsonString(error);
    }
}
