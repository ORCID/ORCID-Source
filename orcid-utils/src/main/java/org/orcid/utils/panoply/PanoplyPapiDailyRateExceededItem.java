package org.orcid.utils.panoply;

import java.time.LocalDate;

public class PanoplyPapiDailyRateExceededItem {
    private String ipAddress;
    private String clientId;
    private String email;
    private String orcid;
    private LocalDate requestDate;
    
    
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getClientId() {
        return clientId;
    }
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getOrcid() {
        return orcid;
    }
    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    public LocalDate getRequestDate() {
        return requestDate;
    }
    public void setRequestDate(LocalDate requestDate) {
        this.requestDate = requestDate;
    }
    
    @Override
    public String toString() {
        return "PanoplyPapiDailyRateExceededItem{" + "ipAddress=" + ipAddress + ", clientId='" + clientId + '\'' + ", email='" + email + '\'' + ", orcid='" + orcid + '\''
                + ", requestDate='" + requestDate + '\'' + '}';
    }
}
