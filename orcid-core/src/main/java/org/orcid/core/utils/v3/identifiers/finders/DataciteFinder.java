package org.orcid.core.utils.v3.identifiers.finders;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.core.utils.v3.identifiers.normalizers.DOINormalizer;
import org.orcid.jaxb.model.v3.rc1.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.ExternalIDs;
import org.orcid.pojo.FindMyStuffItem;
import org.orcid.pojo.FindMyStuffResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class DataciteFinder implements Finder {

    @Resource
    DOINormalizer norm;

    @Resource
    PIDResolverCache cache;
    
    @Value("${org.orcid.core.finder.datacite.enabled}")
    private Boolean isEnabled;
    @Value("${org.orcid.core.finder.datacite.clientid}")//"0000-0001-8099-6984"
    private String clientId;
    @Value("${org.orcid.core.finder.datacite.endpoint}")//"https://api.datacite.org/works?query="
    private String metadataEndpoint;

    @Override
    public FindMyStuffResult find(String orcid, ExternalIDs existingIDs) {
        orcid = "0000-0003-1419-2405";
        FindMyStuffResult result = new FindMyStuffResult();
        result.setFinderName(getFinderName());
        if (!isEnabled())
            return result;
        try {
            InputStream is = cache.get(metadataEndpoint + orcid, "application/json");
            ObjectMapper objectMapper = new ObjectMapper();
            DataciteSearchResult dcResult = objectMapper.readValue(is, DataciteSearchResult.class);
            for (DataciteSimpleWork w : dcResult.data) {
                if (!existingIDs.getExternalIdentifier().contains(w.attributes.getExternalID())) {
                    result.getResults().add(new FindMyStuffItem(w.attributes.doi, "doi", w.attributes.title));
                }
            }
        } catch (MalformedURLException e) {
            // do nothing
        } catch (IOException e) {
            // do nothing
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
    public static class DataciteSearchResult {
        public List<DataciteSimpleWork> data;

        public DataciteSearchResult() {
        };
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataciteSimpleWork {
        public DataciteSimpleWorkAttributes attributes;

        public DataciteSimpleWork() {
        };
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataciteSimpleWorkAttributes {
        public String doi;
        public String title;

        public DataciteSimpleWorkAttributes() {
        };

        public ExternalID getExternalID() {
            ExternalID eid = new ExternalID();
            eid.setType("doi");
            eid.setValue(doi);
            eid.setNormalized(new TransientNonEmptyString(doi));
            return eid;
        }
    }

    @Override
    public boolean isEnabled() {
        return (getRelatedClientId() != null && metadataEndpoint != null && isEnabled !=null && isEnabled);
    }
}
