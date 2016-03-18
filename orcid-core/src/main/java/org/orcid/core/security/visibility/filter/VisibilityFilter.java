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
package org.orcid.core.security.visibility.filter;

import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.message.Visibility;

/**
 * @author Declan Newman (declan) Date: 16/03/2012
 */
public interface VisibilityFilter {

    OrcidMessage filter(OrcidMessage messageToBeFiltered, Visibility... visibilities);

    OrcidMessage filter(OrcidMessage messageToBeFiltered, String sourceId, boolean allowPrivateWorks, boolean allowPrivateFunding, boolean allowPrivateAffiliations, Visibility... visibilities);
    
}
