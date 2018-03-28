package org.orcid.core.utils.v3.identifiers.normalizers;

import java.util.List;

import org.apache.jena.ext.com.google.common.collect.Lists;
import org.springframework.core.Ordered;

public interface NormalizerWithURLTransform extends Normalizer{

    /** If this normaliser can handle the apiTypeName provided 
     * then attempt to normalise the value and return it.
     * If the value cannot be normalised for any reason, return an empty string.
     * 
     * @param apiTypeName
     * @param value
     * @return
     */
    public String normaliseURL(String apiTypeName, String value);
}
