package org.orcid.core.utils.v3.identifiers.resolvers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.jena.ext.com.google.common.collect.Lists;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.exception.UnexpectedResponseCodeException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.jaxb.model.v3.rc1.common.Day;
import org.orcid.jaxb.model.v3.rc1.common.Month;
import org.orcid.jaxb.model.v3.rc1.common.PublicationDate;
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
public class PubMedResolver implements LinkResolver, MetadataResolver {

    @Resource
    PIDNormalizationService normalizationService;

    @Resource
    PIDResolverCache cache;

    @Resource
    private IdentifierTypeManager identifierTypeManager;

    @Resource
    protected LocaleManager localeManager;

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy MMM dd");

    List<String> types = Lists.newArrayList("pmc", "pmid");

    private String metadataEndpoint = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary?db={db}&id={id}&retmode=json";

    @Override
    public List<String> canHandle() {
        return types;
    }

    /**
     * Checks for a http 200 normalizing the value and creating a URL using the
     * resolution prefix
     * 
     */
    @Override
    public PIDResolutionResult resolve(String apiTypeName, String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(normalizationService.normalise(apiTypeName, value)))
            return PIDResolutionResult.NOT_ATTEMPTED;

        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (!StringUtils.isEmpty(normUrl)) {
            if (cache.isHttp200(normUrl)) {
                return new PIDResolutionResult(true, true, true, normUrl);
            } else {
                return new PIDResolutionResult(false, true, true, null);
            }
        }

        return new PIDResolutionResult(false, false, true, null);// unreachable?
    }

    @Override
    public Work resolveMetadata(String apiTypeName, String value) {
        PIDResolutionResult rr = this.resolve(apiTypeName, value);
        if (!rr.isResolved())
            return null;

        try {
            String endpoint = null;

            if (value.startsWith("PMC") || value.startsWith("pmc")) {
                endpoint = metadataEndpoint.replace("{id}", value.substring(3));
            } else {
                endpoint = metadataEndpoint.replace("{id}", value);
            }

            if (apiTypeName.equals("pmid")) {
                endpoint = endpoint.replace("{db}", "pubmed");
            } else {
                endpoint = endpoint.replace("{db}", "pmc");
            }
            InputStream inputStream = cache.get(endpoint, MediaType.APPLICATION_JSON);
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
        } catch (IOException | JSONException | ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private Work getWork(JSONObject json) throws JSONException, ParseException {
        Work result = new Work();
        Locale locale = localeManager.getLocale();
        JSONObject results = json.getJSONObject("result");

        if (results != null) {
            JSONArray uids = results.getJSONArray("uids");
            String elementId = uids.getString(0);
            JSONObject metadata = results.getJSONObject(elementId);

            WorkTitle workTitle = new WorkTitle();
            if (metadata.has("title")) {
                workTitle.setTitle(new Title(metadata.getString("title")));
            }
            result.setWorkTitle(workTitle);

            if (metadata.has("fulljournalname")) {
                result.setJournalTitle(new Title(metadata.getString("fulljournalname")));
            }

            if (metadata.has("pubdate")) {
                String publicationDateString = metadata.getString("pubdate");
                Date date = dateFormat.parse(publicationDateString);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                PublicationDate publicationDate = new PublicationDate();
                result.setPublicationDate(publicationDate);
                publicationDate.setDay(new Day(c.get(Calendar.DAY_OF_MONTH)));
                // January = 0
                publicationDate.setMonth(new Month(c.get(Calendar.MONTH) + 1));
                publicationDate.setYear(new Year(c.get(Calendar.YEAR)));
            }

            if (metadata.has("articleids")) {
                result.setWorkExternalIdentifiers(new ExternalIDs());
                JSONArray extIdsMetadata = metadata.getJSONArray("articleids");
                for (int i = 0; i < extIdsMetadata.length(); i++) {
                    JSONObject extIdMetadata = extIdsMetadata.getJSONObject(i);
                    ExternalID extId = new ExternalID();
                    String type = extIdMetadata.getString("idtype");
                    IdentifierType idType = null;
                    switch (type) {
                    case "doi":
                        idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("DOI", locale);
                        extId.setType("DOI");
                        break;
                    case "pmid":
                    case "pubmed":
                        idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("PMID", locale);
                        extId.setType("PMID");
                        break;
                    case "pmcid":
                    case "pmc":
                        idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("PMC", locale);
                        extId.setType("PMC");
                        break;
                    default:
                        continue;
                    }

                    String value = extIdMetadata.getString("value");
                    extId.setRelationship(Relationship.SELF);
                    extId.setValue(value);
                    if (idType != null && !PojoUtil.isEmpty(idType.getResolutionPrefix())) {
                        extId.setUrl(new Url(idType.getResolutionPrefix() + value));
                    }

                    result.getWorkExternalIdentifiers().getExternalIdentifier().add(extId);
                }
            }

            if (metadata.has("issn")) {
                String value = metadata.getString("issn");
                if (!PojoUtil.isEmpty(value)) {
                    if (result.getWorkExternalIdentifiers() == null) {
                        result.setWorkExternalIdentifiers(new ExternalIDs());
                    }
                    ExternalID extId = new ExternalID();
                    extId.setRelationship(Relationship.SELF);
                    extId.setType("ISSN");
                    extId.setValue(value);
                    IdentifierType idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("ISSN", locale);
                    if (idType != null && !PojoUtil.isEmpty(idType.getResolutionPrefix())) {
                        extId.setUrl(new Url(idType.getResolutionPrefix() + value));
                    }
                    result.getWorkExternalIdentifiers().getExternalIdentifier().add(extId);
                }
            }

            if (metadata.has("pubtype")) {
                try {
                    JSONArray a = metadata.getJSONArray("pubtype");
                    result.setWorkType(getWorkType(a.getString(0)));
                } catch (IllegalArgumentException e) {

                }
            }
        }

        return result;
    }

    /**
     * Map pubmed publication types with the WorkType enum
     * https://www.nlm.nih.gov/mesh/pubtypes.html
     * */
    private WorkType getWorkType(String pubmedValue) {
        switch (pubmedValue) {
        case "Caricatures":
        case "Cartoons":
            return WorkType.ARTISTIC_PERFORMANCE;
        case "Account Books":
            return WorkType.BOOK;
        case "Book Reviews":
            return WorkType.BOOK_REVIEW;
        case "Clinical Conference":
        case "Consensus Development Conference":
        case "Consensus Development Conference, NIH":
            return WorkType.CONFERENCE_PAPER;
        case "Dataset":
            return WorkType.DATA_SET;
        case "Dictionary":
            return WorkType.DICTIONARY_ENTRY;
        case "Academic Dissertations":
            return WorkType.DISSERTATION;
        case "Introductory Journal Article":
        case "Journal Article":
            return WorkType.JOURNAL_ARTICLE;
        case "Lecture Notes":
        case "Lectures":
            return WorkType.LECTURE_SPEECH;
        case "Newspaper Article":
            return WorkType.NEWSPAPER_ARTICLE;
        case "Video-Audio Media":
        case "Webcasts":
            return WorkType.ONLINE_RESOURCE;
        case "Annual Reports":
            return WorkType.REPORT;
        case "Database":
            return WorkType.SOFTWARE;
        case "Blogs":
            return WorkType.WEBSITE;
        }
        return null;
    }
}
