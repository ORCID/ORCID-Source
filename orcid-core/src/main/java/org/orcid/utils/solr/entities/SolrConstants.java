package org.orcid.utils.solr.entities;

import java.util.Arrays;
import java.util.List;

public class SolrConstants {
    public static final String SCORE = "score";
    public static final String ORCID = "orcid";
    public static final String BIOGRAPHY = "biography";
    public static final String GIVEN_NAMES = "given-names";
    public static final String FAMILY_NAME = "family-name";
    public static final String GIVEN_AND_FAMILY_NAMES = "given-and-family-names";
    public static final String EMAIL_ADDRESS = "email";
    public static final String AFFILIATE_PAST_INSTITUTION_NAMES = "past-institution-affiliation-name";
    public static final String AFFILIATE_CURRENT_INSTITUTION_NAME = "current-institution-affiliation-name";
    public static final String CREDIT_NAME = "credit-name";
    public static final String OTHER_NAMES = "other-names";
    public static final String EXTERNAL_ID_TYPE_AND_VALUE = "external-id-type-and-value";
    public static final String EXTERNAL_ID_SOURCE = "external-id-source";
    public static final String EXTERNAL_ID_SOURCE_AND_REFERENCE = "external-id-source-and-reference";
    public static final String EXTERNAL_ID_REFERENCES = "external-id-reference";
    public static final String DIGITAL_OBJECT_IDS = "digital-object-ids";
    public static final String WORK_TITLES = "work-titles";
    public static final String GRANT_NUMBERS = "grant-numbers";
    public static final String FUNDING_TITLES = "funding-titles";
    public static final String KEYWORDS = "keyword";
    public static final String TEXT = "text";
    public static final String PROFILE_SUBMISSION_DATE = "profile-submission-date";
    public static final String PROFILE_LAST_MODIFIED_DATE = "profile-last-modified-date";
    public static final String PUBLIC_PROFILE = "public-profile-message";
    public static final String PRIMARY_RECORD = "primary-record";
    public static final String AGR = "agr";
    public static final String ARXIV = "arxiv";
    public static final String ASIN = "asin";
    public static final String ASIN_TLD = "asin-tld";
    public static final String BIBCODE = "bibcode";
    public static final String CBA = "cba";
    public static final String CIT = "cit";
    public static final String CTX = "ctx";
    public static final String EID = "eid";
    public static final String ETHOS = "ethos";
    public static final String HANDLE = "handle";
    public static final String HIR = "hir";
    public static final String ISBN = "isbn";
    public static final String ISSN = "issn";
    public static final String JFM = "jfm";
    public static final String JSTOR = "jstor";
    public static final String LCCN = "lccn";
    public static final String MR = "mr";
    public static final String OCLC = "oclc";
    public static final String OL = "ol";
    public static final String OSTI = "osti";
    public static final String PAT = "pat";
    public static final String PMC = "pmc";
    public static final String PMID = "pmid";
    public static final String RFC = "rfc";
    public static final String SOURCE_WORK_ID = "source-work-id";
    public static final String SSRN = "ssrn";
    public static final String URI = "uri";
    public static final String URN = "urn";
    public static final String WOSUID = "wosuid";
    public static final String ZBL = "zbl";
    public static final String OTHER_IDENTIFIER_TYPE = "other-identifier-type";
    public static final String ORG_DISAMBIGUATED_ID = "org-disambiguated-id";
    public static final String ORG_DISAMBIGUATED_NAME = "org-disambiguated-name";
    public static final String ORG_DISAMBIGUATED_CITY = "org-disambiguated-city";
    public static final String ORG_DISAMBIGUATED_REGION = "org-disambiguated-region";
    public static final String ORG_DISAMBIGUATED_COUNTRY = "org-disambiguated-country";
    public static final String ORG_DISAMBIGUATED_ID_FROM_SOURCE = "org-disambiguated-id-from-source";
    public static final String ORG_DISAMBIGUATED_ID_SOURCE_TYPE = "org-disambiguated-id-source-type";
    public static final String ORG_DISAMBIGUATED_STATUS = "org-disambiguated-status";
    public static final String ORG_DISAMBIGUATED_TYPE = "org-disambiguated-type";
    public static final String ORG_DISAMBIGUATED_POPULARITY = "org-disambiguated-popularity";
    public static final String ORG_DEFINED_FUNDING_TYPE = "org-defined-funding-type";
    public static final String ORG_CHOSEN_BY_MEMBER = "org-chosen-by-member";
    public static final String ORG_LOCATIONS_JSON = "org-locations-json";
    public static final String ORG_NAMES_JSON = "org-names-json";
    public static final String ORG_NAMES = "org-names";
    public static final String IS_FUNDING_ORG = "is-funding-org";
    public static final String DYNAMIC_SELF = "-self";
    public static final String DYNAMIC_PART_OF = "-part-of";
    public static final String DYNAMIC_VERSION_OF = "-version-of";
    public static final String DYNAMIC_ORGANISATION_ID = "-org-id";
    public static final String RINGGOLD_ORGANISATION_ID = "ringgold-org-id";
    public static final String FUNDREF_ORGANISATION_ID = "fundref-org-id";
    public static final String GRID_ORGANISATION_ID = "grid-org-id";
    public static final String ROR_ORGANISATION_ID = "ror-org-id";
    public static final String DYNAMIC_ORGANISATION_NAME = "-org-name";
    public static final String AFFILIATION_ORGANISATION_NAME = "affiliation-org-name";
    public static final String FUNDING_ORGANISATION_NAME = "funding-org-name";
    public static final String PEER_REVIEW_ORGANISATION_NAME = "peer-review-org-name";
    public static final String PEER_REVIEW_TYPE = "peer-review-type";
    public static final String PEER_REVIEW_ROLE = "peer-review-role";
    public static final String PEER_REVIEW_GROUP_ID = "peer-review-group-id";

    public static final String RINGGOLD_ORG_TYPE = "RINGGOLD";
    public static final String GRID_ORG_TYPE = "GRID";
    public static final String FUNDREF_ORG_TYPE = "FUNDREF";
    public static final String ROR_ORG_TYPE = "ROR";

    public static final String RESEARCH_RESOURCE_PROPOSAL_TITLES = "research-resource-proposal-title";
    public static final String RESEARCH_RESOURCE_PROPOSAL_HOSTS_NAME = "research-resource-proposal-org-name";
    public static final String RESEARCH_RESOURCE_ITEM_NAME = "research-resource-item-name";
    public static final String RESEARCH_RESOURCE_ITEM_HOSTS_NAME = "research-resource-item-org-name";

    /**
     * Fields client is allowed to specify
     * 
     **/
    public static final List<String> ALLOWED_FIELDS = Arrays.asList(SolrConstants.ORCID, SolrConstants.EMAIL_ADDRESS, SolrConstants.GIVEN_NAMES,
            SolrConstants.FAMILY_NAME, SolrConstants.GIVEN_AND_FAMILY_NAMES, SolrConstants.AFFILIATE_CURRENT_INSTITUTION_NAME,
            SolrConstants.AFFILIATE_PAST_INSTITUTION_NAMES, SolrConstants.CREDIT_NAME, SolrConstants.OTHER_NAMES);

}
