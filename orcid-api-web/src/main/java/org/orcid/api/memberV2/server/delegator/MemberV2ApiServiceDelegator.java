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
package org.orcid.api.memberV2.server.delegator;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
import org.orcid.jaxb.model.record.Work;

/**
 * 
 * @author Will Simpson
 *
 */
public interface MemberV2ApiServiceDelegator {

    Response viewStatusText();

    Response viewActivities(String orcid);
    
    Response viewWork(String orcid, String putCode);
    
    Response createWork(String orcid, Work work);
    
    Response updateWork(String orcid, Work work);
    
    Response deleteWork(String orcid, String putCode);

}
