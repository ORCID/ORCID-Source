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
    public static final String PROFILE_ROOT_PATH = "/{orcid}";
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
    public static final String PERMISSIONS_PATH = "/{orcid}/notification-permission";
    public static final String PERMISSIONS_VIEW_PATH = "/{orcid}/notification-permission/{id}";
    public static final String RECORD = "/{orcid:[^/]+}{ignore:(/record)?}";
    public static final String ACTIVITIES = "/{orcid}/activities";
    public static final String WORK = "/{orcid}/work";
    public static final String WORKS = "/{orcid}/works";
    public static final String WORK_SUMMARY = "/{orcid}/work/summary";
    public static final String FUNDING = "/{orcid}/funding";
    public static final String FUNDINGS = "/{orcid}/fundings";
    public static final String FUNDING_SUMMARY = "/{orcid}/funding/summary";
    public static final String EDUCATION = "/{orcid}/education";
    public static final String EDUCATIONS = "/{orcid}/educations";
    public static final String EDUCATION_SUMMARY = "/{orcid}/education/summary";
    public static final String EMPLOYMENT = "/{orcid}/employment";
    public static final String EMPLOYMENTS = "/{orcid}/employments";
    public static final String EMPLOYMENT_SUMMARY = "/{orcid}/employment/summary";
    public static final String PUTCODE = "/{putCode}"; // concated on the end of
                                                       // other paths like
                                                       // FUNDINGS
    public static final String PEER_REVIEW = "/{orcid}/peer-review";
    public static final String PEER_REVIEWS = "/{orcid}/peer-reviews";
    public static final String PEER_REVIEW_SUMMARY = "/{orcid}/peer-review/summary";
    public static final String GROUP_ID_RECORD = "/group-id-record";
    public static final String STATS_PATH = "/statistics";
    public static final String IDENTIFIER_PATH = "/identifiers";
    public static final String STATS = "/{type}";
    public static final String STATS_ALL = "/all";
    public static final String CERIF_PATH = "/cerif/1_0";
    public static final String CERIF_PERSONS_PATH = "/persons/{id}";
    public static final String CERIF_PUBLICATIONS_PATH = "/publications/{id}";
    public static final String CERIF_PRODUCTS_PATH = "/products/{id}";
    public static final String CERIF_ENTITIES_PATH = "/entities";
    public static final String CERIF_SEMANTICS_PATH = "/semantics";
    public static final String ERROR = "/error";
    public static final String ORCID_XML = "application/orcid+xml; qs=3";
    public static final String ORCID_JSON = "application/orcid+json; qs=2";
    public static final String TEXT_TURTLE = "text/turtle; qs=3";
    public static final String TEXT_N3 = "text/n3; qs=2";
    public static final String N_TRIPLES = "application/n-triples; qs=3";
    public static final String JSON_LD = "application/ld+json; qs=2";
    public static final String APPLICATION_RDFXML = "application/rdf+xml; qs=2";
    public static final String VND_ORCID_XML = "application/vnd.orcid+xml; qs=5";
    public static final String VND_ORCID_JSON = "application/vnd.orcid+json; qs=4";
    public static final String HTML = "text/html; qs=1";
    public static final String HTML_UTF = "text/html; charset=UTF-8";

    public static final String TEXT_CSV = "text/csv";
    public static final String STATUS_OK_MESSAGE = "OK I am here";
    public static final String ACTIVITY_EDUCATION = "education";
    public static final String ACTIVITY_EMPLOYMENT = "employment";
    public static final String ACTIVITY_FUNDING = "funding";
    public static final String ACTIVITY_PEER_REVIEW = "peer-review";
    public static final String ACTIVITY_WORK = "work";
    public static final String SWAGGER_PATH = "/resources";
    public static final String SWAGGER_FILE = "/swagger.json";
    public static final String SWAGGER_FILE_YAML = "/swagger.yaml";
    public static final String APPLICATION_CITEPROC = "application/vnd.citationstyles.csl+json";
    public static final String RESEARCHER_URLS = "/{orcid}/researcher-urls";
    public static final String EMAIL = "/{orcid}/email";
    public static final String EXTERNAL_IDENTIFIERS = "/{orcid}/external-identifiers";
    public static final String INTERNAL_API_PERSON_READ = "/{orcid}/person";
    public static final String OTHER_NAMES = "/{orcid}/other-names";
    public static final String PERSONAL_DETAILS = "/{orcid}/personal-details";
    public static final String MEMBER_INFO = "/member-info";
    public static final String BIOGRAPHY = "/{orcid}/biography";
    public static final String KEYWORDS = "/{orcid}/keywords";
    public static final String ADDRESS = "/{orcid}/address";
    public static final String PERSON = "/{orcid}/person";
    
    public static final int MAX_NOTIFICATIONS_AVAILABLE = 1000;
}
