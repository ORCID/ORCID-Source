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

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class AnalysisResults {
    
    private long hitsAnalysed;
    
    private Map<String, ClientStats> statsByClient = new HashMap<>();
    
    public void record(String clientDetailsId, ApiLog apiLog) {
        ClientStats stats = statsByClient.get(clientDetailsId);
        if (stats == null) {
            stats = new ClientStats();
        }
        stats.recordVersionHit(apiLog.getVersion());
        stats.incrementTotalHits();
        statsByClient.put(clientDetailsId, stats);
    }
    
    public long getHitsAnalysed() {
        return hitsAnalysed;
    }
    
    public void outputClientStats(OutputStream outputStream) throws IOException {
        for (String clientId : statsByClient.keySet()) {
            ClientStats clientStats = statsByClient.get(clientId);
            StringBuilder builder = new StringBuilder(clientId);
            builder.append(":");
            builder.append("\n  total hits: ").append(clientStats.getTotalHits());
            builder.append("\n  versions hit: ");
            for (String version : clientStats.getVersionsHit()) {
                builder.append(version).append("; ");
            }
            outputStream.write(builder.toString().getBytes());
            outputStream.write("\n".getBytes());
        }
        outputStream.close();
    }
    
}