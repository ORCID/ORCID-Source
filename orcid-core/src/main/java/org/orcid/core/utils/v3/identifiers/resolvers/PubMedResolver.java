package org.orcid.core.utils.v3.identifiers.resolvers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import jakarta.annotation.Resource;
import jakarta.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.orcid.core.exception.UnexpectedResponseCodeException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.ContributorAttributes;
import org.orcid.jaxb.model.v3.release.common.ContributorOrcid;
import org.orcid.jaxb.model.v3.release.common.CreditName;
import org.orcid.jaxb.model.v3.release.common.Day;
import org.orcid.jaxb.model.v3.release.common.Month;
import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Year;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.pojo.ContributorsRolesAndSequences;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.PIDResolutionResult;
import org.orcid.pojo.WorkExtended;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

@Component
public class PubMedResolver implements LinkResolver, MetadataResolver {

    private static final Logger LOG = LoggerFactory.getLogger(PubMedResolver.class);

    @Resource
    PIDNormalizationService normalizationService;

    @Resource
    PIDResolverCache cache;

    @Resource
    private IdentifierTypeManager identifierTypeManager;

    @Resource
    protected LocaleManager localeManager;

    @Value("${org.orcid.core.work.contributors.ui.max:50}")
    private int maxContributorsForUI;

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    List<String> types = List.of("pmc", "pmid");

    private String metadataEndpoint = "https://www.ebi.ac.uk/europepmc/webservices/rest/search?query={type}:{id}&resultType=core&format=json";

