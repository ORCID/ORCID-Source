package org.orcid.api.common.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.togglz.Features;
import org.orcid.core.utils.JsonUtils;
import org.orcid.core.web.filters.ApiVersionFilter;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

public class Disable12ApiFilter extends OncePerRequestFilter {
    private static final String API_12_version = "1.2";
    private final String JSON_ERROR_MESSAGE;
    private final String XML_ERROR_MESSAGE;

    {
        OrcidMessage error = new OrcidMessage();
        error.setErrorDesc(new ErrorDesc("API 1.2 is disabled, please upgrade to the 2.0 API https://members.orcid.org/api/news/xsd-20-update"));
        JSON_ERROR_MESSAGE = JsonUtils.convertToJsonString(error);
        XML_ERROR_MESSAGE = error.toXmlString();
    }

    @Resource
    protected OrcidUrlManager orcidUrlManager;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    protected Features feature;

    protected final List<String> membersToKeepItActive;

    public Disable12ApiFilter(@Value("${org.orcid.api.1.2.active.for.clients:}") String exclusionList) {
        if (!PojoUtil.isEmpty(exclusionList)) {
            String[] clientList = exclusionList.split(",");
            membersToKeepItActive = Arrays.asList(clientList);
        } else {
            membersToKeepItActive = new ArrayList<String>();
        }
    }

    public void setFeature(Features f) {
        this.feature = f;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String version = (String) request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME);
        if (PojoUtil.isEmpty(version)) {
            filterChain.doFilter(request, response);
        } else if (version.startsWith(API_12_version) && feature.isActive()) {
            String clientId = orcidSecurityManager.getClientIdFromAPIRequest();
            if (membersToKeepItActive.contains(clientId)) {
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String accept = request.getHeader("Accept") == null ? null : request.getHeader("Accept").toLowerCase();
                if (accept.contains("json")) {
                    response.getWriter().println(JSON_ERROR_MESSAGE);
                } else {
                    response.getWriter().println(XML_ERROR_MESSAGE);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
