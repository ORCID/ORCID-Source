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

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${org.orcid.core.work.contributors.ui.max:50}")
    private int maxContributorsForUI;

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    List<String> types = List.of("pmc", "pmid");

    private String metadataEndpoint = "https://www.ebi.ac.uk/europepmc/webservices/rest/search?query={type}:{id}&resultType=core&format=json";

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
    public WorkExtended resolveMetadata(String apiTypeName, String value) {
        PIDResolutionResult rr = this.resolve(apiTypeName, value);
        if (!rr.isResolved())
            return null;

        try {
            String endpoint = getPubMedEndpoint(apiTypeName, value);
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
