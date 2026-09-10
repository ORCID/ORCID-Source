package org.orcid.core.manager.impl;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import jakarta.annotation.Resource;

import com.github.benmanes.caffeine.cache.CacheLoader;
import org.orcid.core.utils.http.HttpRequestUtils;

/**
 * 
 * @author Tom Demeranville
 *
 */
public class DoiBibtexCacheEntryFactory implements CacheLoader<Object, Object> {
    
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
}
