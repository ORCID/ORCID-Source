/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
