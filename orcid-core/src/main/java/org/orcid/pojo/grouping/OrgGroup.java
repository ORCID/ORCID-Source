package org.orcid.pojo.grouping;

import java.util.HashMap;
import java.util.Set;

import org.orcid.pojo.OrgDisambiguated;


public class OrgGroup {

    private static final long serialVersionUID = 1L;
    
    private OrgDisambiguated sourceOrg;
    private HashMap<String, OrgDisambiguated> orgs = new HashMap<String, OrgDisambiguated>();
    private HashMap<String, OrgDisambiguated> externalIds = new HashMap<String, OrgDisambiguated>();
    private boolean isFunding = false;
    
    private OrgDisambiguated rorOrg = null;
    
    public HashMap<String, OrgDisambiguated> getOrgs() {
        return orgs;
    }
    
    public OrgDisambiguated getSourceOrg() {
        return sourceOrg;
    }
    
    public void setOrgs(HashMap<String, OrgDisambiguated> orgs) {
        this.orgs = orgs;
    }
    
    public void setSourceOrg(OrgDisambiguated sourceOrg) {
        this.sourceOrg = sourceOrg;
    }

    public OrgDisambiguated getRorOrg() {
        return rorOrg;
    }

    public void setRorOrg(OrgDisambiguated rorOrg) {
        this.rorOrg = rorOrg;
    }

    public boolean isFunding() {
        return isFunding;
    }

    public void setFunding(boolean isFunding) {
        this.isFunding = isFunding;
    }
}
