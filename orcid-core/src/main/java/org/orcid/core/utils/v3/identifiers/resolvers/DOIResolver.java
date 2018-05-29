package org.orcid.core.utils.v3.identifiers.resolvers;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.core.utils.v3.identifiers.normalizers.DOINormalizer;
import org.orcid.pojo.PIDResolutionResult;
import org.springframework.stereotype.Component;

@Component
public class DOIResolver implements Resolver {

    @Resource
    PIDNormalizationService normalizationService;

    @Resource
    DOINormalizer norm;

    @Resource
    PIDResolverCache cache;

    public List<String> canHandle() {
        return norm.canHandle();
    }

    @Override
    public PIDResolutionResult resolve(String apiTypeName, String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(normalizationService.normalise(apiTypeName, value)))
            return PIDResolutionResult.NOT_ATTEMPTED;

        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (!StringUtils.isEmpty(normUrl)) {
            if (cache.isValidDOI(normUrl)){
                return new PIDResolutionResult(true,true,true,normUrl);                
            }else{
                return new PIDResolutionResult(false,true,true,null);
            }
        }
        
        return new PIDResolutionResult(false,false,true,null);//unreachable?
    }

}
