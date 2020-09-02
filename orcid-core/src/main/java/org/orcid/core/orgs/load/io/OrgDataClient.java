package org.orcid.core.orgs.load.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;

public class OrgDataClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgDataClient.class);        
    
    private Client client;

    /**
     * To be called before any set of operations involving the OrgDataClient
     */
    public void init() {
        client = Client.create();
    }
    
    /**
     * To be called after any set of operations involving the OrgDataClient
     */
    public void cleanUp() {
        client.destroy();
    }

    /**
     * Attempts to return the entity specified by the Class parameter
     * @param <T> - Type of entity class
     * @param url - url 
     * @param userAgent - user agent
     * @param clazz - Class object for entity class type
     * @return - entity of specified type, retrieved from the specified URL
     */
    public <T> T get(String url, String userAgent, GenericType<T> type) {
        WebResource resource = client.resource(url);
        ClientResponse response = resource.header("User-Agent", userAgent).get(ClientResponse.class);
        int status = response.getStatus();
        if (status != 200) {
            LOGGER.warn("Unable to fetch file {}: {}", new Object[] { url, status });
            return null;
        }
        T entity = response.getEntity(type);
        return entity;
    }
    
    /**
     * Downloads a file from the specified url, using the specified userAgent as a header
     * @param url
     * @param userAgent
     * @return boolean indicator of success
     */
    public boolean downloadFile(String url, String userAgent, String localFilePath) {
        WebResource resource = client.resource(url);
        ClientResponse response = resource.header("User-Agent", userAgent).get(ClientResponse.class);
        int status = response.getStatus();
        if (status != 200) {
            LOGGER.warn("Unable to fetch file {}: {}", new Object[] { url, status });
            return false;
        }
        try (InputStream data = response.getEntityInputStream(); OutputStream outputStream = new FileOutputStream(new File(localFilePath))) {
            IOUtils.copy(data, outputStream);
            return true;
        } catch (IOException e) {
            LOGGER.error("Error writing to local file", e);
            return false;
        }
    }

}
