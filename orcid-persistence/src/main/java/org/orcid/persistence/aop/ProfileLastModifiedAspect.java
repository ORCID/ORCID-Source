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
package org.orcid.persistence.aop;

import java.util.Date;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileAware;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.messaging.JmsMessageSender;
import org.orcid.persistence.messaging.JmsMessageSender.JmsDestination;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Will Simpson
 * 
 */
@Aspect
@Component(value = "profileLastModifiedAspect")
public class ProfileLastModifiedAspect implements PriorityOrdered {
    
    // shared REQUEST_PROFILE_LAST_MODIFIED also used in other places
    public static String REQUEST_PROFILE_LAST_MODIFIED = "REQUEST_PROFILE_LAST_MODIFIED";

    private static final int PRECEDENCE = 50;

    @Resource
    private ProfileDao profileDao;
    
    @Resource 
    JmsMessageSender messaging;

    private boolean enabled = true;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileLastModifiedAspect.class);

    //@formatter:off
    private static final String POINTCUT_DEFINITION_BASE = "(execution(* org.orcid.persistence.dao.*.remove*(..))"
            + "|| execution(* org.orcid.persistence.dao.*.delete*(..))" + "|| execution(* org.orcid.persistence.dao.*.update*(..))"
            + "|| execution(* org.orcid.persistence.dao.*.merge*(..))" + "|| execution(* org.orcid.persistence.dao.*.add*(..))"
            + "|| execution(* org.orcid.persistence.dao.*.persist*(..)))"
            + "&& !@annotation(org.orcid.persistence.aop.ExcludeFromProfileLastModifiedUpdate)"
            + "&& !within(org.orcid.persistence.dao.impl.WebhookDaoImpl)";

    //@formatter:on

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @AfterReturning(POINTCUT_DEFINITION_BASE + " && args(orcid, ..)")
    public void updateProfileLastModified(JoinPoint joinPoint, String orcid) {
        if (!enabled) {
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            if (!OrcidStringUtils.isValidOrcid(orcid)) {
                LOGGER.debug("Invalid ORCID for last modified date update: orcid={}, join point={}", orcid, joinPoint);
            }
        }
        profileDao.updateLastModifiedDateAndIndexingStatus(orcid);
        /* ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        Date lastMod = profileDao.retrieveLastModifiedDate(orcid);
        if (sra != null)
            sra.setAttribute(REQUEST_PROFILE_LAST_MODIFIED, lastMod,ServletRequestAttributes.SCOPE_REQUEST); */
        messaging.sendMap(ImmutableMap.of("orcid", orcid, "method", joinPoint.getTarget().getClass().getName()+"."+joinPoint.getSignature().getName()), JmsDestination.UPDATED_ORCIDS);
    }

    @AfterReturning(POINTCUT_DEFINITION_BASE + " && args(profileAware, ..)")
    public void updateProfileLastModified(JoinPoint joinPoint, ProfileAware profileAware) {
        ProfileEntity profile = profileAware.getProfile();
        if (profile != null) {
            String orcid = profile.getId();
            updateProfileLastModified(joinPoint, orcid);
        }
    }

    @Override
    public int getOrder() {
        return PRECEDENCE;
    }

}
