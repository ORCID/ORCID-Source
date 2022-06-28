package org.orcid.core.orgs.load.io;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

public class OrgDataClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgDataClient.class);        
    
    @Resource
    protected Client jerseyClient;

    /**
     * Attempts to return the entity specified by the Class parameter
     * @param <T> - Type of entity class
     * @param url - url 
     * @param userAgent - user agent
     * @param clazz - Class object for entity class type
     * @return - entity of specified type, retrieved from the specified URL
     */
    public <T> T get(String url, String userAgent, GenericType<T> type) {
        WebTarget webTarget = jerseyClient.target(url);
        Builder builder = webTarget.request().header("User-Agent", userAgent);
        Response response = builder.get(Response.class);
        int status = response.getStatus();
        if (status != 200) {
            LOGGER.warn("Unable to fetch file {}: {}", new Object[] { url, status });
            return null;
        }
        T entity = response.readEntity(type);
        return entity;
    }
    
    /**
     * Downloads a file from the specified url, using the specified userAgent as a header
     * @param url
     * @return boolean indicator of success
     */
    public boolean downloadFile(String url, String localFilePath) {
        try (InputStream crunchifyInputStream = URI.create(url).toURL().openStream()) {
            Files.copy(crunchifyInputStream, Paths.get(localFilePath), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.warn("Unable to fetch file {}: {}", new Object[] { url, e.getMessage() });
            return false;
        }
        return true;
    }

}
