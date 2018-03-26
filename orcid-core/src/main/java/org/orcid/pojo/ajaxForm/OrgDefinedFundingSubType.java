package org.orcid.pojo.ajaxForm;

import java.io.Serializable;

public class OrgDefinedFundingSubType implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
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
