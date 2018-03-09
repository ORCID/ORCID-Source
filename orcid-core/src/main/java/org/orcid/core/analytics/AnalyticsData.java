package org.orcid.core.analytics;

public class AnalyticsData {

    private String userAgent;

    private String ipAddress;

    private String clientDetailsString;

    private String url;

    private String category;

    private String contentType;

    private Integer responseCode;

    private String apiVersion;

    private String method;

    private String clientId;

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getClientDetailsString() {
        return clientDetailsString;
    }

    public void setClientDetailsString(String clientDetailsId) {
        this.clientDetailsString = clientDetailsId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Integer getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(Integer responseCode) {
        this.responseCode = responseCode;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\n Agent: ").append(userAgent);
        builder.append("\n Content/Accept: ").append(contentType);
        builder.append("\n Client: ").append(clientDetailsString);
        builder.append("\n IP: ").append(ipAddress);
        builder.append("\n Method: ").append(method);
        builder.append("\n URL: ").append(url);
        builder.append("\n Response: ").append(responseCode);
        builder.append("\n API version: ").append(apiVersion);
        builder.append("\n Category: ").append(category);
        return builder.toString();
    }

}
