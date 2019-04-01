package org.orcid.core.security.aop;

import javax.annotation.Resource;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.orcid.core.manager.v3.read_only.ProfileEntityManagerReadOnly;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * @author Angel Montenegro
 * */
@Aspect
@Component
@Order(100)
public class CheckNonLockedAspect {

    @Resource(name = "profileEntityManagerReadOnlyV3")
    ProfileEntityManagerReadOnly profileEntityManager;
    
    @Before("@annotation(nonLocked) && (args(orcid))")
    public void checkPermissionsWithAll(NonLocked nonLocked, String orcid) {        
        if(profileEntityManager.isLocked(orcid)) {            
            throw new LockedException("The given account " + orcid + " is locked", orcid);
        }
    }

}
