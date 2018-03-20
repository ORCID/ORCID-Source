package org.orcid.core.version.impl;

/**
 * 
 * @author Will Simpson
 *
 */
public class V2_1VersionConverterChainImpl extends V2VersionConverterChainImpl {

    protected int compareVersion(String v1, String v2) {
        if (versionIndex.indexOf(v1) < versionIndex.indexOf(v2)) {
            return -1;
        } else if (versionIndex.indexOf(v1) > versionIndex.indexOf(v2)) {
            return 1;
        }
        return 0;
    }

}
