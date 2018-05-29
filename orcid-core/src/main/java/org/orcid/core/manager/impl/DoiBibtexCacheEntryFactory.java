package org.orcid.core.manager.impl;

import org.ehcache.spi.loaderwriter.CacheLoaderWriter;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

/**
 * 
 * @author Tom Demeranville
 *
 */
public class DoiBibtexCacheEntryFactory implements CacheLoaderWriter<Object, Object> {

    private Client client = Client.create();
    private static String X_BIBTEX = "application/x-bibtex";

    public DoiBibtexCacheEntryFactory(){
        client.setFollowRedirects(true);
    }

    /**
     * Keys MUST be URLs
     * 
     */
    @Override
    public Object load(Object key) throws Exception {
        ClientResponse cr = client.resource(key.toString()).accept(X_BIBTEX).get(ClientResponse.class);
        if (cr.getStatus() == Status.OK.getStatusCode()) {
            return cr.getEntity(String.class);
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
