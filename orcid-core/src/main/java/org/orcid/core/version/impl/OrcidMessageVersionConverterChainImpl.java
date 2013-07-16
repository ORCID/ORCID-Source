/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import org.orcid.core.version.OrcidMessageVersionConverter;
import org.orcid.core.version.OrcidMessageVersionConverterChain;
import org.orcid.jaxb.model.message.OrcidMessage;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidMessageVersionConverterChainImpl implements OrcidMessageVersionConverterChain {

    public List<OrcidMessageVersionConverter> converters;
    public List<OrcidMessageVersionConverter> descendingConverters;

    public void setConverters(List<OrcidMessageVersionConverter> converters) {
        this.converters = converters;
        List<OrcidMessageVersionConverter> descendingConverters = new ArrayList<>(converters);
        Collections.reverse(descendingConverters);
        this.descendingConverters = descendingConverters;
    }

    @Override
    public OrcidMessage downgradeMessage(OrcidMessage orcidMessage, String requiredVersion) {
        if (orcidMessage == null) {
            return null;
        }
        for (OrcidMessageVersionConverter converter : descendingConverters) {
            String oldVersion = orcidMessage.getMessageVersion();
            if (requiredVersion.equals(oldVersion)) {
                return orcidMessage;
            }
            if (converter.getFromVersion().compareTo(requiredVersion) <= 0) {
                orcidMessage = converter.downgradeMessage(orcidMessage);
            }
        }
        return orcidMessage;
    }

    @Override
    public OrcidMessage upgradeMessage(OrcidMessage orcidMessage, String requiredVersion) {
        if (orcidMessage == null) {
            return null;
        }
        for (OrcidMessageVersionConverter converter : converters) {
            String oldVersion = orcidMessage.getMessageVersion();
            if (requiredVersion.equals(oldVersion)) {
                return orcidMessage;
            }
            if (converter.getToVersion().compareTo(requiredVersion) >= 0) {
                orcidMessage = converter.upgradeMessage(orcidMessage);
            }
        }
        return orcidMessage;
    }

}
