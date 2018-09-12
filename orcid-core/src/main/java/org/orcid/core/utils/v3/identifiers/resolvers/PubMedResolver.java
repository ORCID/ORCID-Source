package org.orcid.core.utils.v3.identifiers.resolvers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.annotation.Resource;

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
import org.orcid.jaxb.model.v3.rc1.common.Subtitle;
import org.orcid.jaxb.model.v3.rc1.common.Title;
import org.orcid.jaxb.model.v3.rc1.common.Url;
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

    List<String> types = Lists.newArrayList("pmc", "pmid");
    
    private String metadataEndpoint = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/esummary?db={db}&id={id}&retmode=json";        

    @Override
    public List<String> canHandle() {
        return types;
    }

    /**
     * Checks for a http 200 
     * normalizing the value and creating a URL using the resolution prefix
     * 
     */
    @Override
    public PIDResolutionResult resolve(String apiTypeName, String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(normalizationService.normalise(apiTypeName, value)))
            return PIDResolutionResult.NOT_ATTEMPTED;

        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (!StringUtils.isEmpty(normUrl)) {
            if (cache.isHttp200(normUrl)){
                return new PIDResolutionResult(true,true,true,normUrl);                
            }else{
                return new PIDResolutionResult(false,true,true,null);
            }
        }
        
        return new PIDResolutionResult(false,false,true,null);//unreachable?        
    }
    
    @Override
    public Work resolveMetadata(String apiTypeName, String value) {
        PIDResolutionResult rr = this.resolve(apiTypeName, value);
        if (!rr.isResolved())
            return null;
        
        try {
            String endpoint = metadataEndpoint.replace("{id}", "value");
            if(apiTypeName.equals("pmid")) {
                endpoint = endpoint.replaceAll("{db}", "pubmed");
            } else {
                endpoint = endpoint.replaceAll("{db}", "pmc");
            }
            InputStream inputStream = cache.get(endpoint, "application/json");
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
        
        if(json.has("type")) {
            try {
                result.setWorkType(WorkType.fromValue(json.getString("type")));
            } catch(IllegalArgumentException e) {
                
            }
        }
        
        WorkTitle workTitle = new WorkTitle();
        if(json.has("title")) {
            workTitle.setTitle(new Title(json.getString("title")));            
        } 
        
        if(json.has("subtitle")) {
            workTitle.setSubtitle(new Subtitle(json.getString("subtitle")));
        }
        
        result.setWorkTitle(workTitle);
        
        if(json.has("URL")) {
            result.setUrl(new Url(json.getString("URL")));
        }
        
        // Populate other external identifiers
        result.setWorkExternalIdentifiers(new ExternalIDs());
        if(json.has("DOI")) {
            String doi = json.getString("DOI");
            ExternalID extId = new ExternalID();
            extId.setType("DOI");
            extId.setRelationship(Relationship.SELF);
            extId.setValue(doi);
            IdentifierType idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("DOI", localeManager.getLocale());
            if(idType != null && !PojoUtil.isEmpty(idType.getResolutionPrefix())) {
                extId.setUrl(new Url(idType.getResolutionPrefix() + doi));
            }
            result.getWorkExternalIdentifiers().getExternalIdentifier().add(extId);
        }
        if(json.has("ISBN")) {
            try {
                JSONArray isbns = json.getJSONArray("ISBN");
                for(int i = 0; i < isbns.length(); i++) {
                    String isbn = isbns.getString(i);
                    ExternalID extId = new ExternalID();
                    extId.setType("ISBN");
                    extId.setRelationship(Relationship.SELF);
                    extId.setValue(isbn);
                    IdentifierType idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("ISBN", localeManager.getLocale());
                    if(idType != null && !PojoUtil.isEmpty(idType.getResolutionPrefix())) {
                        extId.setUrl(new Url(idType.getResolutionPrefix() + isbn));
                    }
                    result.getWorkExternalIdentifiers().getExternalIdentifier().add(extId);
                }
            } catch(Exception e) {
                
            }
        }
        if(json.has("ISSN")) {
            try {
                JSONArray isbns = json.getJSONArray("ISSN");
                for(int i = 0; i < isbns.length(); i++) {
                    String isbn = isbns.getString(i);
                    ExternalID extId = new ExternalID();
                    extId.setType("ISSN");
                    extId.setRelationship(Relationship.SELF);
                    extId.setValue(isbn);
                    IdentifierType idType = identifierTypeManager.fetchIdentifierTypeByDatabaseName("ISSN", localeManager.getLocale());
                    if(idType != null && !PojoUtil.isEmpty(idType.getResolutionPrefix())) {
                        extId.setUrl(new Url(idType.getResolutionPrefix() + isbn));
                    }
                    result.getWorkExternalIdentifiers().getExternalIdentifier().add(extId);
                }
            } catch(Exception e) {
                
            }
        }        
        
        if(json.has("abstract")) {
            String description = json.getString("abstract");
            result.setShortDescription(description);
        }
        return result;
    }

}
