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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.InstitutionalSignInManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.SlackManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.persistence.dao.OrcidOauth2TokenDetailDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.persistence.jpa.entities.UserConnectionStatus;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.orcid.pojo.HeaderCheckResult;
import org.orcid.pojo.HeaderMismatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class InstitutionalSignInManagerImpl implements InstitutionalSignInManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(InstitutionalSignInManagerImpl.class);

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
    
    @Resource
    private SlackManager slackManager;
    
    @Override
    @Transactional
    public void createUserConnectionAndNotify(String idType, String remoteUserId, String displayName, String providerId, String userOrcid, Map<String, String> headers)
            throws UnsupportedEncodingException {
        UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(remoteUserId, providerId, idType);
        if (userConnectionEntity == null) {
            LOGGER.info("No user connection found for idType={}, remoteUserId={}, displayName={}, providerId={}, userOrcid={}",
                    new Object[] { idType, remoteUserId, displayName, providerId, userOrcid });
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
            userConnectionEntity.setHeadersJson(JsonUtils.convertToJsonString(headers));
            userConnectionDao.persist(userConnectionEntity);
        } else {
            LOGGER.info("Found existing user connection, {}", userConnectionEntity);
        }

        sendNotification(userOrcid, providerId);
    }
    
    @Override
    public void sendNotification(String userOrcid, String providerId) throws UnsupportedEncodingException {
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
    
    @Override
    public HeaderCheckResult checkHeaders(Map<String, String> originalHeaders, Map<String, String> currentHeaders) {
        HeaderCheckResult result = new HeaderCheckResult();
        List<String> headersToCheck = new ArrayList<>();
        headersToCheck.addAll(Arrays.asList(POSSIBLE_REMOTE_USER_HEADERS));
        headersToCheck.add(EPPN_HEADER);
        for (String headerName : headersToCheck) {
            String original = originalHeaders.get(headerName);
            String current = currentHeaders.get(headerName);
            // Only compare where both are not blank, because otherwise could
            // just be an IdP config change to add/remove the attribute
            if (StringUtils.isNoneBlank(original, current)) {
                if (!current.equals(original)) {
                    result.addMismatch(new HeaderMismatch(headerName, original, current));
                }
            }
        }
        if (!result.isSuccess()) {
            String message = String.format("Institutional sign in header check failed: %s, originalHeaders=%s", result, originalHeaders);
            LOGGER.info(message);
            slackManager.sendSystemAlert(message);
        }
        return result;
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
