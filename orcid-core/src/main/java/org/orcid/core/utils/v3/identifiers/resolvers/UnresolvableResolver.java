package org.orcid.core.utils.v3.identifiers.resolvers;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.PIDNormalizationService;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.PIDResolutionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class UnresolvableResolver implements LinkResolver{

    @Autowired
    @Lazy
    private IdentifierTypeManager idman;

    @Autowired
    private PIDNormalizationService normalizationService;
    
    @Autowired
    private Http200Resolver http200Resolver;

    private List<String> types;

    @Override
    public List<String> canHandle() {
        if (types == null && idman != null) {
            Map<String, IdentifierType> idTypes = idman.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH);
            if (idTypes != null) {
                types = new ArrayList<String>(idTypes.keySet());
                //have their own resolvers
                types.remove("isbn");
                types.remove("oclc");
                types.remove("doi");
                if (http200Resolver != null && http200Resolver.canHandle() != null) {
                    types.removeAll(http200Resolver.canHandle());
                }
            }
        }
        return types != null ? types : List.of();
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