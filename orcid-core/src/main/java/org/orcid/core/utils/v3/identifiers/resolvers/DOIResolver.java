package org.orcid.core.utils.v3.identifiers.resolvers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
import org.orcid.jaxb.model.v3.rc1.common.Day;
import org.orcid.jaxb.model.v3.rc1.common.Month;
import org.orcid.jaxb.model.v3.rc1.common.PublicationDate;
import org.orcid.jaxb.model.v3.rc1.common.Subtitle;
import org.orcid.jaxb.model.v3.rc1.common.Title;
import org.orcid.jaxb.model.v3.rc1.common.Url;
import org.orcid.jaxb.model.v3.rc1.common.Year;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkTitle;
import org.orcid.jaxb.model.v3.rc1.record.WorkType;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.PIDResolutionResult;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.stereotype.Component;

@Component
public class DOIResolver implements LinkResolver, MetadataResolver {

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
    public Work resolveMetadata(String apiTypeName, String value) {
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
            InputStream inputStream = cache.get(rr.getGeneratedUrl(), "application/vnd.citationstyles.csl+json");
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8.name()));

            StringBuffer response = new StringBuffer();
            in.lines().forEach(i -> response.append(i));
            in.close();

            // Read JSON response and print
            JSONObject json = new JSONObject(response.toString());

            if (json != null) {
                return getWork(json);
            }
        } catch (UnexpectedResponseCodeException e) {
            // TODO: For future projects, we might want to retry when
            // e.getReceivedCode() tell us that we can retry later, like 503 or
            // 504
        } catch (IOException | JSONException e) {
            return null;
        }
        return null;
    }

    private Work getWork(JSONObject json) throws JSONException {
        Work result = new Work();
        Locale locale = localeManager.getLocale();
        if (json.has("type")) {
            try {
                result.setWorkType(WorkType.fromValue(json.getString("type")));
            } catch (IllegalArgumentException e) {

            }
        }

        WorkTitle workTitle = new WorkTitle();
        if (json.has("title")) {
            workTitle.setTitle(new Title(json.getString("title")));
        }

        if (json.has("subtitle")) {
            workTitle.setSubtitle(new Subtitle(json.getString("subtitle")));
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
            extId.setType("DOI");
            extId.setRelationship(Relationship.SELF);
            extId.setValue(doi);
            IdentifierType idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("DOI", locale);
            if (idType != null && !PojoUtil.isEmpty(idType.getResolutionPrefix())) {
                extId.setUrl(new Url(idType.getResolutionPrefix() + doi));
            }
            result.getWorkExternalIdentifiers().getExternalIdentifier().add(extId);
        }
        if (json.has("ISBN")) {
            try {
                JSONArray isbns = json.getJSONArray("ISBN");
                IdentifierType idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("ISBN", locale);
                for (int i = 0; i < isbns.length(); i++) {
                    String isbn = isbns.getString(i);
                    ExternalID extId = new ExternalID();
                    extId.setType("ISBN");
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
                    extId.setType("ISSN");
                    extId.setRelationship(Relationship.SELF);
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
            String description = json.getString("abstract");
            result.setShortDescription(description);
        }

        if (json.has("journal-title")) {
            String journalTitle = json.getString("journal-title");
            result.setJournalTitle(new Title(journalTitle));
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
            result.setLanguageCode(json.getString("language"));
        }
        return result;
    }
}
