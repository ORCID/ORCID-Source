package org.orcid.core.version;

/**
 * 
 * @author Will Simpson
 *
 */
public interface V2VersionObjectFactory {

    Object createEquivalentInstance(Object originalObject, String requiredVersion);

}
