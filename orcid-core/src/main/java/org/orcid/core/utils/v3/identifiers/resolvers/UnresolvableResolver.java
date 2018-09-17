package org.orcid.core.utils.v3.identifiers.resolvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.PIDResolutionResult;
import org.springframework.stereotype.Component;

@Component
public class UnresolvableResolver implements LinkResolver{

    @Resource
    IdentifierTypeManager idman;

    @Resource
    PIDNormalizationService normalizationService;
    
    @Resource Http200Resolver http200Resolver;

    List<String> types;

    @PostConstruct
    public void init() {
        Map<String, IdentifierType> idTypes = idman.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH);
        types = new ArrayList<String>(idTypes.keySet());
        //have their own resolvers
        types.remove("isbn");
        types.remove("oclc");
        types.remove("doi");
        types.removeAll(http200Resolver.canHandle());
    }

    @Override
    public List<String> canHandle() {
        return types;
    }

    /** Attempts to normalize value and generate a URL for consumption by the UI.
     * Does NOT attempt resolution.
     * IDs that cannot be recognised by their normalizer can then be flagged by the UI as 'wrong'.
     * The UI can use the URL to populate the form.
     * 
     */
    @Override
    public PIDResolutionResult resolve(String apiTypeName, String value) {
        if (StringUtils.isEmpty(value) || StringUtils.isEmpty(normalizationService.normalise(apiTypeName, value)))
            return PIDResolutionResult.NOT_ATTEMPTED;
        
        String normUrl = normalizationService.generateNormalisedURL(apiTypeName, value);
        if (StringUtils.isEmpty(normUrl)) {
            return new PIDResolutionResult(false,false,true,null);
        }else{
            return new PIDResolutionResult(false,false,true,normUrl);
        }
    }

}
