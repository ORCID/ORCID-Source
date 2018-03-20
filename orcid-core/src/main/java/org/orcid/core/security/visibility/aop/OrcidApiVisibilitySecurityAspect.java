package org.orcid.core.security.visibility.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

/**
 * @author Declan Newman (declan) Date: 16/03/2012
 */
@Aspect
@Component
public class OrcidApiVisibilitySecurityAspect {

    @Resource(name = "visibilityFilter")
    private VisibilityFilter visibilityFilter;

    @AfterReturning(pointcut = "@annotation(visibilityAnnotation)", returning = "response")
    public void simpleVisibilityResponseFilter(Response response, VisibilityControl visibilityAnnotation) {
        Object entity = response != null ? response.getEntity() : null;
        if (entity != null && OrcidMessage.class.isAssignableFrom(entity.getClass())) {
            visibilityFilter.filter((OrcidMessage) entity, visibilityAnnotation.visibilities());
        }
    }

    @AfterReturning(pointcut = "@annotation(visibilityAnnotation)", returning = "profile")
    public void simpleVisibilityProfileFilter(OrcidProfile profile, VisibilityControl visibilityAnnotation) {
        if (profile != null) {
            OrcidMessage message = new OrcidMessage(profile);
            visibilityFilter.filter(message, visibilityAnnotation.visibilities());
        }
    }

}
