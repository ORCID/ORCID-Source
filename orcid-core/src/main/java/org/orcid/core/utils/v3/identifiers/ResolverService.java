/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.utils.v3.identifiers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.resolvers.ResolutionResult;
import org.orcid.core.utils.v3.identifiers.resolvers.Resolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

@Component
public class ResolverService {

    @Resource
    List<Resolver> Resolvers = new ArrayList<Resolver>();

    @Resource
    IdentifierTypeManager idman;

    Map<String, LinkedList<Resolver>> map = new HashMap<String, LinkedList<Resolver>>();

    @PostConstruct
    public void init() {
        Collections.sort(Resolvers, AnnotationAwareOrderComparator.INSTANCE);
        for (String type : idman.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH).keySet()) {
            map.put(type, new LinkedList<Resolver>());
        }
        for (Resolver n : Resolvers) {
            List<String> supported = n.canHandle();
            if (supported.isEmpty()) {
                for (String type : map.keySet())
                    map.get(type).add(n);
            } else {
                for (String type : supported) {
                    map.get(type).add(n);
                }
            }
        }
    }

    /**
     * Ensure this is the API type name, not the DB type name.
     * 
     * @param type the api type name
     * @param value the url value
     * @return a resolution result containing the resolved URL (if successful), 
     * a flag indicating success and a flag indicating if resolution was attempted 
     * (i.e. there is a resolver that can handle the type)
     */
    public ResolutionResult resolve(String apiTypeName, String value) {
        ResolutionResult result = new ResolutionResult(false,null);
        for (Resolver r : map.get(apiTypeName)) {
            result = r.resolve(apiTypeName, value);
            if (result.isResolved())
                return result;
        } 
        return result;
    }

}