    /**
     * Existence of an identifier is decided by EuropePMC, never by fetching the
     * pubmed.ncbi.nlm.nih.gov landing page: NCBI serves that page behind a
     * cookie/bot challenge that answers HTTP 203 to non-browser clients, which
     * made every PubMed import fail (PD-6180). Cached so that repeated form
     * validations of the same identifier only call EuropePMC once. A loader that
     * throws is not cached by Guava, so transient EuropePMC failures do not stick
     * for the life of the entry.
     */
    private final LoadingCache<String, Boolean> existsInEuropePmc = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES).maximumSize(10000)
            .build(new CacheLoader<String, Boolean>() {
                public Boolean load(String endpoint) throws IOException, JSONException {
                    return hasResults(fetchMetadata(endpoint));
                }
            });

    @Override
    public List<String> canHandle() {
        return types;
    }

    /**
     * Checks that EuropePMC knows the identifier, normalizing the value and
     * creating a URL using the resolution prefix
     *
     */
    @Override
    public PIDResolutionResult resolve(String apiTypeName, String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(normalizationService.normalise(apiTypeName, value)))
            return PIDResolutionResult.NOT_ATTEMPTED;

        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (!StringUtils.isEmpty(normUrl)) {
            if (exists(apiTypeName, value)) {
                return new PIDResolutionResult(true, true, true, normUrl);
            } else {
                return new PIDResolutionResult(false, true, true, null);
            }
        }

        return new PIDResolutionResult(false, false, true, null);// unreachable?
    }

    @Override
    public WorkExtended resolveMetadata(String apiTypeName, String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(normalizationService.normalise(apiTypeName, value)))
            return null;

        try {
            JSONObject json = fetchMetadata(getPubMedEndpoint(apiTypeName, value));
            if (hasResults(json)) {
                return getWork(json);
            }
        } catch (UnexpectedResponseCodeException e) {
            // TODO: For future projects, we might want to retry when
            // e.getReceivedCode() tell us that we can retry later, like 503 or
            // 504
            LOG.warn(String.format("UnexpectedResponseCode retrieving %s %s from EuropePMC. Expected %s, got %s", apiTypeName, value, e.getExpectedCode(),
                    e.getReceivedCode()), e);
        } catch (IOException | JSONException | ParseException e) {
            // Returning null asks the caller to report "unable to import", which
            // is a better outcome than a 500 the work modal cannot handle.
            LOG.warn(String.format("Error retrieving %s %s from EuropePMC", apiTypeName, value), e);
        }
        return null;
    }

    private boolean exists(String apiTypeName, String value) {
        try {
            return existsInEuropePmc.get(getPubMedEndpoint(apiTypeName, value));
        } catch (ExecutionException e) {
            LOG.warn(String.format("Error resolving %s %s against EuropePMC", apiTypeName, value), e.getCause());
            return false;
        }
    }

    private JSONObject fetchMetadata(String endpoint) throws IOException, JSONException {
        InputStream inputStream = cache.get(endpoint, MediaType.APPLICATION_JSON);
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8.name()))) {
            StringBuffer response = new StringBuffer();
            in.lines().forEach(i -> response.append(i));
            return new JSONObject(response.toString());
        }
    }

    private boolean hasResults(JSONObject json) throws JSONException {
        if (json == null || !json.has("resultList")) {
            return false;
        }
        JSONObject resultList = json.getJSONObject("resultList");
        return resultList != null && resultList.has("result") && resultList.getJSONArray("result").length() > 0;
    }

    // returns PID without prefix or URL etc
    private String getPubMedEndpoint(String apiTypeName, String userInput) {
        String normalised = normalizationService.normalise(apiTypeName, userInput);
        if (apiTypeName.equals("pmc")) {
            normalised = "PMC" + normalised;            
        }
        String endpoint = metadataEndpoint.replace("{id}", normalised);
        if (apiTypeName.equals("pmid")) {
            return endpoint.replace("{type}", "EXT_ID");
        }
        return endpoint.replace("{type}", "PMCID");
    }

    private WorkExtended getWork(JSONObject json) throws JSONException, ParseException {
        WorkExtended work = new WorkExtended();
        work.setWorkType(WorkType.JOURNAL_ARTICLE); // default for pubMed
        
        Locale locale = localeManager.getLocale();
        JSONObject resultsList = json.getJSONObject("resultList");

        if (resultsList != null && resultsList.has("result")) {
            JSONArray results = resultsList.getJSONArray("result");
            if (results != null && results.length() > 0) {
                // Pick the first element always
                JSONObject workMetadata = results.getJSONObject(0);

                if (workMetadata.has("title")) {
                    WorkTitle w = new WorkTitle();
                    w.setTitle(new Title(workMetadata.getString("title")));
                    work.setWorkTitle(w);
                }

                if (workMetadata.has("abstractText")) {
                    work.setShortDescription(workMetadata.getString("abstractText"));
                }

                String pubDate = null;
                if (workMetadata.has("firstPublicationDate")) {
                    pubDate = workMetadata.getString("firstPublicationDate");

                } else if (workMetadata.has("electronicPublicationDate")) {
                    pubDate = workMetadata.getString("electronicPublicationDate");
                }

                if (pubDate != null) {
                    Date date = dateFormat.parse(pubDate);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    PublicationDate publicationDate = new PublicationDate();
                    work.setPublicationDate(publicationDate);
                    publicationDate.setDay(new Day(c.get(Calendar.DAY_OF_MONTH)));
                    // January = 0
                    publicationDate.setMonth(new Month(c.get(Calendar.MONTH) + 1));
                    publicationDate.setYear(new Year(c.get(Calendar.YEAR)));
                }

                if (workMetadata.has("journalInfo")) {
                    JSONObject journalInfoMetadata = workMetadata.getJSONObject("journalInfo");
                    if (journalInfoMetadata.has("journal")) {
                        JSONObject journalMetadata = journalInfoMetadata.getJSONObject("journal");
                        if (journalMetadata.has("title")) {
                            work.setJournalTitle(new Title(journalMetadata.getString("title")));
                        }
                    }
                }

                if (workMetadata.has("fullTextUrlList")) {
                    JSONObject fullTextUrlList = workMetadata.getJSONObject("fullTextUrlList");
                    if (fullTextUrlList.has("fullTextUrl")) {
                        JSONArray urls = fullTextUrlList.getJSONArray("fullTextUrl");
                        for (int i = 0; i < urls.length(); i++) {
                            JSONObject url = urls.getJSONObject(i);
                            // Look for html or doi links
                            String urlType = null;
                            if (url.has("documentStyle")) {
                                urlType = url.getString("documentStyle");
                            }
                            String availability = null;
                            if (url.has("availability")) {
                                availability = url.getString("availability");
                            }

                            // If we find the html link, use it and stop
                            // searching
                            if (urlType != null) {
                                if (urlType.equals("html")) {
                                    if(availability == null || availability.equals("Free") || availability.equals("Open access")) {
                                        work.setUrl(new Url(url.getString("url")));
                                        break;
                                    }
                                } else if (urlType.equals("doi")) {
                                    work.setUrl(new Url(url.getString("url")));
                                }
                            }
                        }
                    }
                }
                if (workMetadata.has("pmid")) {
                    addExternalIdentifier(work, "pmid", workMetadata.getString("pmid"), locale);
                }

                if (workMetadata.has("pmcid")) {
                    addExternalIdentifier(work, "pmc", workMetadata.getString("pmcid"), locale);
                }

                if (workMetadata.has("doi")) {
                    addExternalIdentifier(work, "doi", workMetadata.getString("doi"), locale);
                }

                if (workMetadata.has("authorList")) {
                    List<ContributorsRolesAndSequences> contributorsGroupedByOrcid = new ArrayList<>();
                    JSONObject authorList = workMetadata.getJSONObject("authorList");
                    JSONArray contributors = authorList.getJSONArray("author");
                    for (int i = 0; i < (contributors.length() > maxContributorsForUI ? maxContributorsForUI + 1 : contributors.length()); i++) {
                        ContributorsRolesAndSequences newContributor = new ContributorsRolesAndSequences();
                        JSONObject contributor = contributors.getJSONObject(i);
                        if (contributor.has("collectiveName")) {
                            newContributor.setCreditName(new CreditName(contributor.getString("collectiveName")));
                        } else {
                            if (contributor.has("fullName")) {
                                newContributor.setCreditName(new CreditName(contributor.getString("fullName")));
                            } else {
                                StringBuilder sb = new StringBuilder();
                                if (contributor.has("firstName")) {
                                    sb.append(contributor.getString("firstName"));
                                }
                                if (contributor.has("lastName")) {
                                    String family = contributor.getString("lastName");
                                    sb.append(sb.length() > 0 ? ' ' + family : family);
                                }
                                newContributor.setCreditName(new CreditName(sb.toString()));
                            }
                        }
                        if (contributor.has("authorId")) {
                            JSONObject authorId = contributor.getJSONObject("authorId");
                            if (authorId.has("type") && "ORCID".equalsIgnoreCase(authorId.getString("type"))) {
                                if (authorId.has("value")) {
                                    newContributor.setContributorOrcid(new ContributorOrcid(authorId.getString("value")));
                                }
                            }
                        }

                        ContributorAttributes contributorAttributes = new ContributorAttributes();
                        contributorAttributes.setContributorRole("author");
                        newContributor.setRolesAndSequences(Arrays.asList(contributorAttributes));
                        contributorsGroupedByOrcid.add(newContributor);
                    }
                    work.setContributorsGroupedByOrcid(contributorsGroupedByOrcid);
                }
            }
        }
        return work;
    }

    private void addExternalIdentifier(Work work, String type, String value, Locale locale) {
        ExternalID extId = new ExternalID();
        extId.setType(type);
        extId.setValue(value);
        extId.setRelationship(Relationship.SELF);
        IdentifierType idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName(type.toUpperCase(), locale);
        if (idType != null && !PojoUtil.isEmpty(idType.getResolutionPrefix())) {
            extId.setUrl(new Url(idType.getResolutionPrefix() + value));
        }
        if(work.getExternalIdentifiers() == null) {
            work.setWorkExternalIdentifiers(new ExternalIDs());
        }
        
        work.getExternalIdentifiers().getExternalIdentifier().add(extId);
    }
}
