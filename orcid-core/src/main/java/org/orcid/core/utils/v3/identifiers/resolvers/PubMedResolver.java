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
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.rc2.common.Day;
import org.orcid.jaxb.model.v3.rc2.common.Month;
import org.orcid.jaxb.model.v3.rc2.common.PublicationDate;
import org.orcid.jaxb.model.v3.rc2.common.Title;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.common.Year;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.WorkTitle;
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

    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    List<String> types = Lists.newArrayList("pmc", "pmid");

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
    public Work resolveMetadata(String apiTypeName, String value) {
        PIDResolutionResult rr = this.resolve(apiTypeName, value);
        if (!rr.isResolved())
            return null;

        try {
            String endpoint = metadataEndpoint.replace("{id}", value);

            if (apiTypeName.equals("pmid")) {
                endpoint = endpoint.replace("{type}", "EXT_ID");
            } else {
                endpoint = endpoint.replace("{type}", "PMCID");
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
        Work work = new Work();
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
                            String urlType = url.getString("documentStyle");
                            String availability = url.getString("availability");
                            // If we find the html link, use it and stop
                            // searching
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
                if (workMetadata.has("pmid")) {
                    addExternalIdentifier(work, "pmid", workMetadata.getString("pmid"), locale);
                }

                if (workMetadata.has("pmcid")) {
                    addExternalIdentifier(work, "pmc", workMetadata.getString("pmcid"), locale);
                }

                if (workMetadata.has("doi")) {
                    addExternalIdentifier(work, "doi", workMetadata.getString("doi"), locale);
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
