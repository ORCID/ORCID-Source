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

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.ExternalIdOrcid;
import org.orcid.jaxb.model.message.ExternalIdReference;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Visibility;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

public class CurrentAffiliationsForm {

    private String orcid;

    private PrimaryInstitutionForm primaryInstitutionForm;

    private JointAffiliationForm jointAffiliationForm;

    private PastInstitutionsForm pastInstitutionsForm;

    private List<SponsorIdPair> sponsorIds = new ArrayList<SponsorIdPair>();

    private boolean isExternalIdentifiersPublic;

    private boolean pastAffiliationsPublic;

    public CurrentAffiliationsForm() {
        super();
        primaryInstitutionForm = new PrimaryInstitutionForm();
        jointAffiliationForm = new JointAffiliationForm();
        pastInstitutionsForm = new PastInstitutionsForm();
    }

    public CurrentAffiliationsForm(OrcidProfile orcidProfile) {
        OrcidBio orcidBio = orcidProfile.getOrcidBio();
        orcid = orcidProfile.getOrcid().getValue();
        primaryInstitutionForm = new PrimaryInstitutionForm(orcidProfile);
        jointAffiliationForm = new JointAffiliationForm(orcidProfile);
        pastInstitutionsForm = new PastInstitutionsForm(orcidProfile);

        Visibility visibility = (orcidBio != null && orcidBio.getExternalIdentifiers() != null) ?
                orcidBio.getExternalIdentifiers().getVisibility() :
                OrcidVisibilityDefaults.EXTERNAL_IDENTIFIER_DEFAULT.getVisibility();
        setExternalIdentifiersPublic(Visibility.PUBLIC.equals(visibility));
    }

    public PastInstitutionsForm getPastInstitutionsForm() {
        return pastInstitutionsForm;
    }

    public OrcidProfile getOrcidProfile() {
        OrcidProfile profile = new OrcidProfile();
        profile.setOrcid(orcid);
        OrcidBio bio = new OrcidBio();
        profile.setOrcidBio(bio);
        Affiliation primaryInstitution = primaryInstitutionForm.getPrimaryInstitution();
        if (primaryInstitution != null) {
            bio.getAffiliations().add(primaryInstitution) ;
        }
        List<Affiliation> affiliateInstitutions = jointAffiliationForm.getAffiliateInstitutions();
        if (affiliateInstitutions != null && !affiliateInstitutions.isEmpty()) {
            bio.getAffiliations().addAll(affiliateInstitutions);
        }
        List<Affiliation> affiliationsFromSelected = pastInstitutionsForm.getAffiliationsFromSelected();
        if (affiliationsFromSelected != null && !affiliationsFromSelected.isEmpty()) {
            bio.getAffiliations().addAll(affiliationsFromSelected);
        }

        ExternalIdentifiers externalIdentifiers = new ExternalIdentifiers();
        externalIdentifiers.setVisibility(isExternalIdentifiersPublic() ? Visibility.PUBLIC : Visibility.LIMITED);

        for (SponsorIdPair pair : sponsorIds) {
            if (!"".equals(pair.getExternalId())) {
                externalIdentifiers.getExternalIdentifier().add(
                        new ExternalIdentifier(new ExternalIdOrcid(pair.getSponsorOrcid()), new ExternalIdReference(pair.getExternalId())));
            }
        }

        bio.setExternalIdentifiers(externalIdentifiers);
        return profile;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    @Valid
    public PrimaryInstitutionForm getPrimaryInstitutionForm() {
        return primaryInstitutionForm;
    }

    public void setPrimaryInstitutionForm(PrimaryInstitutionForm primaryInstitutionForm) {
        this.primaryInstitutionForm = primaryInstitutionForm;
    }

    @Valid
    public JointAffiliationForm getJointAffiliationForm() {
        return jointAffiliationForm;
    }

    public void setJointAffiliationForm(JointAffiliationForm jointAffiliationForm) {
        this.jointAffiliationForm = jointAffiliationForm;
    }

    public List<SponsorIdPair> getSponsorIds() {
        return sponsorIds;
    }

    public void setSponsorIds(List<SponsorIdPair> sponsorIds) {
        this.sponsorIds = sponsorIds;
    }

    public boolean isExternalIdentifiersPublic() {
        return isExternalIdentifiersPublic;
    }

    public void setExternalIdentifiersPublic(boolean externalIdentifiersPublic) {
        isExternalIdentifiersPublic = externalIdentifiersPublic;
    }

    public boolean isPastAffiliationsPublic() {
        return pastAffiliationsPublic;
    }

    public void setPastAffiliationsPublic(boolean pastAffiliationsPublic) {
        this.pastAffiliationsPublic = pastAffiliationsPublic;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
