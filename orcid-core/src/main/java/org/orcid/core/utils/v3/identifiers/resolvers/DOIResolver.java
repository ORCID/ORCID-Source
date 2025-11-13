package org.orcid.core.utils.v3.identifiers.resolvers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.exception.UnexpectedResponseCodeException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.core.utils.v3.identifiers.normalizers.DOINormalizer;
import org.orcid.jaxb.model.common.CitationType;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.SequenceType;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.Day;
import org.orcid.jaxb.model.v3.release.common.Month;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.common.Subtitle;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.jaxb.model.v3.release.record.Citation;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.PIDResolutionResult;
import org.orcid.pojo.WorkExtended;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DOIResolver implements LinkResolver, MetadataResolver {
    
    private static final Logger LOG = LoggerFactory.getLogger(DOIResolver.class);

    @Resource
    PIDNormalizationService normalizationService;

    @Resource
    DOINormalizer norm;

    @Resource
    PIDResolverCache cache;

    @Resource
    private IdentifierTypeManager identifierTypeManager;

    @Resource
    protected LocaleManager localeManager;

    @Value("${org.orcid.core.work.contributors.ui.max:50}")
    private int maxContributorsForUI;

    public List<String> canHandle() {
        return norm.canHandle();
    }

    @Override
    public PIDResolutionResult resolve(String apiTypeName, String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(normalizationService.normalise(apiTypeName, value)))
            return PIDResolutionResult.NOT_ATTEMPTED;

        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (!StringUtils.isEmpty(normUrl)) {
            if (cache.isValidDOI(normUrl)) {
                return new PIDResolutionResult(true, true, true, normUrl);
            } else {
                return new PIDResolutionResult(false, true, true, null);
            }
        }

        return new PIDResolutionResult(false, false, true, null);// unreachable?
    }

    /**
     * Uses content negotiation to get work metadata from a DOI see
     * https://crosscite.org/docs.html#sec-4 Uses
     * application/vnd.citationstyles.csl+json as it is consistent across DOI
     * providers (although other formats may be better...)
     */
    @Override
    public WorkExtended resolveMetadata(String apiTypeName, String value) {
        PIDResolutionResult rr = this.resolve(apiTypeName, value);
        if (!rr.isResolved())
            return null;

        // now resolve rr.getGeneratedUrl() into metadata.

        // BASIC VERSION:
        // datacite example: curl -LH "Accept:
        // application/vnd.citationstyles.csl+json"
        // https://doi.org/10.6084/m9.figshare.824317
        // crossref example: curl -LH "Accept:
        // application/vnd.citationstyles.csl+json"
        // https://doi.org/10.3390/publications4040030
        // citeproc vocab: https://aurimasv.github.io/z2csl/typeMap.xml
        // schema here:
        // https://github.com/citation-style-language/schema/blob/v1.0.1/csl-data.json
        // maybe turn into jackson pojo using this:
        // https://github.com/joelittlejohn/jsonschema2pojo

        // ADVANCED: alternatively, we can use crossref xml, datacite xml and
        // mEDRA XML. You need to work out who created the DOI to do this.
        // JAXB here:
        // orcid-utils/src/main/java/org/crossref/
        // orcid-utils/src/main/java/org/datacite/

        // curl -D - -L -H "Accept: application/unixref+xml"
        // "https://doi.org/10.1126/science.1157784"
        // returns https://www.crossref.org/schema/unixref1.1.xsd however, the
        // schema cannot be parsed by XJC.
        // this is the only one that works: xjc
        // https://www.crossref.org/schemas/crossref_query_output3.0.xsd, but I
        // do not know the endpoint that returns it.
        // Datacite schema can be parsed by XJC:
        // https://schema.datacite.org/meta/kernel-4.0/metadata.xsd. It also
        // supports schema.org This makes it easy.

        // see https://crosscite.org/docs.html for more docs.

        try {
            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Accept", "application/vnd.citationstyles.csl+json");
            headers.put("User-Agent", "ORCID Registry;mailto:support@orcid.org");
            InputStream inputStream = cache.get(rr.getGeneratedUrl(), headers);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8.name()));

            StringBuffer response = new StringBuffer();
            in.lines().forEach(i -> response.append(i));
            in.close();
            // Read JSON response and print
            JSONObject json = new JSONObject(response.toString());

            if (json != null) {
                return getWorkExtended(json);
            }
        } catch (UnexpectedResponseCodeException e) {
            LOG.warn(String.format("UnexpectedResponseCode retrieving DOI %s. Expected %s, got %s", rr.getGeneratedUrl(), e.getExpectedCode(), e.getReceivedCode()), e);
        } catch (IOException | JSONException e) {
            return null;
        }
        return null;
    }

    private WorkExtended getWorkExtended(JSONObject json) throws JSONException {
        WorkExtended result = new WorkExtended();
        Locale locale = localeManager.getLocale();
        if (json.has("type")) {
            try {
                result.setWorkType(WorkType.fromValue(json.getString("type")));
            } catch (IllegalArgumentException e) {

            }
        }
        if (result.getWorkType() == null && json.has("subtype")) {
            try {
                result.setWorkType(WorkType.fromValue(json.getString("subtype")));
            } catch (IllegalArgumentException e) {

            }
        }

        WorkTitle workTitle = new WorkTitle();
        if (json.has("title")) {
            workTitle.setTitle(new Title(removeSquareBrackets(json.getString("title"))));
        }

        if (json.has("subtitle")) {
            workTitle.setSubtitle(new Subtitle(removeSquareBrackets(json.getString("subtitle"))));
        }

        result.setWorkTitle(workTitle);

        if (json.has("URL")) {
            result.setUrl(new Url(json.getString("URL")));
        }

        // Populate other external identifiers
        result.setWorkExternalIdentifiers(new ExternalIDs());
        if (json.has("DOI")) {
            String doi = json.getString("DOI");
            ExternalID extId = new ExternalID();
            extId.setType("doi");
            extId.setRelationship(Relationship.SELF);
            extId.setValue(doi);
            IdentifierType idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("DOI", locale);
            if (idType != null && !PojoUtil.isEmpty(idType.getResolutionPrefix())) {
                extId.setUrl(new Url(idType.getResolutionPrefix() + doi));
            }
            result.getWorkExternalIdentifiers().getExternalIdentifier().add(extId);
            //get Citation Data
            String bibtexCitation = getBibtexCitationData(idType.getResolutionPrefix() + doi);
            if(!StringUtils.isBlank(bibtexCitation)) {
                Citation citation = new Citation();
                citation.setWorkCitationType(CitationType.BIBTEX);
                citation.setCitation(bibtexCitation);
                result.setWorkCitation(citation);
            }
        }
        if (json.has("ISBN")) {
            try {
                JSONArray isbns = json.getJSONArray("ISBN");
                IdentifierType idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("ISBN", locale);
                for (int i = 0; i < isbns.length(); i++) {
                    String isbn = isbns.getString(i);
                    ExternalID extId = new ExternalID();
                    extId.setType("isbn");
                    extId.setRelationship(Relationship.SELF);
                    extId.setValue(isbn);
                    if (idType != null && !PojoUtil.isEmpty(idType.getResolutionPrefix())) {
                        extId.setUrl(new Url(idType.getResolutionPrefix() + isbn));
                    }
                    result.getWorkExternalIdentifiers().getExternalIdentifier().add(extId);
                }
            } catch (Exception e) {

            }
        }
        if (json.has("ISSN")) {
            try {
                JSONArray issns = json.getJSONArray("ISSN");
                IdentifierType idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("ISSN", locale);
                for (int i = 0; i < issns.length(); i++) {
                    String issn = issns.getString(i);
                    ExternalID extId = new ExternalID();
                    extId.setType("issn");
                    extId.setRelationship(Relationship.PART_OF);
                    extId.setValue(issn);
                    if (idType != null && !PojoUtil.isEmpty(idType.getResolutionPrefix())) {
                        extId.setUrl(new Url(idType.getResolutionPrefix() + issn));
                    }
                    result.getWorkExternalIdentifiers().getExternalIdentifier().add(extId);
                }
            } catch (Exception e) {

            }
        }

        if (json.has("abstract")) {
            result.setShortDescription(removeSquareBrackets(json.getString("abstract")));
        }
        
        if (result.getWorkType() == WorkType.BOOK) {
            if (json.has("publisher")) {
                result.setJournalTitle(new Title(removeSquareBrackets(json.getString("publisher"))));
            }
        } else if (json.has("journal-title")) {
            result.setJournalTitle(new Title(removeSquareBrackets(json.getString("journal-title"))));
        } else if (json.has("container-title")) {
            result.setJournalTitle(new Title(removeSquareBrackets(json.getString("container-title"))));
        } else if (json.has("container-title-short")) {
            result.setJournalTitle(new Title(removeSquareBrackets(json.getString("container-title-short"))));
        }

        JSONObject publicationDateJson = null;
        if (json.has("published-print")) {
            publicationDateJson = json.getJSONObject("published-print");
        } else if (json.has("published-online")) {
            publicationDateJson = json.getJSONObject("published-online");
        }

        if (publicationDateJson != null) {
            JSONArray dateParts = publicationDateJson.getJSONArray("date-parts");
            PublicationDate publicationDate = new PublicationDate();
            try {
                JSONArray dateArray = dateParts.getJSONArray(0);
                // Should we assume YYYY-MM-DD date format?
                publicationDate.setYear(new Year(dateArray.getInt(0)));
                publicationDate.setMonth(new Month(dateArray.getInt(1)));
                publicationDate.setDay(new Day(dateArray.getInt(2)));
            } catch (JSONException e) {
                // Might be due to Month or Day not available, so, since the
                // previous field (Year or Month) is already populated, we don't
                // need to worry about this error
            }
            result.setPublicationDate(publicationDate);
        }

        if (json.has("language")) {
            try {
                result.setLanguageCode(json.getString("language"));
            } catch (IllegalArgumentException e) {
                // ignore if language value doesn't match our LanguageCode
            }
        }
        
        if (result.getPublicationDate() == null && json.has("issued")) {
            JSONObject issued = (JSONObject) json.get("issued");
            JSONArray dateParts = issued.getJSONArray("date-parts");
            try {
                JSONArray date = dateParts.getJSONArray(0);

                if (date != null) {
                    int year = 0;
                    int month = 0;
                    int day = 0;
                    if (date.length() > 0 && !JSONObject.NULL.equals(date.get(0))) {
                        year = date.getInt(0);
                    }
                    if (date.length() > 1 && !JSONObject.NULL.equals(date.get(1))) {
                        month = date.getInt(1);
                    }
                    if (date.length() > 2 && !JSONObject.NULL.equals(date.get(2))) {
                        day = date.getInt(2);
                    }

                    if (year != 0) {
                        PublicationDate publicationDate = new PublicationDate();
                        publicationDate.setYear(new Year(year));
                        if (month != 0) {
                            publicationDate.setMonth(new Month(month));
                        }
                        if (day != 0) {
                            publicationDate.setDay(new Day(day));
                        }
                        result.setPublicationDate(publicationDate);
                    }
                }
            } catch (JSONException ignored) { }
        }

        if (json.has("author")) {
            List<ContributorsRolesAndSequences> contributorsGroupedByOrcid = new ArrayList<>();
            try {
                JSONArray contributors = json.getJSONArray("author");
                for (int i = 0; i < (contributors.length() > maxContributorsForUI ? maxContributorsForUI + 1 : contributors.length()); i++) {
                    ContributorsRolesAndSequences newContributor = new ContributorsRolesAndSequences();
                    JSONObject contributor = contributors.getJSONObject(i);

                    if (contributor.has("ORCID")) {
                        newContributor.setContributorOrcid(new ContributorOrcid(OrcidStringUtils.getOrcidNumber(contributor.getString("ORCID"))));
                    }

                    if (contributor.has("name")) {
                        newContributor.setCreditName(new CreditName(contributor.getString("name")));
                    } else {
                        StringBuilder sb = new StringBuilder();
                        if (contributor.has("given")) {
                            sb.append(contributor.getString("given"));
                        }
                        if (contributor.has("family")) {
                            String family = contributor.getString("family");
                            sb.append(sb.length() > 0 ? ' ' + family : family);
                        }
                        newContributor.setCreditName(new CreditName(sb.toString()));
                    }

                    ContributorAttributes contributorAttributes = new ContributorAttributes();
                    contributorAttributes.setContributorRole("author");
                    if (contributor.has("sequence")) {
                        contributorAttributes.setContributorSequence(SequenceType.fromValue(contributor.getString("sequence")));
                    }
                    newContributor.setRolesAndSequences(Arrays.asList(contributorAttributes));
                    contributorsGroupedByOrcid.add(newContributor);
                }
            } catch (IllegalArgumentException | JSONException e) {
                // ignore if language value doesn't match our LanguageCode
            }

            result.setContributorsGroupedByOrcid(contributorsGroupedByOrcid);

        }

        return result;
    }
    
    
    private String getBibtexCitationData(String url) {
        try {
            
            InputStream inputStream = cache.get(url, "application/x-bibtex");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8.name()));

            StringBuffer response = new StringBuffer();
            in.lines().forEach(i -> response.append(i));
            in.close();
 
            if(StringUtils.isNotBlank(response)) {
                String responseStr = response.toString();
                if(responseStr.startsWith("@") && responseStr.endsWith("}")) {
                    return responseStr;
                }
            }
            
        } catch (UnexpectedResponseCodeException e) {
            LOG.warn(String.format("UnexpectedResponseCode retrieving bibtext for DOI %s. Expected %s, got %s", url, e.getExpectedCode(), e.getReceivedCode()), e);
        } catch (IOException e) {
            return null;
        }
        return null;
        
    }

    private String removeSquareBrackets(String text) {
        if (text.startsWith("[") && text.endsWith("]")) {
            text = text.substring(1, text.length() - 1);
        }

        if (text.startsWith("\"") && text.endsWith("\"")) {
            text = text.substring(1, text.length() - 1);
        }
        return text;
    }
}
