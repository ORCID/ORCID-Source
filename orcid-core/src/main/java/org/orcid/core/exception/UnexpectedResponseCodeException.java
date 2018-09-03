package org.orcid.core.exception;

import java.io.IOException;
import java.net.HttpURLConnection;

public class UnexpectedResponseCodeException extends IOException {
    private static final long serialVersionUID = -5063540615913131647L;
    int expectedCode;
    int receivedCode;

    public UnexpectedResponseCodeException(int received) {
        this.expectedCode = HttpURLConnection.HTTP_OK;
        this.receivedCode = received;
    }

    public UnexpectedResponseCodeException(int expected, int received) {
        this.expectedCode = expected;
        this.receivedCode = received;
    }

    public int getExpectedCode() {
        return expectedCode;
    }

    public void setExpectedCode(int expectedCode) {
        this.expectedCode = expectedCode;
    }

    public int getReceivedCode() {
        return receivedCode;
    }

    public void setReceivedCode(int receivedCode) {
        this.receivedCode = receivedCode;
    }

}
