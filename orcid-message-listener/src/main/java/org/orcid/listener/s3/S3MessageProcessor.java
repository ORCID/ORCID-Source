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
package org.orcid.listener.s3;

import java.util.function.Consumer;

import javax.annotation.Resource;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.listener.exception.DeprecatedRecordException;
import org.orcid.listener.exception.LockedRecordException;
import org.orcid.listener.orcid.Orcid12APIClient;
import org.orcid.listener.orcid.Orcid20APIClient;
import org.orcid.listener.persistence.managers.RecordStatusManager;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.orcid.utils.listener.BaseMessage;
import org.orcid.utils.listener.LastModifiedMessage;
import org.orcid.utils.listener.RetryMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Core logic for listeners
 * 
 * @author tom
 *
 */
@Component
public class S3MessageProcessor implements Consumer<LastModifiedMessage> {

    Logger LOG = LoggerFactory.getLogger(S3MessageProcessor.class);

    @Value("${org.orcid.message-listener.api12Enabled:true}")
    private boolean is12IndexingEnabled;

    @Value("${org.orcid.message-listener.api20Enabled:true}")
    private boolean is20IndexingEnabled;

    @Value("${org.orcid.message-listener.api20ActivitiesEnabled:true}")
    private boolean is20ActivitiesIndexingEnabled;

    @Resource
    private Orcid12APIClient orcid12ApiClient;
    @Resource
    private Orcid20APIClient orcid20ApiClient;
    @Resource
    private S3Updater s3Updater;
    @Resource
    private ExceptionHandler exceptionHandler;
    @Resource
    private RecordStatusManager recordStatusManager;

    /**
     * Populates the Amazon S3 buckets
     */
    public void accept(LastModifiedMessage m) {
        String orcid = m.getOrcid();
        update_1_2_API(orcid);
        update_2_0_API(m);
        update_2_0_Activities_API(orcid);
    }

    public void accept(RetryMessage m) {
        String orcid = m.getOrcid();
        AvailableBroker destinationBroker = AvailableBroker.fromValue(m.getMap().get(RetryMessage.BROKER_NAME));
        if (AvailableBroker.DUMP_STATUS_1_2_API.equals(destinationBroker)) {
            update_1_2_API(orcid);
        } else if (AvailableBroker.DUMP_STATUS_2_0_API.equals(destinationBroker)) {
            update_2_0_API(m);
        } else if (AvailableBroker.DUMP_STATUS_2_0_ACTIVITIES_API.equals(destinationBroker)) {
            update_2_0_Activities_API(orcid);
        }
    }

    private void update_1_2_API(String orcid) {
        if (is12IndexingEnabled) {
            try {
                OrcidMessage profile = orcid12ApiClient.fetchPublicProfile(orcid);
                // Update API 1.2
                if (profile != null) {
                    s3Updater.updateS3(orcid, profile);
                    recordStatusManager.markAsSent(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
                }
            } catch (LockedRecordException | DeprecatedRecordException e) {
                try {
                    if (e instanceof LockedRecordException) {
                        LOG.error("Record " + orcid + " is locked");
                        exceptionHandler.handle12LockedRecordException(orcid, ((LockedRecordException) e).getOrcidMessage());
                    } else {
                        LOG.error("Record " + orcid + " is deprecated");
                        exceptionHandler.handle12DeprecatedRecordException(orcid, ((DeprecatedRecordException) e).getOrcidDeprecated());
                    }
                    recordStatusManager.markAsSent(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
                } catch (Exception e1) {
                    LOG.error("Unable to handle LockedRecordException for record " + orcid, e1);
                    recordStatusManager.markAsFailed(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
                }
            } catch (Exception e) {
                // something else went wrong fetching record from ORCID and
                // threw a
                // runtime exception
                LOG.error("Unable to fetch record " + orcid + " for 1.2 API");
                LOG.error(e.getMessage(), e);
                recordStatusManager.markAsFailed(orcid, AvailableBroker.DUMP_STATUS_1_2_API);
            }
        }
    }

    private void update_2_0_API(BaseMessage message) {
        String orcid = message.getOrcid();
        if (is20IndexingEnabled) {
            // Update API 2.0
            try {
                Record record = orcid20ApiClient.fetchPublicRecord(message);
                if (record != null) {
                    s3Updater.updateS3(orcid, record);
                    recordStatusManager.markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
                }
            } catch (LockedRecordException | DeprecatedRecordException e) {
                try {
                    OrcidError error = null;
                    if (e instanceof LockedRecordException) {
                        LOG.error("Record " + orcid + " is locked");
                        error = ((LockedRecordException) e).getOrcidError();
                    } else {
                        LOG.error("Record " + orcid + " is deprecated");
                        error = ((DeprecatedRecordException) e).getOrcidError();
                    }
                    exceptionHandler.handle20Exception(orcid, error);
                    recordStatusManager.markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
                } catch (Exception e1) {
                    LOG.error("Unable to handle LockedRecordException for record " + orcid, e1);
                    recordStatusManager.markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
                }
            } catch (Exception e) {
                // something else went wrong fetching record from ORCID and
                // threw a
                // runtime exception
                LOG.error("Unable to fetch record " + orcid + " for 2.0 API");
                LOG.error(e.getMessage(), e);
                recordStatusManager.markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_API);
            }
        }
    }

    private void update_2_0_Activities_API(String orcid) {
        if (is20ActivitiesIndexingEnabled) {
            // Update API 2.0
            try {
                ActivitiesSummary as = orcid20ApiClient.fetchPublicActivities(orcid);
                if (as != null) {
                    s3Updater.updateS3(orcid, as);
                    recordStatusManager.markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_ACTIVITIES_API);
                }
            } catch (LockedRecordException | DeprecatedRecordException e) {
                try {
                    OrcidError error = null;
                    if (e instanceof LockedRecordException) {
                        LOG.error("Record " + orcid + " is locked");
                        error = ((LockedRecordException) e).getOrcidError();
                    } else {
                        LOG.error("Record " + orcid + " is deprecated");
                        error = ((DeprecatedRecordException) e).getOrcidError();
                    }
                    exceptionHandler.handle20ActivitiesException(orcid, error);
                    recordStatusManager.markAsSent(orcid, AvailableBroker.DUMP_STATUS_2_0_ACTIVITIES_API);
                } catch (Exception e1) {
                    LOG.error("Unable to handle LockedRecordException for record " + orcid, e1);
                    recordStatusManager.markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_ACTIVITIES_API);
                }
            } catch (Exception e) {
                // something else went wrong fetching activities from ORCID and
                // threw a
                // runtime exception
                LOG.error("Unable to fetch activities for record " + orcid + " for 2.0 API");
                LOG.error(e.getMessage(), e);
                recordStatusManager.markAsFailed(orcid, AvailableBroker.DUMP_STATUS_2_0_ACTIVITIES_API);
            }
        }
    }

}
