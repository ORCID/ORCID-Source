package org.orcid.core.manager;

import org.orcid.jaxb.model.message.OrcidMessage;

public interface SolrAndDBSearchManager {
    public OrcidMessage findFilteredOrcidsBasedOnQuery(String criteria, Integer start, Integer rows);
}
