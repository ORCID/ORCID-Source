package org.orcid.api.common.filter;

import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.web.filters.ApiVersionFilter;
import org.orcid.jaxb.model.v3.rc2.error.OrcidError;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

public class Disable20RCApiFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Disable20RCApiFilter.class);

    private static final String API_20_RC = "2.0_rc";

    private String JSON_RESPONSE;

    private String XML_RESPONSE;

    @Resource
    private LocaleManager localeManager;

    private String getXmlResponse() throws JAXBException {
        if (XML_RESPONSE == null) {
            OrcidError error = new OrcidError();
            error.setDeveloperMessage(localeManager.resolveMessage("apiError.9056.developerMessage"));
            error.setUserMessage(localeManager.resolveMessage("apiError.9056.userMessage"));
            error.setResponseCode(HttpServletResponse.SC_MOVED_PERMANENTLY);
            error.setErrorCode(9056);

            JAXBContext context = JAXBContext.newInstance(error.getClass());
            StringWriter writer = new StringWriter();
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(error, writer);
            this.XML_RESPONSE = writer.toString();
        }
        return XML_RESPONSE;
    }

    private String getJsonResponse() {
        if (JSON_RESPONSE == null) {
            OrcidError error = new OrcidError();
            error.setDeveloperMessage(localeManager.resolveMessage("apiError.9056.developerMessage"));
            error.setUserMessage(localeManager.resolveMessage("apiError.9056.userMessage"));
            error.setResponseCode(HttpServletResponse.SC_MOVED_PERMANENTLY);
            error.setErrorCode(9056);
            JSON_RESPONSE = JsonUtils.convertToJsonString(error);
        }
        return JSON_RESPONSE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String version = (String) request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME);
        
        if (PojoUtil.isEmpty(version)) {
            filterChain.doFilter(request, response);
        } else if (version.startsWith(API_20_RC) && Features.V2_DISABLE_RELEASE_CANDIDATES.isActive()) {
            String v20Location;
            String fullPath = request.getRequestURL() == null ? "" : request.getRequestURL().toString();
            
            if(fullPath.startsWith("https://localhost:")) {
                v20Location = fullPath.replaceFirst("_(rc1|rc2|rc3|rc4)", "");
            } else {
                String path = OrcidUrlManager.getPathWithoutContextPath(request);
                v20Location = path.replaceFirst("_(rc1|rc2|rc3|rc4)", "");
            }
            
            LOGGER.info("Redirecting request '{}' to '{}'", fullPath, v20Location);
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
            response.setHeader("Location", v20Location);
            String accept = request.getHeader("Accept") == null ? null : request.getHeader("Accept").toLowerCase();
            if (accept.contains("json")) {
                response.getWriter().println(getJsonResponse());
            } else {
                try {
                    response.getWriter().println(getXmlResponse());
                } catch (Exception e) {
                    LOGGER.error("Unable to generate XML message", e);
                    response.getWriter().println(localeManager.resolveMessage("apiError.9056.developerMessage"));
                }
            }

        } else {
            filterChain.doFilter(request, response);
        }
    }
}
