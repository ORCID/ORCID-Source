package org.orcid.api.notificationsV3.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.api.memberV3.server.delegator.MemberV3ApiServiceDelegator;
import org.orcid.api.notificationsV2.server.delegator.NotificationsApiServiceDelegator;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidNotificationAlreadyReadException;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.version.V3Convertible;
import org.orcid.core.version.V3VersionConverterChain;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.stereotype.Component;

/**
 * 
 * @author Will Simpson
 *
 */
@Component
public class NotificationsApiServiceVersionedDelegatorImpl implements NotificationsApiServiceDelegator<Object> {

    @Resource
    private NotificationsApiServiceDelegator<Object> notificationsApiServiceDelegator;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    private String externalVersion;

    @Resource
    private V3VersionConverterChain v2VersionConverterChain;
    
    @Resource
    private V3VersionConverterChain v2_1VersionConverterChain;

    public String getExternalVersion() {
        return externalVersion;
    }

    public void setExternalVersion(String externalVersion) {
        this.externalVersion = externalVersion;
    }
    
    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response findPermissionNotifications(String orcid) {        
        checkProfileStatus(orcid, true);
        Response response = notificationsApiServiceDelegator.findPermissionNotifications(orcid);
        if(externalVersion.equals("2.1")) {
            return upgradeResponse(response);
        } else {
            return downgradeResponse(response);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response findPermissionNotification(String orcid, Long id) {
        checkProfileStatus(orcid, true);
        Response response = downgradeResponse(notificationsApiServiceDelegator.findPermissionNotification(orcid, id));
        if(externalVersion.equals("2.1")) {
            return upgradeResponse(response);
        } else {
            return downgradeResponse(response);
        }
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response flagNotificationAsArchived(String orcid, Long id) throws OrcidNotificationAlreadyReadException {
        checkProfileStatus(orcid, false);
        return downgradeResponse(notificationsApiServiceDelegator.flagNotificationAsArchived(orcid, id));
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.PREMIUM_NOTIFICATION)
    public Response addPermissionNotification(UriInfo uriInfo, String orcid, Object notification) {
        checkProfileStatus(orcid, false);
        return notificationsApiServiceDelegator.addPermissionNotification(uriInfo, orcid, upgradeObject(notification));
    }

    private Response downgradeResponse(Response response) {
        Object entity = response.getEntity();
        V3Convertible result = null;
        if (entity != null) {
            result = v2VersionConverterChain.downgrade(new V3Convertible(entity, MemberV3ApiServiceDelegator.LATEST_V3_VERSION), externalVersion);
        }
        return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
    }
    
    private Response upgradeResponse(Response response) {
        Object entity = response.getEntity();
        V3Convertible result = null;
        if (entity != null) {
            result = v2_1VersionConverterChain.upgrade(new V3Convertible(entity, MemberV3ApiServiceDelegator.LATEST_V3_VERSION), externalVersion);
            return Response.fromResponse(response).entity(result.getObjectToConvert()).build();
        }
        return response;
    }

    private Object upgradeObject(Object entity) {
        V3Convertible result = null;
        if (entity != null) {
            result = v2VersionConverterChain.upgrade(new V3Convertible(entity, externalVersion), MemberV3ApiServiceDelegator.LATEST_V3_VERSION);
        }
        return result.getObjectToConvert();
    }
    
    private void checkProfileStatus(String orcid, boolean readOperation) {
        try {
            orcidSecurityManager.checkProfile(orcid);
        } catch (DeactivatedException e) {
            // If it is a read operation, ignore the deactivated status since we
            // are going to return the empty element with the deactivation date
            if (!readOperation) {
                throw e;
            }
        }
    }

    
}
