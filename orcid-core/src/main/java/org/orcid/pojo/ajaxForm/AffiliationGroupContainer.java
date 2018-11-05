package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.jaxb.model.v3.rc2.record.AffiliationType;

public class AffiliationGroupContainer implements Serializable {
    private static final long serialVersionUID = 364517151433889152L;
    Map<AffiliationType, List<AffiliationGroupForm>> affiliationGroups = new HashMap<AffiliationType, List<AffiliationGroupForm>>();

    public Map<AffiliationType, List<AffiliationGroupForm>> getAffiliationGroups() {
        return affiliationGroups;
    }

    public void setAffiliationGroups(Map<AffiliationType, List<AffiliationGroupForm>> affiliationGroups) {
        this.affiliationGroups = affiliationGroups;
    }
}
