package org.orcid.core.utils.v3.identifiers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Component;

@Component
public class ResolverCache {

    //needs cache?
    public boolean isHttp200(String url){
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
            e.printStackTrace();
        }  
        return false;
    }

    public boolean isHttp303(String url) {
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
}
