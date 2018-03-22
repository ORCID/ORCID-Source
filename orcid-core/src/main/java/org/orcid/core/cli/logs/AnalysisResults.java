package org.orcid.core.cli.logs;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.ClientDetailsDao;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AnalysisResults implements Serializable {

    private static final long serialVersionUID = 1L;

    private long hitsAnalysed;

    private List<ClientStats> clientResults = new ArrayList<>();

    @JsonIgnore
    private Map<String, ClientStats> statsByClient = new HashMap<>();

    @JsonIgnore
    private OutputStream outputStream;

    @JsonIgnore
    private AnalysisSummary summary = new AnalysisSummary();

    @JsonIgnore
    private ClientDetailsDao clientDetailsDao;

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void setSummaryOutputStream(OutputStream outputStream) {
        summary.setOutputStream(outputStream);
    }

    public void setClientDetailsDao(ClientDetailsDao clientDetailsDao) {
        this.clientDetailsDao = clientDetailsDao;
    }

    public void record(String clientDetailsId, ApiLog apiLog) {
        hitsAnalysed++;

        ClientStats stats = statsByClient.get(clientDetailsId);
        if (stats == null) {
            stats = new ClientStats();
            stats.setClientDetailsId(clientDetailsId);
            stats.setClientName(clientDetailsDao.getMemberName(clientDetailsId));
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

    public void outputResults() throws IOException {
        buildClientResultsListAndSummary();
        outputStream.write(JsonUtils.convertToJsonStringPrettyPrint(this).getBytes());
        outputStream.close();
        summary.outputSummary();
    }

    private void buildClientResultsListAndSummary() {
        int numV1Clients = 0;
        int numV2Clients = 0;
        int numV3Clients = 0;
        int numClientsUsingMultipleVersions = 0;

        for (String clientId : statsByClient.keySet()) {
            ClientStats stats = statsByClient.get(clientId);
            if (stats.getVersionsHit().size() > 1) {
                numClientsUsingMultipleVersions++;
            }

            // group major versionss
            Set<String> majorVersionsHit = getMajorVersionsHit(stats.getVersionsHit());
            if (majorVersionsHit.contains("v1")) {
                numV1Clients++;
            }
            if (majorVersionsHit.contains("v2")) {
                numV2Clients++;
            }
            if (majorVersionsHit.contains("v3")) {
                numV3Clients++;
            }
            clientResults.add(stats);
        }

        summary.setNumV1Clients(numV1Clients);
        summary.setNumV2Clients(numV2Clients);
        summary.setNumV3Clients(numV3Clients);
        summary.setNumClientsUsingMultipleVersions(numClientsUsingMultipleVersions);
    }

    private Set<String> getMajorVersionsHit(List<String> allVersions) {
        Set<String> majorVersionsHit = new HashSet<>();
        for (String version : allVersions) {
            for (String majorVersion : new String[] { "v1", "v2", "v3" }) {
                if (version.startsWith(majorVersion)) {
                    majorVersionsHit.add(majorVersion);
                }
            }
        }
        return majorVersionsHit;
    }

}