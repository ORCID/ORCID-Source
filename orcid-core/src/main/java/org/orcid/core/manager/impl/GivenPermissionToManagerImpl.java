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
package org.orcid.core.manager.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.orcid.core.manager.GivenPermissionToManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.message.ApprovalDate;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.orcid.utils.DateUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class GivenPermissionToManagerImpl implements GivenPermissionToManager {

    @Resource
    private GivenPermissionToDao givenPermissionToDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Override
    public GivenPermissionToEntity findByGiverAndReceiverOrcid(String giverOrcid, String receiverOrcid) {
        return givenPermissionToDao.findByGiverAndReceiverOrcid(giverOrcid, receiverOrcid);
    }

    @Override
    public void remove(String giverOrcid, String receiverOrcid) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                givenPermissionToDao.remove(giverOrcid, receiverOrcid);
                profileEntityManager.updateLastModifed(giverOrcid);
                profileEntityManager.updateLastModifed(receiverOrcid);
            }
        });            
    }

    @Override
    public void create(String userOrcid, String delegateOrcid) {
        GivenPermissionToEntity existing = findByGiverAndReceiverOrcid(userOrcid, delegateOrcid);
        if (existing == null) {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    Date approvalDate = new Date();
                    // Create the delegate relationship
                    GivenPermissionToEntity permission = new GivenPermissionToEntity();
                    permission.setGiver(userOrcid);
                    ProfileSummaryEntity receiver = new ProfileSummaryEntity(delegateOrcid);
                    permission.setReceiver(receiver);
                    permission.setApprovalDate(approvalDate);
                    givenPermissionToDao.merge(permission);

                    // Notify
                    DelegationDetails details = new DelegationDetails();
                    details.setApprovalDate(new ApprovalDate(DateUtils.convertToXMLGregorianCalendar(approvalDate)));
                    DelegateSummary summary = new DelegateSummary();
                    summary.setOrcidIdentifier(new OrcidIdentifier(delegateOrcid));
                    details.setDelegateSummary(summary);
                    notificationManager.sendNotificationToAddedDelegate(userOrcid, details);

                    // Update last modified on delegate's profile so that the
                    // granting user is visible to them immediately
                    profileEntityManager.updateLastModifed(delegateOrcid);
                }
            });
        }
    }

}
