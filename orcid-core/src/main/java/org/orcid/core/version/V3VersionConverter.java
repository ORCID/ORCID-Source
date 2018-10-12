package org.orcid.core.version;


/**
 * 
 * @author Will Simpson
 * 
 */
public interface V3VersionConverter {

    String getLowerVersion();

    String getUpperVersion();

    V3Convertible downgrade(V3Convertible objectToDowngrade);

    V3Convertible upgrade(V3Convertible objectToUpgrade);

}
