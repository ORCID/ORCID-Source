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
package org.orcid.core.cli.logs;

import java.util.ArrayList;
import java.util.List;

public class ClientStats {
    
    private long totalHits = 0;
    
    private List<String> versionsHit = new ArrayList<>();
    
    public void recordVersionHit(String version) {
        for (String v : versionsHit) {
            if (version.equals(v)) {
                return;
            }
        }
        versionsHit.add(version);
    }
    
    public void incrementTotalHits() {
        totalHits++;
    }
    
    public List<String> getVersionsHit() {
        return versionsHit;
    }
    
    public long getTotalHits() {
        return totalHits;
    }
    
}