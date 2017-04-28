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
package org.orcid.core.analytics;

import java.util.List;

import javax.ws.rs.core.PathSegment;

import com.sun.jersey.spi.container.ContainerRequest;

public class APIEndpointParser {

    private static final String ORCID_REGEX = "(\\d{4}-){3,}\\d{3}[\\dX]";

    private static final String API_VERSION_REGEX = "v\\d{1,}.*";

    private static final String API_VERSION_2X_REGEX = "v2.*";

    private static final String RECORD_CATEGORY = "record";

    private static final String ORCID_BIO_CATEGORY = "orcid-bio";

    private String apiVersion;

    private String category;

    public APIEndpointParser(ContainerRequest request) {
        parse(request);
    }

    private void parse(ContainerRequest request) {
        List<PathSegment> path = request.getPathSegments(true);
        int categoryIndex = 2;
        if (path.get(0).toString().matches(API_VERSION_REGEX)) {
            // found api version
            apiVersion = path.get(0).toString();
            if (!path.get(1).toString().matches(ORCID_REGEX)) {
                // no ORCID iD
                categoryIndex--;
            }
        } else {
            // no api version
            apiVersion = "";
            categoryIndex--;
            if (!path.get(0).toString().matches(ORCID_REGEX)) {
                // no ORCID iD
                categoryIndex--;
            }
        }

        if (path.size() > categoryIndex) {
            category = path.get(categoryIndex).toString();
        } else if (apiVersion != null && apiVersion.matches(API_VERSION_2X_REGEX)) {
            // no category in URL: version 2.x so category is record
            category = RECORD_CATEGORY;
        } else {
            // no category in URL: version 1.x so category is orcid-bio
            category = ORCID_BIO_CATEGORY;
        }
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getCategory() {
        return category;
    }

}
