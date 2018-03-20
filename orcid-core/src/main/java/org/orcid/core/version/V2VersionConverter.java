package org.orcid.core.version;


/**
 * 
 * @author Will Simpson
 * 
 */
public interface V2VersionConverter {

    String getLowerVersion();

    String getUpperVersion();

    V2Convertible downgrade(V2Convertible objectToDowngrade);

    V2Convertible upgrade(V2Convertible objectToUpgrade);

}
