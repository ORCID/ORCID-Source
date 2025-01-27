package org.orcid.scheduler.loader.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrgDataClient {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgDataClient.class);        
    
    @Resource(name = "jerseyClientHelperForOrgLoaders")
    private JerseyClientHelper jerseyClientHelperForOrgLoaders;
    
    /**
     * Attempts to return the entity specified by the Class parameter
     * @param <T> - Type of entity class
     * @param url - url 
     * @param userAgent - user agent
     * @param clazz - Class object for entity class type
     * @return - entity of specified type, retrieved from the specified URL
     */
    public <T> T get(String url, String userAgent, Class<T> type) {
        JerseyClientResponse<T, String> response = jerseyClientHelperForOrgLoaders.executeGetRequest(url, null, null, false, Map.of(), Map.of("User-Agent", userAgent), type, String.class);
        int status = response.getStatus();      
        if (status != 200) {
            LOGGER.error("Unable to fetch file {}: {}", new Object[] { url, status });
            return null;
        }
        return response.getEntity();
    }
    
    /**
     * Downloads a file from the specified url, using the specified userAgent as a header
     * @param url
     * @param userAgent
     * @return boolean indicator of success
     */
    public boolean downloadFile(String url, String userAgent, String localFilePath) {
        LOGGER.info("About to download file {}", url);
        JerseyClientResponse<InputStream, String> response = jerseyClientHelperForOrgLoaders.executeGetRequest(url, null, null, false, Map.of(), Map.of("User-Agent", userAgent), InputStream.class, String.class);
        int status = response.getStatus();
        if (status != 200) {
            LOGGER.warn("Unable to fetch file {}: {}", new Object[] { url, status });
            return false;
        }
        try (InputStream data = response.getEntity(); OutputStream outputStream = new FileOutputStream(new File(localFilePath))) {
            IOUtils.copy(data, outputStream);
            return true;
        } catch (IOException e) {
            LOGGER.error("Error writing to local file", e);
            return false;
        }
    }

}
