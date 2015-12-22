package org.orcid.core.web.filters;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.ext.Provider;

import org.orcid.core.exception.OrcidBadRequestException;

import com.sun.jersey.api.core.InjectParam;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

@Provider
public class ApiVersionCheckFilter implements ContainerRequestFilter {

    @InjectParam("v1xDisabled")
    private Boolean v1xDisabled;

    private static final Pattern VERSION_PATTERN = Pattern.compile("v(\\d.*?)/");

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        String path = request.getPath();
        Matcher matcher = VERSION_PATTERN.matcher(path);
        String version = null;
        if (matcher.lookingAt()) {
            version = matcher.group(1);
        }

        if (version != null && version.startsWith("1.1") && v1xDisabled) {
            throw new OrcidBadRequestException("API Version 1.1 is not enabled");
        }

        return request;
    }
}