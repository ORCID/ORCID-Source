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
package org.orcid.persistence.solr.entities;

import java.util.Collection;

public class OrcidSolrResult {

    private String orcid;
    private float relevancyScore;
    private String email;
    private String givenNames;
    private String familyName;
    private Collection<String> currentPrimaryInstitutionAffiliationNames;
    private Collection<String> currentInstitutionAffiliationNames;
    private Collection<String> pastInstitutionAffiliationNames;
    private String creditName;
    private Collection<String> otherNames;
    private String publicProfileMessage;

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public float getRelevancyScore() {
        return relevancyScore;
    }

    public void setRelevancyScore(float relevancyScore) {
        this.relevancyScore = relevancyScore;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGivenNames() {
        return givenNames;
    }

    public void setGivenNames(String givenNames) {
        this.givenNames = givenNames;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Collection<String> getCurrentPrimaryInstitutionAffiliationNames() {
        return currentPrimaryInstitutionAffiliationNames;
    }

    public void setCurrentPrimaryInstitutionAffiliationNames(Collection<String> currentPrimaryInstitutionAffiliationNames) {
        this.currentPrimaryInstitutionAffiliationNames = currentPrimaryInstitutionAffiliationNames;
    }

    public Collection<String> getCurrentInstitutionAffiliationNames() {
        return currentInstitutionAffiliationNames;
    }

    public void setCurrentInstitutionAffiliationNames(Collection<String> currentInstitutionAffiliationNames) {
        this.currentInstitutionAffiliationNames = currentInstitutionAffiliationNames;
    }

    public Collection<String> getPastInstitutionAffiliationNames() {
        return pastInstitutionAffiliationNames;
    }

    public void setPastInstitutionAffiliationNames(Collection<String> pastInstitutionAffiliationNames) {
        this.pastInstitutionAffiliationNames = pastInstitutionAffiliationNames;
    }

    public String getCreditName() {
        return creditName;
    }

    public void setCreditName(String creditName) {
        this.creditName = creditName;
    }

    public Collection<String> getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(Collection<String> otherNames) {
        this.otherNames = otherNames;
    }

    public String getPublicProfileMessage() {
        return publicProfileMessage;
    }

    public void setPublicProfileMessage(String publicProfileMessage) {
        this.publicProfileMessage = publicProfileMessage;
    }

}
