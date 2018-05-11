package org.orcid.core.manager.v3.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.GivenPermissionToManager;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class GivenPermissionToManagerImpl implements GivenPermissionToManager {

    @Resource
    private GivenPermissionToDao givenPermissionToDao;

    @Resource
    private TransactionTemplate transactionTemplate;
    
    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;
    
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
        GivenPermissionToEntity existing = givenPermissionToDao.findByGiverAndReceiverOrcid(userOrcid, delegateOrcid);
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
                    notificationManager.sendNotificationToAddedDelegate(userOrcid, delegateOrcid);

                    // Update last modified on delegate's profile so that the
                    // granting user is visible to them immediately
                    profileEntityManager.updateLastModifed(delegateOrcid);
                }
            });
        }
    }

}
