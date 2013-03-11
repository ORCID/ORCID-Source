/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities;

import java.net.URI;
import java.util.Date;

import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;

/**
 * @author Will Simpson
 */
public class WebhookEntity extends BaseEntity<WebhookEntityPk> {

    private WebhookEntityPk id;
    private String orcid;
    private ClientDetailsEntity clientDetails;
    private URI uri;
    private Date lastFailed;
    private Integer failedAttemptCount;
    private boolean enabled;
    private Date disabledDate;
    private String disabledComments;

    private static final long serialVersionUID = 1L;

    @Override
    public WebhookEntityPk getId() {
        return id;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public ClientDetailsEntity getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(ClientDetailsEntity clientDetails) {
        this.clientDetails = clientDetails;
    }

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }

    public Date getLastFailed() {
        return lastFailed;
    }

    public void setLastFailed(Date lastFailed) {
        this.lastFailed = lastFailed;
    }

    public Integer getFailedAttemptCount() {
        return failedAttemptCount;
    }

    public void setFailedAttemptCount(Integer failedAttemptCount) {
        this.failedAttemptCount = failedAttemptCount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Date getDisabledDate() {
        return disabledDate;
    }

    public void setDisabledDate(Date disabledDate) {
        this.disabledDate = disabledDate;
    }

    public String getDisabledComments() {
        return disabledComments;
    }

    public void setDisabledComments(String disabledComments) {
        this.disabledComments = disabledComments;
    }

}
