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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.core.version.V2VersionConverterChain;

/**
 * 
 * @author Will Simpson
 *
 */
public class V2VersionConverterChainImpl implements V2VersionConverterChain {

    private List<V2VersionConverter> converters;
    private List<V2VersionConverter> reversedConverters;

    public void setConverters(List<V2VersionConverter> converters) {
        this.converters = converters;
        this.reversedConverters = new ArrayList<>(converters);
        Collections.reverse(this.reversedConverters);
    }

    @Override
    public V2Convertible downgrade(V2Convertible objectToDowngrade, String requiredVersion) {
        for (V2VersionConverter converter : reversedConverters) {
            if (converter.getLowerVersion().compareTo(requiredVersion) > -1) {
                objectToDowngrade = converter.downgrade(null, objectToDowngrade);
            } else {
                return objectToDowngrade;
            }
        }
        return objectToDowngrade;
    }

    @Override
    public V2Convertible uprade(V2Convertible objectToUpgrade, String requiredVersion) {
        for (V2VersionConverter converter : converters) {
            if (converter.getUpperVersion().compareTo(requiredVersion) < 1) {
                objectToUpgrade = converter.upgrade(null, objectToUpgrade);
            } else {
                return objectToUpgrade;
            }
        }
        return objectToUpgrade;
    }

}
