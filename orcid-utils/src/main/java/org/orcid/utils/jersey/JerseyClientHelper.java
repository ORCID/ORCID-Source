package org.orcid.utils.jersey;

import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation.Builder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Component
public class JerseyClientHelper implements DisposableBean {

    protected Client jerseyClient;

    public JerseyClientHelper() {
        this(false);
    }

    public JerseyClientHelper(List<Class<?>> bodyReaders) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        Map<String, Object> jerseyProperties = new HashMap<String, Object>();
        jerseyProperties.put("com.sun.jersey.client.apache4.config.ApacheHttpClient4Config.PROPERTY_CONNECTION_MANAGER", connectionManager);
        jerseyClient = OrcidJerseyClientHandler.create(bodyReaders, jerseyProperties);
    }
    
    public JerseyClientHelper(Boolean isInDevelopmentMode) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        Map<String, Object> jerseyProperties = new HashMap<String, Object>();
        jerseyProperties.put("com.sun.jersey.client.apache4.config.ApacheHttpClient4Config.PROPERTY_CONNECTION_MANAGER", connectionManager);
        jerseyClient = OrcidJerseyClientHandler.create(isInDevelopmentMode, jerseyProperties);
    }

    @Override
    public void destroy() throws Exception {
        if (jerseyClient != null) {
            jerseyClient.close();
        }
    }

    public JerseyClientResponse<String, String> executeGetRequest(String url, MediaType mediaType) {
        return executeGetRequest(url, mediaType, null, false, Map.of(), Map.of(), String.class, String.class);
    }
    
    public JerseyClientResponse<String, String> executeGetRequest(String url, String userAgent) {
        Map<String, String> headers = Map.of("User-Agent", userAgent);
        return executeGetRequest(url, null, null, false, Map.of(), headers, String.class, String.class);
    }
    
    public JerseyClientResponse<String, String> executeGetRequest(String url, MediaType mediaType, Boolean followRedirects) {
        return executeGetRequest(url, mediaType, null, followRedirects, Map.of(), Map.of(), String.class, String.class);
    }

    public JerseyClientResponse<String, String> executeGetRequest(String url, Map<String, String> queryParams) {
        return executeGetRequest(url, null, null, false, queryParams, Map.of(), String.class, String.class);
    }

    public <T, E> JerseyClientResponse<T, E> executeGetRequest(String url, Class<T> responseType, Class<E> errorResponseType) {
        return executeGetRequest(url, null, null, responseType, errorResponseType);
    }
    
    public <T, E> JerseyClientResponse<T, E> executeGetRequest(String url, MediaType mediaType, Class<T> responseType, Class<E> errorResponseType) {
        return executeGetRequest(url, mediaType, null, responseType, errorResponseType);
    }

    public <T, E> JerseyClientResponse<T, E> executeGetRequest(String url, MediaType mediaType, String accessToken, Class<T> responseType, Class<E> errorResponseType) {
        return executeGetRequest(url, mediaType, accessToken, false, Map.of(), Map.of(), responseType, errorResponseType);
    }

    public <T, E> JerseyClientResponse<T, E> executeGetRequest(String url, MediaType mediaType, String accessToken, Map<String, String> queryParams, Class<T> responseType,
            Class<E> errorResponseType) {
        return executeGetRequest(url, mediaType, accessToken, false, queryParams, Map.of(), responseType, errorResponseType);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, E> JerseyClientResponse<T, E> executeGetRequest(String url, MediaType mediaType, String accessToken, Boolean followRedirects, Map<String, String> queryParams,
            Map<String, String> headers, Class<T> responseType, Class<E> errorResponseType) {
        WebTarget webTarget = jerseyClient.target(url);

        if (queryParams != null) {
            for (Entry<String, String> entry : queryParams.entrySet()) {
                webTarget = webTarget.queryParam(entry.getKey(), entry.getValue());
            }
        }

        if (followRedirects == null || !followRedirects) {
            // Follow redirects is set to false by default
            webTarget.property(ClientProperties.FOLLOW_REDIRECTS, Boolean.FALSE);
        } else {
            webTarget.property(ClientProperties.FOLLOW_REDIRECTS, followRedirects);
        }

        Builder builder;

        if (mediaType == null) {
            builder = webTarget.request();
        } else {
            builder = webTarget.request(mediaType);
        }

        if (!StringUtils.isEmpty(accessToken)) {
            builder = builder.header("Authorization", "Bearer " + accessToken);
        }

        if(headers != null) {
            for (String key : headers.keySet()) {
                builder = builder.header(key, headers.get(key));
            }
        }
        
        Response response = builder.get(Response.class);
        JerseyClientResponse<T, E> jcr;
        if (response.getStatus() == Response.Status.OK.getStatusCode()) {
            // Terrible hack! https://zenodo.org/api/files/25d4f93f-6854-4dd4-9954-173197e7fad7/v1.0-2022-03-17-ror-data.json.zip comes with a duplicated 
            // Content-Type header which is breaking the jersey
            String contentType = response.getHeaderString("Content-Type");
            if(contentType.equals("application/octet-stream,application/octet-stream")) {
                response.getHeaders().putSingle("Content-Type", "application/octet-stream");
            }            
            jcr = new JerseyClientResponse(response.getStatus(), response.readEntity(responseType), null);
        } else {
            // Try to obtain the error object if available
            E errorEntity = null;
            try {
                errorEntity = response.readEntity(errorResponseType);
            } catch (Exception e) {
                // Do nothing
            }
            jcr = new JerseyClientResponse(response.getStatus(), null, errorEntity);
        }

        return jcr;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <T, E> JerseyClientResponse<T, E> executePostRequest(String url, MediaType mediaType, Object message, SimpleEntry<String, String> auth, Class<T> responseType,
            Class<E> errorResponseType) {

        final Client client = ClientBuilder.newClient();
        
        if (auth != null) {
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(auth.getKey(), auth.getValue());
            client.register(feature);
        }
        WebTarget webTarget = client.target(url);
        Builder builder;

        if (mediaType != null) {
            builder = webTarget.request();
        } else {
            builder = webTarget.request(mediaType);
        }        

        Response response = builder.post(Entity.entity(message, mediaType));

        JerseyClientResponse<T, E> jcr;
        if (response.getStatus() == 200) {
            jcr = new JerseyClientResponse(response.getStatus(), response.readEntity(responseType), null);
        } else {
            jcr = new JerseyClientResponse(response.getStatus(), null, response.readEntity(errorResponseType));
        }

        return jcr;
    }
}
