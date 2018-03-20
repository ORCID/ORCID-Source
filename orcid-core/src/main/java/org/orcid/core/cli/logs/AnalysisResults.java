package org.orcid.core.cli.logs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.core.utils.JsonUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AnalysisResults implements Serializable {

    private static final long serialVersionUID = 1L;

    private long hitsAnalysed;
    
    private List<ClientStats> clientResults = new ArrayList<>();
  
    @JsonIgnore
    private Map<String, ClientStats> statsByClient = new HashMap<>();
    
    @JsonIgnore
    private OutputStream outputStream;
    
    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public void record(String clientDetailsId, ApiLog apiLog) {
        hitsAnalysed++;
        
        ClientStats stats = statsByClient.get(clientDetailsId);
        if (stats == null) {
            stats = new ClientStats();
            stats.setClientDetailsId(clientDetailsId);
        }
        stats.recordVersionHit(apiLog.getVersion());
        stats.incrementTotalHits();
        statsByClient.put(clientDetailsId, stats);
    }
    
    public long getHitsAnalysed() {
        return hitsAnalysed;
    }
    
    public List<ClientStats> getClientResults() {
        return clientResults;
    }

    public void outputClientStats() throws IOException {
        for (String clientId : statsByClient.keySet()) {
            clientResults.add(statsByClient.get(clientId));
        }
        
        outputStream.write(JsonUtils.convertToJsonString(this).getBytes());
        outputStream.close();
    }
    
}