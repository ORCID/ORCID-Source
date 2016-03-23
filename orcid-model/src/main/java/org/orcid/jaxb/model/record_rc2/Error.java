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
package org.orcid.jaxb.model.record_rc2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc2.Url;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "responseCode", "developerMessage", "userMessage", "errorCode", "moreInfo" })
@XmlRootElement(name = "error", namespace = "http://www.orcid.org/ns/error")
public class Error implements Serializable, WorkBulkElement {
    private static final long serialVersionUID = -219864157913079409L;
    @XmlElement(namespace = "http://www.orcid.org/ns/error", name = "response-code")
    private String responseCode;
    @XmlElement(namespace = "http://www.orcid.org/ns/error", name = "developer-message")
    private String developerMessage;
    @XmlElement(namespace = "http://www.orcid.org/ns/error", name = "user-message")
    private String userMessage;
    @XmlElement(namespace = "http://www.orcid.org/ns/error", name = "error-code")
    private String errorCode;
    @XmlElement(namespace = "http://www.orcid.org/ns/error", name = "more-info")
    private Url moreInfo;

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Url getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(Url moreInfo) {
        this.moreInfo = moreInfo;
    }
}
