package org.orcid.core.version;

/**
 * 
 * @author Will Simpson
 *
 */
public interface V3VersionConverterChain {

    V3Convertible downgrade(V3Convertible objectToDowngrade, String requiredVersion);

    V3Convertible upgrade(V3Convertible objectToUpgrade, String requiredVersion);
}
