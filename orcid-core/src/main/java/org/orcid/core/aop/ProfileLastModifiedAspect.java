package org.orcid.core.aop;

import java.util.Date;

import jakarta.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.orcid.core.utils.cache.redis.RedisClient;
import org.orcid.persistence.dao.ProfileLastModifiedDao;
import org.orcid.persistence.jpa.entities.IndexingStatus;
import org.orcid.persistence.jpa.entities.OrcidAware;
import org.orcid.utils.OrcidStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.PriorityOrdered;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
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

    private ProfileLastModifiedDao profileLastModifiedDao;

    private boolean enabled = true;
    
    private String name = "default";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileLastModifiedAspect.class);    
    
    private static final String UPDATE_PROFILE_LAST_MODIFIED = "@annotation(org.orcid.persistence.aop.UpdateProfileLastModified)";
    
    private static final String UPDATE_PROFILE_LAST_MODIFIED_AND_INDEXING_STATUS = "@annotation(org.orcid.persistence.aop.UpdateProfileLastModifiedAndIndexingStatus)";
    
    @Resource
    private RedisClient redisClient;    

    @Resource
    private PlatformTransactionManager transactionManager;
    
    @Value("${org.orcid.core.utils.cache.redis.summary.enabled:false}") 
    private boolean isSummaryCacheEnabled;
    
    public boolean isEnabled() {
        return enabled;
    }

    public void setProfileLastModifiedDao(ProfileLastModifiedDao profileLastModifiedDao) {
        this.profileLastModifiedDao = profileLastModifiedDao;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @AfterReturning(UPDATE_PROFILE_LAST_MODIFIED)
    public void updateProfileLastModified(JoinPoint joinPoint) {
        if (!enabled) {
            return;
        }
        String orcid = resolveOrcid(joinPoint.getArgs());
        if (StringUtils.isEmpty(orcid)) {
            LOGGER.debug("Skipping last modified update: no ORCID argument found for join point={}", joinPoint);
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            if (!OrcidStringUtils.isValidOrcid(orcid)) {
                LOGGER.debug("Invalid ORCID for last modified date update: orcid={}, join point={}", orcid, joinPoint);
            }
        }
        Date before = safeRetrieveLastModifiedDate(orcid);
        this.updateLastModifiedDate(orcid);
        Date after = safeRetrieveLastModifiedDate(orcid);
        LOGGER.info("ProfileLastModifiedAspect updateLastModifiedDate source={} orcid={} before={} after={}",
                joinPoint.getSignature().toShortString(), orcid, before, after);
    }
    
    @AfterReturning(UPDATE_PROFILE_LAST_MODIFIED_AND_INDEXING_STATUS)
    public void updateProfileLastModifiedAndIndexingStatus(JoinPoint joinPoint) {
        if (!enabled) {
            return;
        }
        String orcid = resolveOrcid(joinPoint.getArgs());
        if (StringUtils.isEmpty(orcid)) {
            LOGGER.debug("Skipping last modified/indexing update: no ORCID argument found for join point={}", joinPoint);
            return;
        }
        if (LOGGER.isDebugEnabled()) {
            if (!OrcidStringUtils.isValidOrcid(orcid)) {
                LOGGER.debug("Invalid ORCID for last modified date update: orcid={}, join point={}", orcid, joinPoint);
            }
        }
        Date before = safeRetrieveLastModifiedDate(orcid);
        this.updateLastModifiedDateAndIndexingStatus(orcid);
        Date after = safeRetrieveLastModifiedDate(orcid);
        LOGGER.info("ProfileLastModifiedAspect updateLastModifiedDateAndIndexingStatus source={} orcid={} before={} after={}",
                joinPoint.getSignature().toShortString(), orcid, before, after);
    }
    
    @Override
    public int getOrder() {
        return PRECEDENCE;
    }

    /** Updates the last modified date and indexing status and clears the request-scope last modified cache.
     * 
     * @param orcid
     */    
    public void updateLastModifiedDateAndIndexingStatus(String orcid) {
        if (!enabled) {
            return;
        }
        try {
            new TransactionTemplate(transactionManager)
                    .executeWithoutResult(status -> profileLastModifiedDao.updateLastModifiedDateAndIndexingStatus(orcid, IndexingStatus.PENDING));
        } catch(Exception e) {
            LOGGER.error("Unable to update last modified and indexing status for " + orcid, e);
        }
        
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null)
            sra.setAttribute(sraKey(orcid), null, ServletRequestAttributes.SCOPE_REQUEST); 
        
        // Clear redis caches
        evictCaches(orcid);
    }
    
    /** Updates the last modified date and clears the request-scope last modified cache.
     * 
     * @param orcid
     */    
    public void updateLastModifiedDate(String orcid) {
        if (!enabled) {
            return;
        }
        try {
            new TransactionTemplate(transactionManager)
                    .executeWithoutResult(status -> profileLastModifiedDao.updateLastModifiedDateWithoutResult(orcid));
        } catch(Exception e) {
            LOGGER.error("Unable to update last modified for " + orcid, e);
        }
        
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null)
            sra.setAttribute(sraKey(orcid), null, ServletRequestAttributes.SCOPE_REQUEST);
        
        // Clear redis caches
        evictCaches(orcid);
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
            lastMod = profileLastModifiedDao.retrieveLastModifiedDate(orcid);
            if (sra != null)
                sra.setAttribute(sraKey(orcid), lastMod, ServletRequestAttributes.SCOPE_REQUEST);
        }
        return lastMod;
    }

    private String sraKey(String orcid) {
        return REQUEST_PROFILE_LAST_MODIFIED + '_' + name + '_' + orcid;
    }

    private Date safeRetrieveLastModifiedDate(String orcid) {
        try {
            return profileLastModifiedDao.retrieveLastModifiedDate(orcid);
        } catch (Exception e) {
            LOGGER.warn("Unable to read last modified date for diagnostics. orcid={}", orcid, e);
            return null;
        }
    }

    private String resolveOrcid(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object arg : args) {
            if (arg instanceof String str && OrcidStringUtils.isValidOrcid(str)) {
                return str;
            }
            if (arg instanceof OrcidAware orcidAware && !StringUtils.isEmpty(orcidAware.getOrcid())) {
                return orcidAware.getOrcid();
            }
        }
        return null;
    }
    
    public void evictCaches(String orcid) {        
        // Evict the summary cache
        redisClient.remove(orcid + "-summary");
    }
}
