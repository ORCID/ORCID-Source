package org.orcid.core.orgs.load.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class HttpFileDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpFileDownloader.class);

    private String url;

    private String localFilePath;

    private Client client = Client.create();
    
    @Value("${org.orcid.core.clients.userAgent}")
    private String userAgent;

    public boolean downloadFile() {
        WebResource resource = client.resource(url);
        return download(resource);
    }
    
    public boolean downloadFile(MultivaluedMap<String, String> params) {
        WebResource resource = client.resource(url).queryParams(params);
        return download(resource);
    }
    
    private boolean download(WebResource resource) {
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

}
