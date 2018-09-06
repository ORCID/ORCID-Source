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
import org.orcid.pojo.FindMyStuffResult;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CrossrefFinder implements Finder {

    @Resource
    DOINormalizer norm;

    @Resource
    PIDResolverCache cache;

    private static final String clientId = "APP";
    private static final String metadataEndpoint = "https://search.crossref.org/dois?q=";

    @Override
    public FindMyStuffResult find(String orcid, ExternalIDs existingIDs) {
        FindMyStuffResult result = new FindMyStuffResult();
        result.setFinderName(getFinderName());
        try {
            InputStream is = cache.get(metadataEndpoint + orcid, "application/json");
            ObjectMapper objectMapper = new ObjectMapper();
            List<CrossrefSearchResult> crResult = objectMapper.readValue(is, new TypeReference<List<CrossrefSearchResult>>() {
            });
            for (CrossrefSearchResult w : crResult) {
                if (!existingIDs.getExternalIdentifier().contains(w.getExternalID(norm))) {
                    result.getResults().add(new FindMyStuffResult.Result(w.doi, "doi", w.title));
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
    public static class CrossrefSearchResult {
        public String doi;
        public String title;

        public CrossrefSearchResult() {
        };

        public ExternalID getExternalID(DOINormalizer norm) {
            ExternalID eid = new ExternalID();
            eid.setType("doi");
            eid.setValue(doi);
            eid.setNormalized(new TransientNonEmptyString(norm.normalise("doi", doi)));
            return eid;
        }
    }

}
