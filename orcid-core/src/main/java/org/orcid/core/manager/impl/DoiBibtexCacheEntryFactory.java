package org.orcid.core.manager.impl;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.ClientResponse.Status;

import net.sf.ehcache.constructs.blocking.CacheEntryFactory;

/**
 * 
 * @author Tom Demeranville
 *
 */
public class DoiBibtexCacheEntryFactory implements CacheEntryFactory {

    private Client client = Client.create();
    private static String X_BIBTEX = "application/x-bibtex";

    public DoiBibtexCacheEntryFactory(){
        client.setFollowRedirects(true);
    }

    /** Keys MUST be URLs
     * 
     */
    @Override
    public Object createEntry(Object key) throws Exception {
        ClientResponse cr = client.resource(key.toString()).accept(X_BIBTEX).get(ClientResponse.class);
        if (cr.getStatus() == Status.OK.getStatusCode()) { 
            return cr.getEntity(String.class);
        }
        return null;
    }

}
