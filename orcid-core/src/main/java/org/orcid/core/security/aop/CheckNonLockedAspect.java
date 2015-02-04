package org.orcid.core.security.aop;

import javax.annotation.Resource;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(100)
public class CheckNonLockedAspect {

    @Resource
    ProfileEntityManager profileEntityManager;
    
    @Resource
    OrcidProfileManager orcidProfileManager;
    
    @Before("@annotation(nonLocked) && (args(orcid))")
    public void checkPermissionsWithAll(NonLocked nonLocked, String orcid) {        
        if(profileEntityManager.isLocked(orcid)) {
            OrcidProfile orcidProfile = orcidProfileManager.retrieveClaimedOrcidBio(orcid);
            throw new LockedException("The given account is locked", orcidProfile.getOrcidIdentifier());
        }
    }

}
