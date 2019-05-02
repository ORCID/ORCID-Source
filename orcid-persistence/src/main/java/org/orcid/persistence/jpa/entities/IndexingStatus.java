package org.orcid.persistence.jpa.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author Will Simpson
 * 
 */
public enum IndexingStatus {

    PENDING, DONE, REINDEX, IGNORE, FAILED, SOLR_UPDATE, DUMP_UPDATE;

    public static Object getNames(Collection<IndexingStatus> indexingStatuses) {
        List<String> names = new ArrayList<>();
        for (IndexingStatus indexingStatus : indexingStatuses) {
            names.add(indexingStatus.name());
        }
        return names;
    }

}
