package org.orcid.core.cli.logs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientStats implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private String clientDetailsId;
    
    private long totalHits = 0;
    
    private List<String> versionsHit = new ArrayList<>();
    
    public void setClientDetailsId(String clientDetailsId) {
        this.clientDetailsId = clientDetailsId;
    }
    
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
    
    public String getClientDetailsId() {
        return clientDetailsId;
    }
    
}