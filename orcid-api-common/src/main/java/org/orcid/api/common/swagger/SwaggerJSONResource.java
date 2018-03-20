package org.orcid.api.common.swagger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.core.api.OrcidApiConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.swagger.annotations.ApiOperation;
import io.swagger.config.FilterFactory;
import io.swagger.config.Scanner;
import io.swagger.config.ScannerFactory;
import io.swagger.config.SwaggerConfig;
import io.swagger.converter.ModelConverters;
import io.swagger.core.filter.SpecFilter;
import io.swagger.core.filter.SwaggerSpecFilter;
import io.swagger.jaxrs.Reader;
import io.swagger.jaxrs.config.ReaderConfigUtils;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import io.swagger.models.Swagger;
import io.swagger.util.Yaml;

/** Adapted version of APIListingResource that works with our spring-jersey based server.  
 * It removes the need for a ServletContext when initialising, but is otherwise unchanged.
 * 
 * @author tom
 *
 */
public class SwaggerJSONResource {
    
    static boolean initialized = false;
    Logger LOGGER = LoggerFactory.getLogger(SwaggerJSONResource.class);
    @Context
    ServletContext context;

    protected synchronized Swagger scan(Application app) {
        Swagger swagger = null;
        Scanner scanner = ScannerFactory.getScanner();
        ModelConverters.getInstance().addConverter(new SwaggerModelConverter());
        LOGGER.debug("[SWAGGER] using scanner " + scanner);

        if (scanner != null) {
            SwaggerSerializers.setPrettyPrint(scanner.getPrettyPrint());
            swagger = (Swagger) context.getAttribute("swagger");

            Set<Class<?>> classes = scanner.classes();
            if (classes != null) {
                Reader reader = new Reader(swagger, ReaderConfigUtils.getReaderConfig(context));
                swagger = reader.read(classes);
                if (scanner instanceof SwaggerConfig) {
                    swagger = ((SwaggerConfig) scanner).configure(swagger);
                } else {
                    SwaggerConfig configurator = (SwaggerConfig) context.getAttribute("reader");
                    if (configurator != null) {
                        LOGGER.debug("configuring swagger with " + configurator);
                        configurator.configure(swagger);
                        
                    } else {
                        LOGGER.debug("no configurator");
                    }
                }
                context.setAttribute("swagger", swagger);
            }
        }        
        initialized = true;
        return swagger;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path(OrcidApiConstants.SWAGGER_FILE)
    @ApiOperation(value = "The swagger definition in JSON", hidden = true)
    public Response getListingJson(
            @Context Application app,
            @Context HttpHeaders headers,
            @Context UriInfo uriInfo) {
        Swagger swagger = (Swagger) context.getAttribute("swagger");
        if (!initialized) {
            swagger = scan(app);
        }
        if (swagger != null) {
            SwaggerSpecFilter filterImpl = FilterFactory.getFilter();
            if (filterImpl != null) {
                SpecFilter f = new SpecFilter();
                swagger = f.filter(swagger,
                        filterImpl,
                        getQueryParams(uriInfo.getQueryParameters()),
                        getCookies(headers),
                        getHeaders(headers));
            }
            return Response.ok().entity(swagger).build();
        } else {
            return Response.status(404).build();
        }
    }

    @GET
    @Produces("application/yaml")
    @Path(OrcidApiConstants.SWAGGER_FILE_YAML)
    @ApiOperation(value = "The swagger definition in YAML", hidden = true)
    public Response getListingYaml(
            @Context Application app,
            @Context HttpHeaders headers,
            @Context UriInfo uriInfo) {
        Swagger swagger = (Swagger) context.getAttribute("swagger");
        if (!initialized) {
            swagger = scan(app);
        }
        try {
            if (swagger != null) {
                SwaggerSpecFilter filterImpl = FilterFactory.getFilter();
                LOGGER.debug("using filter " + filterImpl);
                if (filterImpl != null) {
                    SpecFilter f = new SpecFilter();
                    swagger = f.filter(swagger,
                            filterImpl,
                            getQueryParams(uriInfo.getQueryParameters()),
                            getCookies(headers),
                            getHeaders(headers));
                }

                String yaml = Yaml.mapper().writeValueAsString(swagger);
                String[] parts = yaml.split("\n");
                StringBuilder b = new StringBuilder();
                for (String part : parts) {
                    //int pos = part.indexOf("!<");
                    //int endPos = part.indexOf(">");
                    b.append(part);
                    b.append("\n");
                }
                return Response.ok().entity(b.toString()).type("application/yaml").build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Response.status(404).build();
    }

    protected Map<String, List<String>> getQueryParams(MultivaluedMap<String, String> params) {
        Map<String, List<String>> output = new HashMap<String, List<String>>();
        if (params != null) {
            for (String key : params.keySet()) {
                List<String> values = params.get(key);
                output.put(key, values);
            }
        }
        return output;
    }

    protected Map<String, String> getCookies(HttpHeaders headers) {
        Map<String, String> output = new HashMap<String, String>();
        if (headers != null) {
            for (String key : headers.getCookies().keySet()) {
                Cookie cookie = headers.getCookies().get(key);
                output.put(key, cookie.getValue());
            }
        }
        return output;
    }


    protected Map<String, List<String>> getHeaders(HttpHeaders headers) {
        Map<String, List<String>> output = new HashMap<String, List<String>>();
        if (headers != null) {
            for (String key : headers.getRequestHeaders().keySet()) {
                List<String> values = headers.getRequestHeaders().get(key);
                output.put(key, values);
            }
        }
        return output;
    }
    
}