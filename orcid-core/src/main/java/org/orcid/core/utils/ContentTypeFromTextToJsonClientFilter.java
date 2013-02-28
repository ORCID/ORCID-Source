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
package org.orcid.core.utils;

import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

/**
 * 
 * @author Will Simpson
 * 
 */
public class ContentTypeFromTextToJsonClientFilter extends ClientFilter {

    public ClientResponse handle(ClientRequest cr) {
        // Call the next filter
        ClientResponse resp = getNext().handle(cr);
        String contentType = resp.getHeaders().getFirst("Content-Type");
        if (contentType.startsWith("text/plain")) {
            String newContentType = "application/json" + contentType.substring(10);
            resp.getHeaders().putSingle("Content-Type", newContentType);
        }
        return resp;
    }

}