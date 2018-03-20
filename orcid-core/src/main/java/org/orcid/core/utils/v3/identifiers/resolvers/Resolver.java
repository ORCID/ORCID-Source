package org.orcid.core.utils.v3.identifiers.resolvers;

import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;

public interface Resolver {

    public static final List<String> CAN_HANDLE_EVERYTHING = Lists.newArrayList();

    public List<String> canHandle();

    //note these could easily be extend to turn into works:
    //public Optional<Work> resolve(String apiTypeName, String value);

    /**
     * If this resolver can handle the apiTypeName provided then attempt to
     * resolve the value.
     * 
     * @param apiTypeName
     * @param value
     * @param providedURL
     * @return
     */
    public boolean canResolve(String apiTypeName, String value, String providedURL);

}
