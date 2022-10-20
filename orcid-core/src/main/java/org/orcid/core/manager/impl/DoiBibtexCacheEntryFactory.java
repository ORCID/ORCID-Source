package org.orcid.core.manager.impl;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import javax.annotation.Resource;

import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.orcid.core.utils.http.HttpRequestUtils;

/**
 * 
 * @author Tom Demeranville
 *
 */
public class DoiBibtexCacheEntryFactory implements CacheLoaderWriter<Object, Object> {
    
    @Resource
    private HttpRequestUtils httpRequestUtils;
    
    /**
     * Keys MUST be URLs
     * 
     */
    @Override
    public Object load(Object key) throws Exception {
        HttpResponse<String> response = httpRequestUtils.doGet(key.toString(), "application/x-bibtex", HttpClient.Redirect.ALWAYS);
        if (response.statusCode() == 200) {
            return response.body();
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
