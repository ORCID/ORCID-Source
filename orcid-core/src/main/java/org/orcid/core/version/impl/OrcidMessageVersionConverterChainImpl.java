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
    public ArrayList<String> versionIndex;
    public List<OrcidMessageVersionConverter> descendingConverters;

    public void setConverters(List<OrcidMessageVersionConverter> converters) {
        this.converters = converters;
        versionIndex = new ArrayList<String>();
        for (int i = 0; i < converters.size(); i ++) {
            if (i == 0)
                versionIndex.add(converters.get(i).getFromVersion());
            versionIndex.add(converters.get(i).getToVersion());
        }
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
                break;
            }
            String fromVersion = converter.getFromVersion();
            if (compareVersion(fromVersion,oldVersion) < 0 && compareVersion(fromVersion, requiredVersion) >= 0) {
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
                break;
            }
            String toVersion = converter.getToVersion();
            if (compareVersion(toVersion, oldVersion) > 0 &&  compareVersion(toVersion,requiredVersion) <= 0) {
                orcidMessage = converter.upgradeMessage(orcidMessage);
            }
        }

        return orcidMessage;
    }
    
    public int compareVersion(String v1, String v2) {
        if (versionIndex.indexOf(v1) < versionIndex.indexOf(v2)) 
            return -1;
        else if (versionIndex.indexOf(v1) > versionIndex.indexOf(v2))
            return 1;
        return 0;
    }
}
