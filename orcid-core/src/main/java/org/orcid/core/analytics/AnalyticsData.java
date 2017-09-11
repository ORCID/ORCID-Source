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
        builder.append("\nAgent: ").append(userAgent);
        builder.append("\nContent/Accept: ").append(contentType);
        builder.append("\nClient: ").append(clientDetailsString);
        builder.append("\nIP: ").append(ipAddress);
        builder.append("\nMethod: ").append(method);
        builder.append("\nURL: ").append(url);
        builder.append("\nResponse: ").append(responseCode);
        builder.append("\nAPI version: ").append(apiVersion);
        builder.append("\nCategory: ").append(category);
        return builder.toString();
    }

}
