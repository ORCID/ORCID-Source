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

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.annotation.Resource;

import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.InstitutionalSignInManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.UserConnectionStatus;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.springframework.transaction.annotation.Transactional;

public class InstitutionalSignInManagerImpl implements InstitutionalSignInManager {

    @Resource
    protected UserConnectionDao userConnectionDao;
    
    @Resource
    protected OrcidUrlManager orcidUrlManager;
    
    @Resource
    protected ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;
    
    @Resource
    protected OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;
    
    @Resource
    protected NotificationManager notificationManager;
    
    @Override
    @Transactional
    public void createUserConnectionAndNotify(String idType, String remoteUserId, String displayName, String providerId, String userOrcid) throws UnsupportedEncodingException {
        UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(remoteUserId, providerId,
                idType);
        if (userConnectionEntity == null) {
            userConnectionEntity = new UserconnectionEntity();
            String randomId = Long.toString(new Random(Calendar.getInstance().getTimeInMillis()).nextLong());
            UserconnectionPK pk = new UserconnectionPK(randomId, providerId, remoteUserId);            
            userConnectionEntity.setOrcid(userOrcid);
            userConnectionEntity.setProfileurl(orcidUrlManager.getBaseUriHttp() + "/" + userOrcid);
            userConnectionEntity.setDisplayname(displayName);
            userConnectionEntity.setRank(1);
            userConnectionEntity.setId(pk);
            userConnectionEntity.setLinked(true);
            userConnectionEntity.setLastLogin(new Date());
            userConnectionEntity.setIdType(idType);
            userConnectionEntity.setConnectionSatus(UserConnectionStatus.NOTIFIED);
            userConnectionDao.persist(userConnectionEntity);
        }    
        
        sendNotification(userOrcid, providerId);
    }        
    
    private void sendNotification(String userOrcid, String providerId) throws UnsupportedEncodingException {
        try {
            ClientDetailsEntity clientDetails = clientDetailsEntityCacheManager.retrieveByIdP(providerId);
            boolean clientKnowsUser = doesClientKnowUser(userOrcid, clientDetails.getClientId());
            //If the client doesn't know about the user yet, send a notification
            if(!clientKnowsUser) {
                notificationManager.sendAcknowledgeMessage(userOrcid, clientDetails.getClientId());
            }
        } catch(IllegalArgumentException e) {
            //The provided IdP hasn't not been linked to any client yet.
        }
    }
    
    private boolean doesClientKnowUser(String userOrcid, String clientId) {
        List<OrcidOauth2TokenDetail> existingTokens = orcidOauth2TokenDetailDao.findByClientIdAndUserName(clientId, userOrcid);
        if(existingTokens == null || existingTokens.isEmpty()) {
            return false;
        }
        
        Date now = new Date();
        for(OrcidOauth2TokenDetail token : existingTokens) {
            if(token.getTokenExpiration() != null && token.getTokenExpiration().after(now)) {
                return true;                                      
            }
        }
        
        return false;
    }
}
