package org.orcid.core.version.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.orcid.core.version.V3Convertible;
import org.orcid.core.version.V3VersionConverter;
import org.orcid.core.version.V3VersionConverterChain;

/**
 * 
 * @author Will Simpson
 *
 */
public class V3VersionConverterChainImpl implements V3VersionConverterChain {

    protected List<V3VersionConverter> converters;
    protected List<V3VersionConverter> descendingConverters;
    protected ArrayList<String> versionIndex;

    public void setConverters(List<V3VersionConverter> converters) {
        this.converters = converters;
        this.descendingConverters = new ArrayList<>(converters);
        Collections.reverse(this.descendingConverters);
        versionIndex = new ArrayList<String>();
        for (int i = 0; i < converters.size(); i++) {
            if (i == 0) {
                versionIndex.add(converters.get(i).getLowerVersion());
            }
            versionIndex.add(converters.get(i).getUpperVersion());
        }
    }

    @Override
    public V3Convertible downgrade(V3Convertible objectToDowngrade, String requiredVersion) {
        for (V3VersionConverter converter : descendingConverters) {
            if (compareVersion(converter.getLowerVersion(), requiredVersion) > -1) {
                objectToDowngrade = converter.downgrade(objectToDowngrade);
            } else {
                return objectToDowngrade;
            }
        }
        return objectToDowngrade;
    }

    @Override
    public V3Convertible upgrade(V3Convertible objectToUpgrade, String requiredVersion) {
        String objectVersion = objectToUpgrade.getCurrentVersion();
        
        if(compareVersion(objectVersion, requiredVersion) < 0) {
            for (V3VersionConverter converter : converters) {  
                if(compareVersion(objectVersion, converter.getUpperVersion()) < 0) {
                    if (compareVersion(converter.getUpperVersion(), requiredVersion) < 1) {
                        objectToUpgrade = converter.upgrade(objectToUpgrade);
                    } else {
                        return objectToUpgrade;
                    }
                }
            }
        }        
                
        return objectToUpgrade;
    }

    protected int compareVersion(String v1, String v2) {
        if (versionIndex.indexOf(v1) < versionIndex.indexOf(v2)) {
            return -1;
        } else if (versionIndex.indexOf(v1) > versionIndex.indexOf(v2)) {
            return 1;
        }
        return 0;
    }

}
