package org.orcid.core.utils.v3.identifiers.finders;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.core.utils.v3.identifiers.normalizers.DOINormalizer;
import org.orcid.jaxb.model.v3.rc2.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.ExternalIDs;
import org.orcid.pojo.FindMyStuffItem;
import org.orcid.pojo.FindMyStuffResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class CrossrefFinder implements Finder {

    @Resource
    DOINormalizer norm;

    @Resource
    PIDResolverCache cache;

    
    @Value("${org.orcid.core.finder.crossref.enabled}")
    private Boolean isEnabled;
    @Value("${org.orcid.core.finder.crossref.clientid}")//"0000-0001-9884-1913";
    private String clientId;
    @Value("${org.orcid.core.finder.crossref.endpoint}")//"https://api.crossref.org/works?filter=orcid:" 0000-0003-1419-2405
    private String metadataEndpoint;

    @Override
    public FindMyStuffResult find(String orcid, ExternalIDs existingIDs) {
        FindMyStuffResult result = new FindMyStuffResult();
        result.setFinderName(getFinderName());
        if (!isEnabled())
            return result;
        try {
            InputStream is = cache.get(metadataEndpoint + orcid, "application/json");
            ObjectMapper objectMapper = new ObjectMapper();
            CrossrefSearchResult crResult = objectMapper.readValue(is, CrossrefSearchResult.class);
            for (CrossrefItem w : crResult.message.items) {
                if (!existingIDs.getExternalIdentifier().contains(w.getExternalID(norm))) {
                    String title = (w.title!=null && !w.title.isEmpty())? w.title.get(0):"";
                    result.getResults().add(new FindMyStuffItem(w.DOI, "doi", title));
                }
            }
            result.setTotal(crResult.message.totalResults);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String getFinderName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getRelatedClientId() {
        return clientId;
    }

    // MODELS

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CrossrefSearchResult {
        public CrossrefMessage message;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CrossrefMessage{
        public List<CrossrefItem> items;
        @JsonProperty("total-results")
        public int totalResults;
    }
    
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CrossrefItem{
        public String DOI;
        public List<String> title;

        public CrossrefItem() {
        };

        public ExternalID getExternalID(DOINormalizer norm) {
            ExternalID eid = new ExternalID();
            eid.setType("doi");
            eid.setValue(DOI);
            eid.setNormalized(new TransientNonEmptyString(norm.normalise("doi", DOI)));
            return eid;
        }
    }

    @Override
    public boolean isEnabled() {
        return (getRelatedClientId() != null && metadataEndpoint != null && isEnabled !=null && isEnabled);
    }

}
