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
package org.orcid.api.common;

public class OrcidApiConstants {

    public static final String PROFILE_POST_PATH = "/orcid-profile";
    public static final String PROFILE_GET_PATH = "/{orcid}" + PROFILE_POST_PATH;
    public static final String PROFILE_DELETE_PATH = "/{orcid}" + PROFILE_POST_PATH;
    public static final String BIO_PATH = "/{orcid:[^/]+}{ignore:(/orcid-bio)?}";
    public static final String BIO_PATH_NO_REGEX = "/{orcid}/orcid-bio";
    public static final String WORKS_PATH = "/{orcid}/orcid-works";
    public static final String EXTERNAL_IDENTIFIER_PATH = "/{orcid}/orcid-bio/external-identifiers";
    public static final String STATUS_PATH = "/status";
    public static final String BIO_SEARCH_PATH = "/search/orcid-bio";
    public static final String WEBHOOKS_PATH = "/{orcid}/webhook/{webhook_uri}";

    public static final String ORCID_XML = "application/orcid+xml; qs=3";
    public static final String ORCID_JSON = "application/orcid+json; qs=2";
    public static final String TEXT_TURTLE = "text/turtle; qs=3";
    public static final String TEXT_TURTLE_UTF8 = "text/turtle; charset=utf8; qs=3";
    public static final String TEXT_N3 = "text/n3; qs=2";
    public static final String TEXT_N3_UTF8 = "text/n3; charset=utf8; qs=2";

    public static final String APPLICATION_RDFXML = "application/rdf+xml; qs=2";
    public static final String VND_ORCID_XML = "application/vnd.orcid+xml; qs=5";
    public static final String VND_ORCID_JSON = "application/vnd.orcid+json; qs=4";

    public static final String HTML = "text/html; qs=1";

    public static final String STATUS_OK_MESSAGE = "OK I am here";

}
