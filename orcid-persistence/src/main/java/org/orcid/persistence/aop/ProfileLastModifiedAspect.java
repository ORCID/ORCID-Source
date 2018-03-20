package org.orcid.persistence.aop;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidAware;
import org.orcid.persistence.jpa.entities.ProfileAware;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.PriorityOrdered;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 
 * @author Will Simpson
 * 
 */
@Aspect
public class ProfileLastModifiedAspect implements PriorityOrdered {

    private static final int PRECEDENCE = 50;

    private static String REQUEST_PROFILE_LAST_MODIFIED = "REQUEST_PROFILE_LAST_MODIFIED";

    private ProfileDao profileDao;

    private boolean enabled = true;
    
    private String name = "default";

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

    public void setProfileDao(ProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    /** Runs after any method that updates a record.
     * Updates the last modified and refreshes the request-scope last modified cache.
     * 
     * @param joinPoint
     * @param orcid
     */
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
        
        //update and clear scope cache
        this.updateLastModifiedDateAndIndexingStatus(orcid);
    }

    @AfterReturning(POINTCUT_DEFINITION_BASE + " && args(profileAware, ..)")
    public void updateProfileLastModified(JoinPoint joinPoint, ProfileAware profileAware) {
        if (!enabled) {
            return;
        }
        ProfileEntity profile = profileAware.getProfile();
        if (profile != null) {
            String orcid = profile.getId();
            updateProfileLastModified(joinPoint, orcid);
        }
    }

    @AfterReturning(POINTCUT_DEFINITION_BASE + " && args(orcidAware, ..)")
    public void updateProfileLastModified(JoinPoint joinPoint, OrcidAware orcidAware) {
        if (!enabled) {
            return;
        }
        String orcid = orcidAware.getOrcid();
        if(!StringUtils.isEmpty(orcid)) {
            updateProfileLastModified(joinPoint, orcid);
        }
    }
    
    @Override
    public int getOrder() {
        return PRECEDENCE;
    }

    /** Updates the last modified date and clears the request-scope last modified cache.
     * 
     * @param orcid
     */    
    public void updateLastModifiedDateAndIndexingStatus(String orcid) {
        if (!enabled) {
            return;
        }
        profileDao.updateLastModifiedDateAndIndexingStatus(orcid, IndexingStatus.PENDING);
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null)
            sra.setAttribute(sraKey(orcid), null, ServletRequestAttributes.SCOPE_REQUEST);             
    }

    /** Fetches the last modified from the request-scope last modified cache
     * If not present, fetches from the DB and populates the request-scope last modified cache.
     * 
     * @param orcid
     * @return
     */
    public Date retrieveLastModifiedDate(String orcid) {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        Date lastMod = null;
        if (sra != null)
            lastMod = (Date) sra.getAttribute(sraKey(orcid), ServletRequestAttributes.SCOPE_REQUEST);
        if (lastMod == null) {
            lastMod = profileDao.retrieveLastModifiedDate(orcid);
            if (sra != null)
                sra.setAttribute(sraKey(orcid), lastMod, ServletRequestAttributes.SCOPE_REQUEST);
        }
        return lastMod;
    }

    private String sraKey(String orcid) {
        return REQUEST_PROFILE_LAST_MODIFIED + '_' + name + '_' + orcid;
    }
}
