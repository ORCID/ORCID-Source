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
package org.orcid.pojo.ajaxForm;

public class OrgDefinedFundingSubType {
    private Text subtype;
    private boolean alreadyIndexed;
    
    public Text getSubtype() {
        return subtype;
    }

    public void setSubtype(Text subtype) {
        this.subtype = subtype;
    }

    public boolean isAlreadyIndexed() {
        return alreadyIndexed;
    }

    public void setAlreadyIndexed(boolean alreadyIndexed) {
        this.alreadyIndexed = alreadyIndexed;
    }
}
