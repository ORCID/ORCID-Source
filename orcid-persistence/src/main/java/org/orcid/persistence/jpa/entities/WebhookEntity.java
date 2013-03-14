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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;

/**
 * @author Will Simpson
 */
@Entity
@Table(name = "webhook")
@IdClass(WebhookEntityPk.class)
//@formatter:off
@NamedNativeQuery(name = WebhookEntity.FIND_WEBHOOKS_READY_TO_PROCESS,query =
"SELECT * FROM webhook w " +
"JOIN profile p ON p.orcid = w.orcid AND p.last_modified >= w.last_modified " +
"JOIN client_details c ON c.client_details_id = w.client_details_id AND c.webhooks_enabled = 'true' " +
"WHERE w.enabled = 'true' " +
"AND w.failed_attempt_count = 0 OR unix_timestamp(w.last_failed) + w.failed_attempt_count * :retryDelayMinutes * 60 < unix_timestamp(now()) " +
"ORDER BY p.last_modified"
, resultClass = WebhookEntity.class)
//@formatter:on
public class WebhookEntity extends BaseEntity<WebhookEntityPk> {

    private WebhookEntityPk id;
    private ProfileEntity profile;
    private String uri;
    private ClientDetailsEntity clientDetails;
    private Date lastFailed;
    private int failedAttemptCount;
    private boolean enabled = true;
    private Date disabledDate;
    private String disabledComments;

    private static final long serialVersionUID = 1L;
    public static final String FIND_WEBHOOKS_READY_TO_PROCESS = "findWebhooksReadyToProcess";

    @Override
    @Transient
    public WebhookEntityPk getId() {
        return id;
    }

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false, updatable = false, insertable = false)
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        this.profile = profile;
    }

    @Id
    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_details_id", nullable = false)
    public ClientDetailsEntity getClientDetails() {
        return clientDetails;
    }

    public void setClientDetails(ClientDetailsEntity clientDetails) {
        this.clientDetails = clientDetails;
    }

    @Column(name = "last_failed")
    public Date getLastFailed() {
        return lastFailed;
    }

    public void setLastFailed(Date lastFailed) {
        this.lastFailed = lastFailed;
    }

    @Column(name = "failed_attempt_count")
    public int getFailedAttemptCount() {
        return failedAttemptCount;
    }

    public void setFailedAttemptCount(int failedAttemptCount) {
        this.failedAttemptCount = failedAttemptCount;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Column(name = "disabled_date")
    public Date getDisabledDate() {
        return disabledDate;
    }

    public void setDisabledDate(Date disabledDate) {
        this.disabledDate = disabledDate;
    }

    @Column(name = "disabled_comments")
    public String getDisabledComments() {
        return disabledComments;
    }

    public void setDisabledComments(String disabledComments) {
        this.disabledComments = disabledComments;
    }

}
