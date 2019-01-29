package org.orcid.core.utils.v3.identifiers;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.orcid.core.exception.UnexpectedResponseCodeException;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Component
public class PIDResolverCache {

    //recursively follow up to 4 http->https or https->http redirects
    public static int doProtocolRedirect(HttpURLConnection con, int count) throws IOException{
        int code = con.getResponseCode();
        if ((code == HttpURLConnection.HTTP_SEE_OTHER 
                || code == HttpURLConnection.HTTP_MOVED_PERM 
                || code == HttpURLConnection.HTTP_MOVED_TEMP) && count < 3){
            count ++;
            //need to manually follow 3xx from one protocol to another.
            String loc = con.getHeaderField("Location");
            HttpURLConnection conRedirect = (HttpURLConnection) new URL(loc).openConnection();
            conRedirect.setRequestMethod("HEAD");
            conRedirect.setInstanceFollowRedirects(true);
            return doProtocolRedirect(conRedirect, count);
        }
        return code;
    }
    
    //these caches ensure we only attempt to resolve once if multiple requests to resolve are made.
    //checks for a 200 at the end of a redirect chain (does not handle cookies!)
    LoadingCache<String, Boolean> is200 = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).maximumSize(10000).build(
            new CacheLoader<String, Boolean>() {
                public Boolean load(String url){
                    try {
                        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                        con.setRequestMethod("HEAD");
                        con.setInstanceFollowRedirects(true);
                        int code = doProtocolRedirect(con,0);
                        return (code == HttpURLConnection.HTTP_OK);            
                    } catch (IOException e) {
                        //nothing
                    }  
                    return false;
                }
              });
    
    //checks the link resolves directly to a 303
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
    
    //checks the link resolves directly to metadata via content negotiation.
    LoadingCache<String, Boolean> isValidDOI = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).maximumSize(10000).build(
            new CacheLoader<String, Boolean>() {
                public Boolean load(String url){
                    try {
                        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
                        con.addRequestProperty("Accept", "application/vnd.citationstyles.csl+json");
                        con.setRequestMethod("HEAD");
                        con.setInstanceFollowRedirects(false);
                        return (con.getResponseCode() == HttpURLConnection.HTTP_SEE_OTHER || con.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP);
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
    
    public boolean isValidDOI(String url) {
        try {
            return isValidDOI.get(url);
        } catch (ExecutionException e) {
            return false;
        }
    }
    
    public InputStream get(String url, String accept) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setRequestProperty("User-Agent", con.getRequestProperty("User-Agent")+ " (orcid.org)");
        con.addRequestProperty("Accept", accept);
        con.setRequestMethod("GET");
        con.setInstanceFollowRedirects(true);
        int responseCode = con.getResponseCode();
        if(responseCode == HttpURLConnection.HTTP_OK) {
            return con.getInputStream();
        }
        
        throw new UnexpectedResponseCodeException(responseCode);
    }
}
