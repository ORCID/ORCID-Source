package org.orcid.core.orgs.extId.normalizer;

public interface OrgDisambiguatedExternalIdNormalizer {
    
    String getType();
    
    String normalize(String value);

}
