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
import javax.persistence.ColumnResult;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;

/**
 * @author Will Simpson
 */
@Entity
@Table(name = "webhook")
@IdClass(WebhookEntityPk.class)
@NamedNativeQueries( {
        @NamedNativeQuery(name = WebhookEntity.COUNT_WEBHOOKS_READY_TO_PROCESS, query = "SELECT COUNT(*) webhook_count "
                + WebhookEntity.WEBHOOKS_READY_TO_PROCESS_FROM_CLAUSE, resultSetMapping = "countMapping"),
        @NamedNativeQuery(name = WebhookEntity.FIND_WEBHOOKS_READY_TO_PROCESS, query = "SELECT *  " + WebhookEntity.WEBHOOKS_READY_TO_PROCESS_FROM_CLAUSE
                + " ORDER BY w.profile_last_modified", resultClass = WebhookEntity.class) })
@SqlResultSetMapping(name = "countMapping", columns = @ColumnResult(name = "webhook_count"))
public class WebhookEntity extends BaseEntity<WebhookEntityPk> implements ProfileAware {

    private ProfileEntity profile;
    private String uri;
    private ClientDetailsEntity clientDetails;
    private Date lastSent;
    private Date profileLastModified;
    private Date lastFailed;
    private int failedAttemptCount;
    private boolean enabled = true;
    private Date disabledDate;
    private String disabledComments;

    private static final long serialVersionUID = 1L;
    public static final String FIND_WEBHOOKS_READY_TO_PROCESS = "findWebhooksReadyToProcess";
    public static final String COUNT_WEBHOOKS_READY_TO_PROCESS = "countWebhooksReadyToProcess";
    public static final String WEBHOOKS_READY_TO_PROCESS_FROM_CLAUSE = "FROM webhook w "
            + "JOIN client_details c ON c.client_details_id = w.client_details_id AND c.webhooks_enabled = 'true'" 
            + "   WHERE w.enabled = 'true' "
            + "   AND (w.profile_last_modified >= w.last_sent OR (w.last_sent IS NULL AND w.profile_last_modified >= w.date_created))"
            + "   AND (w.failed_attempt_count = 0 OR unix_timestamp(w.last_failed) + w.failed_attempt_count * :retryDelayMinutes * 60 < unix_timestamp(now()))";

    @Override
    @Transient
    public WebhookEntityPk getId() {
        return new WebhookEntityPk(profile, uri);
    }

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "orcid", nullable = false, updatable = false, insertable = false)
    public ProfileEntity getProfile() {
        return profile;
    }

    public void setProfile(ProfileEntity profile) {
        if (profile != null) this.profileLastModified = profile.getLastModified();
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

    @Column(name = "last_sent")
    public Date getLastSent() {
        return lastSent;
    }

    public void setLastSent(Date lastSent) {
        this.lastSent = lastSent;
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

    @Column(name = "profile_last_modified")
    public Date getProfileLastModified() {
        return profileLastModified;
    }

    public void setProfileLastModified(Date profileLastModified) {
        this.profileLastModified = profileLastModified;
    }

}
