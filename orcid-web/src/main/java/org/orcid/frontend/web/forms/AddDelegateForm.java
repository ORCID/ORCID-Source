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
package org.orcid.frontend.web.forms;

import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.GivenPermissionTo;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidIdentifier;
import org.orcid.jaxb.model.message.OrcidProfile;

/**
 * 
 * @author Will Simpson
 * 
 */
public class AddDelegateForm {

    private String delegateOrcid;

    public String getDelegateOrcid() {
        return delegateOrcid;
    }

    public void setDelegateOrcid(String delegateOrcid) {
        this.delegateOrcid = delegateOrcid;
    }

    public OrcidProfile getOrcidProfile(String orcid) {
        OrcidProfile orcidProfile = new OrcidProfile();
        orcidProfile.setOrcidIdentifier(orcid);
        OrcidBio orcidBio = new OrcidBio();
        orcidProfile.setOrcidBio(orcidBio);
        Delegation delegation = new Delegation();
        orcidBio.setDelegation(delegation);
        GivenPermissionTo givenPermissionTo = new GivenPermissionTo();
        delegation.setGivenPermissionTo(givenPermissionTo);
        DelegationDetails delegationDetails = new DelegationDetails();
        givenPermissionTo.getDelegationDetails().add(delegationDetails);
        DelegateSummary delegateSummary = new DelegateSummary(new OrcidIdentifier(delegateOrcid));
        delegationDetails.setDelegateSummary(delegateSummary);
        return orcidProfile;
    }

}
