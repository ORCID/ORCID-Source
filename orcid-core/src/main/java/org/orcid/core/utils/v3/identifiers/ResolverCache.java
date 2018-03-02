/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.utils.v3.identifiers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Component
public class ResolverCache {

    //these caches ensure we only attempt to resolve once if multiple requests to resolve are made.
    LoadingCache<String, Boolean> is200 = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).maximumSize(10000).build(
            new CacheLoader<String, Boolean>() {
                public Boolean load(String url){
                    try {
                        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                        con.setRequestMethod("HEAD");
                        con.setInstanceFollowRedirects(true);
                        int code = con.getResponseCode();
                        if (code == HttpURLConnection.HTTP_SEE_OTHER 
                                || code == HttpURLConnection.HTTP_MOVED_PERM 
                                || code == HttpURLConnection.HTTP_MOVED_TEMP){
                            //need to manually follow 303 from one protocol to another.
                            String loc = con.getHeaderField("Location");
                            con = (HttpURLConnection) new URL(loc).openConnection();
                            con.setRequestMethod("HEAD");
                            con.setInstanceFollowRedirects(true);    
                        }
                        return (con.getResponseCode() == HttpURLConnection.HTTP_OK);            
                    } catch (IOException e) {
                        //nothing
                    }  
                    return false;
                }
              });
    
    LoadingCache<String, Boolean> is303 = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).maximumSize(10000).build(
            new CacheLoader<String, Boolean>() {
                public Boolean load(String url){
                    try {
                        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                        con.setRequestMethod("HEAD");
                        con.setInstanceFollowRedirects(false);
                        return (con.getResponseCode() == HttpURLConnection.HTTP_SEE_OTHER);
                    } catch (IOException e) {
                        //nope.
                    }  
                    return false;
                }
            });
    
    public boolean isHttp200(String url){
        try {
            return is200.get(url);
        } catch (ExecutionException e) {
            return false;
        }
    }

    public boolean isHttp303(String url) {
        try {
            return is303.get(url);
        } catch (ExecutionException e) {
            return false;
        }
    }
}
