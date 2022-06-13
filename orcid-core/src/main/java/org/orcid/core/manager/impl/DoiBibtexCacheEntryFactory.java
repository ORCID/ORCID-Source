package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.orcid.utils.rest.RESTHelper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

/**
 * 
 * @author Tom Demeranville
 *
 */
public class DoiBibtexCacheEntryFactory implements CacheLoaderWriter<Object, Object> {

    @Resource
    private RESTHelper restHelper;
    
    private static String X_BIBTEX = "application/x-bibtex";

    /**
     * Keys MUST be URLs
     * 
     */
    @Override
    public Object load(Object key) throws Exception {
        Response response = restHelper.executeGetRequest(key.toString(), true, X_BIBTEX);
        if (response.getStatus() == Status.OK.getStatusCode()) {
            return response.readEntity(String.class);
        }
        return null;
    }

    @Override
    public void write(Object key, Object value) throws Exception {
        // Not needed, populating only

    }

    @Override
    public void delete(Object key) throws Exception {
        // Not needed, populating only

    }

}
