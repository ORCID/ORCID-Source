package org.orcid.core.utils.v3.identifiers.resolvers;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.utils.v3.identifiers.NormalizationService;
import org.orcid.core.utils.v3.identifiers.ResolverCache;
import org.orcid.core.utils.v3.identifiers.normalizers.ISBNNormalizer;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class ISBNOCLCResolver implements Resolver {

    @Resource
    NormalizationService normalizationService;

    @Resource
    ResolverCache cache;

    public List<String> canHandle() {
        return Lists.newArrayList("oclc","isbn");
    }

    @Override
    public ResolutionResult resolve(String apiTypeName, String value) {
        if (StringUtils.isEmpty(value))
            return new ResolutionResult(false,null);

        // Try normalizing value & creating a URL using the resolution prefix
        // this assumes we're using worldcat - 303 on success, 200 on fail
        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (!StringUtils.isEmpty(normUrl)) {
            if (cache.isHttp303(normUrl)){
                return new ResolutionResult(true,normUrl);                
            }
        }
        return new ResolutionResult(true,null); 
    }

}
