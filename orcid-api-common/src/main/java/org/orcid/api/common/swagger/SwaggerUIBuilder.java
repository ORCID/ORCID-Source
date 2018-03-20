package org.orcid.api.common.swagger;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.orcid.core.api.OrcidApiConstants;
import org.orcid.utils.ReleaseNameUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Builds the swagger UI HTML pages using freemarker for use by JAX-RS
 * resources.
 * 
 * @author tom
 *
 */
public class SwaggerUIBuilder {

    private static Configuration freeMarkerConfiguration;
    private static final String SWAGGER_UI_FTL = "swagger-ui.ftl";
    private static final String SWAGGER_O2C_FTL = "swagger-o2c.ftl";
    private static final String SWAGGER_FTL_PATH = "/org/orcid/api/common/swagger";
    private static final String SWAGGER_STATIC_HTML_PATH = "/static/" + ReleaseNameUtils.getReleaseName() + "/swagger/";

    public SwaggerUIBuilder() {
        if (freeMarkerConfiguration == null) {
            configureFreemarker();
        }
    }

    /**
     * Build the swagger UI HTML page
     *
     * @param baseUri
     *            the URL of the main website. e.g. http://orcid.org
     * @param apiUri
     *            the URL of the API e.g. http://pub.orcid.org
     * @param showOAuth
     *            if true, input boxes allowing user to enter client id and
     *            secret will be shown
     * @return a 200 response containing the HTML as text.
     */
    public Response buildSwaggerHTML(String baseUri, String apiUri, boolean showOAuth) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("swaggerJsonUrl", apiUri + OrcidApiConstants.SWAGGER_PATH + OrcidApiConstants.SWAGGER_FILE);
        map.put("swaggerBaseUrl", baseUri + SWAGGER_STATIC_HTML_PATH);
        map.put("showOAuth", showOAuth);
        map.put("baseUri", baseUri);
        map.put("apiUri", apiUri);
        try {
            Template template = freeMarkerConfiguration.getTemplate(SWAGGER_UI_FTL);
            StringWriter result = new StringWriter();
            template.process(map, result);
            return Response.ok(result.toString()).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Build the swagger page that handles OAuth returns.
     * 
     * @return a Response wrapping the HTML text
     */
    public Response buildSwaggerO2CHTML() {
        try {
            Template template = freeMarkerConfiguration.getTemplate(SWAGGER_O2C_FTL);
            return Response.ok(template.toString()).build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * We only need to configure freemarker once
     * 
     */
    private static synchronized void configureFreemarker() {
        if (freeMarkerConfiguration == null) {
            freeMarkerConfiguration = new Configuration();
            freeMarkerConfiguration.setClassForTemplateLoading(SwaggerUIBuilder.class, SWAGGER_FTL_PATH);
        }
    }
}
