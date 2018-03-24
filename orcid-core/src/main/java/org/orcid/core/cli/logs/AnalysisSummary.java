package org.orcid.core.cli.logs;

import java.io.IOException;
import java.io.OutputStream;

import org.orcid.core.utils.JsonUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AnalysisSummary {
    
    private int numV1Clients;
    
    private int numV2Clients;
    
    private int numV3Clients;
    
    private int numClientsUsingMultipleVersions;
    
    @JsonIgnore
    private OutputStream outputStream;

    public int getNumV1Clients() {
        return numV1Clients;
    }

    public void setNumV1Clients(int numV1Clients) {
        this.numV1Clients = numV1Clients;
    }

    public int getNumV2Clients() {
        return numV2Clients;
    }

    public void setNumV2Clients(int numV2Clients) {
        this.numV2Clients = numV2Clients;
    }

    public int getNumV3Clients() {
        return numV3Clients;
    }

    public void setNumV3Clients(int numV3Clients) {
        this.numV3Clients = numV3Clients;
    }

    public int getNumClientsUsingMultipleVersions() {
        return numClientsUsingMultipleVersions;
    }

    public void setNumClientsUsingMultipleVersions(int numClientsUsingMultipleVersions) {
        this.numClientsUsingMultipleVersions = numClientsUsingMultipleVersions;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public void outputSummary() throws IOException {
        outputStream.write(JsonUtils.convertToJsonStringPrettyPrint(this).getBytes());
        outputStream.close();
    }
    
}
