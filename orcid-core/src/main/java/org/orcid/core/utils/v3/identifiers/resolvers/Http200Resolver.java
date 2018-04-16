package org.orcid.core.utils.v3.identifiers.resolvers;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.core.utils.v3.identifiers.PIDResolverCache;
import org.orcid.pojo.PIDResolutionResult;
import org.springframework.stereotype.Component;

@Component
public class Http200Resolver implements Resolver {

    @Resource
    PIDNormalizationService normalizationService;

    @Resource
    PIDResolverCache cache;

    List<String> types;

    @PostConstruct
    public void init() {
        types = new ArrayList<String>();
        //These types reliably return 200 if found.
        types.add("arxiv");
        types.add("pmid");
        types.add("pmc");
        types.add("rrid");
        types.add("rfc");
        types.add("pdb");
        //note url, handles and other types tested etc DO NOT reliably return 200 if found!
    }

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

}
