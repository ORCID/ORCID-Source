package org.orcid.core.version.impl;

import org.orcid.core.version.V2VersionConverter;

public class VersionConverterImplV2_0_rc1ToV2_0rc2 implements V2VersionConverter {

    private static final String FROM_VERSION = "2.0_rc1";
    private static final String TO_VERSION = "2.0_rc2";

    @Override
    public String getFromVersion() {
        return FROM_VERSION;
    }

    @Override
    public String getToVersion() {
        return TO_VERSION;
    }

    @Override
    public Object downgrade(Object objectToDowngrade) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object upgrade(Object objectToUpgrade) {
        // TODO Auto-generated method stub
        return null;
    }

}
