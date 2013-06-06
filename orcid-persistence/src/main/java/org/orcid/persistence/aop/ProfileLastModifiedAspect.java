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
package org.orcid.persistence.aop;

import javax.annotation.Resource;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.ProfileAware;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Will Simpson
 * 
 */
@Aspect
@Component
public class ProfileLastModifiedAspect implements PriorityOrdered {

    private static final int PRECEDENCE = 50;

    @Resource
    private ProfileDao profileDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileLastModifiedAspect.class);

    //@formatter:off
    private static final String POINTCUT_DEFINITION_BASE = "(execution(* org.orcid.persistence.dao.*.remove*(..))"
            + "|| execution(* org.orcid.persistence.dao.*.delete*(..))" + "|| execution(* org.orcid.persistence.dao.*.update*(..))"
            + "|| execution(* org.orcid.persistence.dao.*.merge*(..))" + "|| execution(* org.orcid.persistence.dao.*.add*(..)))"
            + "&& !@annotation(org.orcid.persistence.aop.ExcludeFromProfileLastModifiedUpdate)" + "&& !within(org.orcid.persistence.dao.impl.ProfileDaoImpl)"
            + "&& !within(org.orcid.persistence.dao.impl.WebhookDaoImpl)";

    //@formatter:on

    @AfterReturning(POINTCUT_DEFINITION_BASE + " && args(orcid, ..)")
    public void updateProfileLastModified(JoinPoint joinPoint, String orcid) {
        if (LOGGER.isDebugEnabled()) {
            if (!OrcidStringUtils.isValidOrcid(orcid)) {
                LOGGER.debug("Invalid ORCID for last modified date update: orcid={}, join point={}", orcid, joinPoint);
            }
        }
        profileDao.updateLastModifiedDateAndIndexingStatus(orcid);
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
