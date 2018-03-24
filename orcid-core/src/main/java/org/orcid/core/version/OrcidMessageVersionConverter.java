package org.orcid.core.version;

import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrcidMessageVersionConverter {

    String getFromVersion();

    String getToVersion();

    OrcidMessage downgradeMessage(OrcidMessage orcidMessage);

    OrcidMessage upgradeMessage(OrcidMessage orcidMessage);

}
