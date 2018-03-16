package org.orcid.core.cli.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class ApiLog {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiLog.class);

    private String method;

    private String endpoint;

    private HttpStatus status;

    private String bearerToken;

    private String version;

    private ApiLog() {

    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public static ApiLog parse(String line) {
        try {
            ApiLog log = new ApiLog();
            log.setMethod(getMethod(line));
            log.setEndpoint(getEndpoint(line));
            log.setStatus(getStatus(line));
            log.setBearerToken(getBearerToken(line));
            log.setVersion(getVersion(log.getEndpoint()));
            return log;
        } catch (Exception e) {
            LOGGER.warn("Failed to parse line {}", line, e);
            return null;
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(method);
        builder.append(" ").append(endpoint);
        builder.append(" ").append(status);
        builder.append(" ").append(bearerToken);
        builder.append(" ").append(version);
        return builder.toString();
    }

    private static String getVersion(String endpoint) {
        int index = endpoint.indexOf("/", 1);
        int nextIndex = endpoint.indexOf("/", index + 1);
        if (nextIndex < 0) {
            return null;
        }

        String possibleVersion = endpoint.substring(index + 1, nextIndex);
        if (possibleVersion.matches("v\\d{1,}.*")) {
            return possibleVersion;
        }
        return "v1.x";
    }

    private static String getBearerToken(String line) {
        int index = line.toLowerCase().indexOf("bearer");
        if (index < 0) {
            return null;
        }
        index += "bearer".length();

        int nextIndex = line.indexOf("\"", index);
        return line.substring(index, nextIndex).trim();
    }

    private static HttpStatus getStatus(String line) {
        int index = line.indexOf("\"");
        index = line.indexOf("\"", index + 1);
        index = line.indexOf(" ", index);
        int nextIndex = line.indexOf(" ", index + 1);
        String httpStatusString = line.substring(index + 1, nextIndex);
        return HttpStatus.valueOf(Integer.parseInt(httpStatusString));
    }

    private static String getEndpoint(String line) {
        int index = line.indexOf("\"");
        index = line.indexOf(" ", index);
        int nextIndex = line.indexOf(" ", index + 1);
        return line.substring(index + 1, nextIndex);
    }

    private static String getMethod(String line) {
        int index = line.indexOf("\"");
        int nextIndex = line.indexOf(" ", index);
        return line.substring(index + 1, nextIndex);
    }

}
