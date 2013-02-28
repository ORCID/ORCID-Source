/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.manager.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.orcid.core.crossref.CrossRefMetadata;
import org.orcid.core.manager.CrossRefManager;
import org.orcid.core.utils.ContentTypeFromTextToJsonClientFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.GenericType;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.client.apache4.ApacheHttpClient4;

/**
 * 
 * @author Will Simpson
 * 
 */
public class CrossRefManagerImpl implements CrossRefManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CrossRefManagerImpl.class);
	
	/* sample url get
	 * old: http://crossref.org/sigg/sigg/FindWorks?version=1&access=API_KEY&format=json&op=OR&expression=Laura+Paglione
	 * new: http://search.crossref.org/dois?content-type=application%2Fjson&op=OR&access=API_KEY&version=1&q=Laura+Paglione
	 *
	 * Documentation: http://search.crossref.org/help/api
	 */
    private String crossRefUrl = "http://search.crossref.org/dois";
    private static final String queryString = "content-type=application%2Fjson&op=OR&access=API_KEY&version=1&q=";

    public void setCrossRefUrl(String crossRefUrl) {
        this.crossRefUrl = crossRefUrl;
    }

    @Override
    public List<CrossRefMetadata> searchForMetadata(String searchTerms) {
    	WebResource resource = createResource(searchTerms);
        return resource.get(new GenericType<ArrayList<CrossRefMetadata>>() {});
    }

    @Override
    public String searchForMetadataAsString(String searchTerms) {
        WebResource resource = createResource(searchTerms);
        return resource.post(String.class);
    }

    private WebResource createResource(String searchTerms) {
    	 Client client = createClient();
      	 String encoded = null;
    	 try {
    		encoded = URLEncoder.encode(searchTerms,"UTF-8");
    	 } catch (UnsupportedEncodingException e) {
    		LOGGER.error("search terms: "+ searchTerms + "cannot be encoded", e);
    	 }
         WebResource resource = client.resource(crossRefUrl+"?"+queryString + encoded);
         return resource;
    }

    private Client createClient() {
        Set<Class<?>> providers = new HashSet<Class<?>>();
        providers.add(JacksonJsonProvider.class);
        ClientConfig config = new DefaultClientConfig(providers);
        config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
        Client client = ApacheHttpClient4.create(config);
        client.setFollowRedirects(true);
        client.addFilter(new ContentTypeFromTextToJsonClientFilter());
        return client;
    }

}
