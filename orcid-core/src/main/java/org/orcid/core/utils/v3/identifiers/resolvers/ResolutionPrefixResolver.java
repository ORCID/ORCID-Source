package org.orcid.core.utils.v3.identifiers.resolvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.NormalizationService;
import org.orcid.core.utils.v3.identifiers.ResolverCache;
import org.orcid.pojo.IdentifierType;
import org.springframework.stereotype.Component;

@Component
public class ResolutionPrefixResolver implements Resolver {

    @Resource
    IdentifierTypeManager idman;

    @Resource
    NormalizationService normalizationService;

    @Resource
    ResolverCache cache;

    List<String> types;

    @PostConstruct
    public void init() {
        Map<String, IdentifierType> idTypes = idman.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH);
        types = new ArrayList<String>();
        for (String id : idTypes.keySet()){
            if (idTypes.get(id).getResolutionPrefix() != null){
                types.add(id);
            }
        }
        //have their own resolvers
        types.remove("isbn");
        types.remove("oclc");
        types.remove("doi");
        //types that should normally be a URL.
        types.add("uri");
        types.add("handle");
        //types with successful tests
        
        //types that fail due to the id implementations.
        types.remove("cienciaiul");
        types.remove("lensid");
        types.remove("jstor");
        types.remove("ssrn");
        types.remove("ethos");
        types.remove("jfm");
        types.remove("kuid");
        types.remove("lccn");
        types.remove("mr");
        types.remove("zbl");
    }

    @Override
    public List<String> canHandle() {
        return types;
    }

    /**
     * Checks for a http 200 1. if value is a URL, try that 2. If the value is
     * in the providedURL, try using that 3. Try normalizing the value and
     * creating a URL using the resolution prefix
     */
    @Override
    public ResolutionResult resolve(String apiTypeName, String value) {
        if (StringUtils.isEmpty(value))
            return new ResolutionResult(false,null);
        
        // if value is a URL, try that
        if (value.startsWith("http")) {
            if (cache.isHttp200(value)){
                return new ResolutionResult(true,value);                
            }
        }

        // Try normalizing the value and creating a URL using the resolution
        // prefix
        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (!StringUtils.isEmpty(normUrl) && !value.equals(normUrl)) {
            if (cache.isHttp200(normUrl)){
                return new ResolutionResult(true,normUrl);                
            }
        }

        return new ResolutionResult(true,null);
    }

}
