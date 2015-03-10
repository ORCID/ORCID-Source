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
package org.orcid.core.api;

public class OrcidApiConstants {

    public static final String EXPERIMENTAL_RDF_V1 = "/experimental_rdf_v1";

    public static final String PROFILE_POST_PATH = "/orcid-profile";
    public static final String PROFILE_GET_PATH = "/{orcid}" + PROFILE_POST_PATH;
    public static final String PROFILE_DELETE_PATH = "/{orcid}" + PROFILE_POST_PATH;
    public static final String BIO_PATH = "/{orcid:[^/]+}{ignore:(/orcid-bio)?}"; 
    public static final String BIO_PATH_NO_REGEX = "/{orcid}/orcid-bio";
    public static final String AFFILIATIONS_PATH = "/{orcid}/affiliations";
    public static final String FUNDING_PATH = "/{orcid}/funding"; 
    public static final String WORKS_PATH = "/{orcid}/orcid-works";
    public static final String EXTERNAL_IDENTIFIER_PATH = "/{orcid}/orcid-bio/external-identifiers";
    public static final String STATUS_PATH = "/status";
    public static final String BIO_SEARCH_PATH = "/search/orcid-bio";
    public static final String WEBHOOKS_PATH = "/{orcid}/webhook/{webhook_uri}";
    public static final String AUTHENTICATE_PATH = "/{orcid}/authenticate";
    public static final String CLIENT_PATH = "/client/{client_id:[^/]+}";
    public static final String NOTIFICATIONS_PATH ="/{orcid}/notifications";
    public static final String ADD_ACTIVITIES_PATH = "/add-activities";
    public static final String ADD_ACTIVITIES_VIEW_PATH = "/add-activities/{id}";
    public static final String RECORD = "/{orcid}/record";
    public static final String ACTIVITIES = "/{orcid}/activities";
    public static final String WORK = "/{orcid}/work";
    public static final String WORK_SUMMARY = "/{orcid}/work/summary";
    public static final String FUNDING = "/{orcid}/funding";
    public static final String FUNDING_SUMMARY = "/{orcid}/funding/summary";
    public static final String EDUCATION = "/{orcid}/education";
    public static final String EDUCATION_SUMMARY = "/{orcid}/education/summary";
    public static final String EMPLOYMENT = "/{orcid}/employment";
    public static final String EMPLOYMENT_SUMMARY = "/{orcid}/employment/summary";
    public static final String PUTCODE = "/{putCode}"; // concated on the end of other paths like FUNDINGS
    public static final String ADD_ACTIVITIES_FLAG_AS_ARCHIVED_PATH = "/add-activities/{id}/archive";
    public static final String ERROR = "/error";
    
    public static final String ORCID_XML = "application/orcid+xml; qs=3";
    public static final String ORCID_JSON = "application/orcid+json; qs=2";
    public static final String TEXT_TURTLE = "text/turtle; qs=3";
    public static final String TEXT_N3 = "text/n3; qs=2";

    public static final String APPLICATION_RDFXML = "application/rdf+xml; qs=2";
    public static final String VND_ORCID_XML = "application/vnd.orcid+xml; qs=5";
    public static final String VND_ORCID_JSON = "application/vnd.orcid+json; qs=4";

    public static final String HTML = "text/html; qs=1";

    public static final String STATUS_OK_MESSAGE = "OK I am here";

}       
