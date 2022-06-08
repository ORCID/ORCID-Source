package org.orcid.api.common.analytics;

import java.util.List;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriInfo;

import org.orcid.core.analytics.AnalyticsEventCategory;

public class APIEndpointParser {

    private static final String ORCID_REGEX = "(\\d{4}-){3,}\\d{3}[\\dX]";

    private static final String API_VERSION_REGEX = "v\\d{1,}.*";

    private static final String API_VERSION_2X_REGEX = "v2.*";

    private static final String API_VERSION_3X_REGEX = "v3.*";

    private static final String RECORD_CATEGORY = "record";

    private static final String ORCID_BIO_CATEGORY = "orcid-bio";

    static final String INVALID_URL_CATEGORY = "INVALID-URL";

    private String orcidId;

    private String apiVersion;

    private String category;

    public APIEndpointParser(List<PathSegment> pathSegments) {
        parse(pathSegments);
    }

    private void parse(List<PathSegment> pathSegments) {
        int categoryIndex = 2;
        if (pathSegments.get(0).toString().matches(API_VERSION_REGEX)) {
            // found api version
            apiVersion = pathSegments.get(0).toString();
            if (!pathSegments.get(1).toString().matches(ORCID_REGEX)) {
                // no ORCID iD
                categoryIndex--;
            } else {
                orcidId = pathSegments.get(1).toString();
            }
        } else {
            // no api version
            apiVersion = "";
            categoryIndex--;
            if (!pathSegments.get(0).toString().matches(ORCID_REGEX)) {
                // no ORCID iD
                categoryIndex--;
            } else {
                orcidId = pathSegments.get(0).toString();
            }
        }

        if (pathSegments.size() > categoryIndex) {
            category = pathSegments.get(categoryIndex).toString();
        } else if (apiVersion != null && apiVersion.matches(API_VERSION_2X_REGEX)) {
            // no category in URL: version 2.x so category is record
            category = RECORD_CATEGORY;
        } else if (apiVersion != null && apiVersion.matches(API_VERSION_3X_REGEX)) {
            // same rule applies for v3
            category = RECORD_CATEGORY;
        } else {
            // no category in URL: version 1.x so category is orcid-bio
            category = ORCID_BIO_CATEGORY;
        }

        if (!validAnalyticsEventCategory(category)) {
            category = INVALID_URL_CATEGORY;
        }
    }

    private boolean validAnalyticsEventCategory(String category) {
        for (AnalyticsEventCategory validCategory : AnalyticsEventCategory.values()) {
            if (validCategory.getValue().equals(category)) {
                return true;
            }
        }
        return false;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public String getCategory() {
        return category;
    }

    public String getOrcidId() {
        return orcidId;
    }

}
