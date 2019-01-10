package org.orcid.core.utils.v3.identifiers.resolvers;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.pojo.PIDResolutionResult;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class ISBNOCLCResolver implements LinkResolver {

    @Resource
    PIDNormalizationService normalizationService;

    @Resource
    PIDResolverCache cache;

    public List<String> canHandle() {
        return Lists.newArrayList("oclc","isbn");
    }

    /** Looks for a 303.
     * Worldcat will 303 for valid IDs, and 200 for invalid IDs.
     * 
     */
    @Override
    public PIDResolutionResult resolve(String apiTypeName, String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(normalizationService.normalise(apiTypeName, value)))
            return PIDResolutionResult.NOT_ATTEMPTED;

        // Try normalizing value & creating a URL using the resolution prefix
        // this assumes we're using worldcat - 303 on success, 200 on fail
        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (!StringUtils.isEmpty(normUrl)) {
            if (cache.isHttp303(normUrl)){
                return new PIDResolutionResult(true,true,true,normUrl);                
            }else{
                return new PIDResolutionResult(false,true,true,null);
            }
        }
        return new PIDResolutionResult(false,false,true,null);//unreachable?
    }
}
