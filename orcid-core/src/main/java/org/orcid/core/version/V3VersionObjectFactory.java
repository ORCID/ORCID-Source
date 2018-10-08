package org.orcid.core.version;

/**
 * 
 * @author Will Simpson
 *
 */
public interface V3VersionObjectFactory {

    Object createEquivalentInstance(Object originalObject, String requiredVersion);

}
