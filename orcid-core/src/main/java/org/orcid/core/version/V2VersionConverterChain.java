package org.orcid.core.version;

/**
 * 
 * @author Will Simpson
 *
 */
public interface V2VersionConverterChain {

    V2Convertible downgrade(V2Convertible objectToDowngrade, String requiredVersion);

    V2Convertible upgrade(V2Convertible objectToUpgrade, String requiredVersion);
}
