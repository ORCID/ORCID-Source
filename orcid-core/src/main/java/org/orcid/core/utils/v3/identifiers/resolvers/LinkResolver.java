package org.orcid.core.utils.v3.identifiers.resolvers;

import java.util.List;

import org.orcid.pojo.PIDResolutionResult;

public interface LinkResolver {

    public static final List<String> CAN_HANDLE_EVERYTHING = List.of();

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
    public PIDResolutionResult resolve(String apiTypeName, String value);

}
