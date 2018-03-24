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
