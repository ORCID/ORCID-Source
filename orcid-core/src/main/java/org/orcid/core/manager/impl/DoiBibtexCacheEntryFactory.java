package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.orcid.utils.jersey.JerseyClientHelper;
import org.orcid.utils.jersey.JerseyClientResponse;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * 
 * @author Tom Demeranville
 *
 */
public class DoiBibtexCacheEntryFactory implements CacheLoaderWriter<Object, Object> {
    
    private static MediaType X_BIBTEX = new MediaType("application", "x-bibtex");

    @Resource
    private JerseyClientHelper jerseyClientHelper;
    
    /**
     * Keys MUST be URLs
     * 
     */
    @Override
    public Object load(Object key) throws Exception {
        JerseyClientResponse<String, String> cr = jerseyClientHelper.executeGetRequest(key.toString(), X_BIBTEX, true);
        if (cr.getStatus() == Response.Status.OK.getStatusCode()) {
            return cr.getEntity();
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
