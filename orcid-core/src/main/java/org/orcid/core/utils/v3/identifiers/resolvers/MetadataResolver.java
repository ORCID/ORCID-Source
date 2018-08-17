package org.orcid.core.utils.v3.identifiers.resolvers;

import org.orcid.jaxb.model.v3.rc1.record.Work;

public interface MetadataResolver extends LinkResolver{

    /**
     * If this resolver can handle the apiTypeName provided then attempt to
     * resolve the value.
     * 
     * @param apiTypeName
     * @param value
     * @param providedURL
     * @return
     */
    public Work resolveMetadata(String apiTypeName, String value);

}
