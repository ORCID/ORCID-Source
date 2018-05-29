package org.orcid.core.version;

import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface OrcidMessageVersionConverterChain {

    OrcidMessage downgradeMessage(OrcidMessage orcidMessage, String requiredVersion);

    OrcidMessage upgradeMessage(OrcidMessage orcidMessage, String requiredVersion);

    public int compareVersion(String v1, String v2);
}
