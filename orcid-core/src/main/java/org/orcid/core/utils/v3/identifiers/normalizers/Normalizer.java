package org.orcid.core.utils.v3.identifiers.normalizers;

import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.springframework.core.Ordered;

public interface Normalizer extends Ordered {

    public static final List<String> CAN_HANDLE_EVERYTHING = Lists.newArrayList();
    
    /** A list of identifier types (using their API names) that this Normalizer can handle
     * An empty list implies all identifier types.
     * 
     */
    public List<String> canHandle();

    /** If this normaliser can handle the apiTypeName provided 
     * then attempt to normalise the value and return it.
     * If the value cannot be normalised for any reason, return an empty string.
     * 
     * @param apiTypeName
     * @param value
     * @return
     */
    public String normalise(String apiTypeName, String value);
}
