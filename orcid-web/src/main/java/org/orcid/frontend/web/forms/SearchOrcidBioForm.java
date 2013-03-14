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

import org.orcid.frontend.web.forms.validate.ValidOrcidBioSearchAnnotation;

/**
 * Form representing searching on an orcid by Bio details.
 * 
 * @author jamesb
 * 
 */
@ValidOrcidBioSearchAnnotation(message = "Please enter an ORCID ID to search for or alternatively any of the name fields - at least one of given name, family name, or institution name must have 2 characters")
public class SearchOrcidBioForm {

    private String orcid;
    private String familyName;
    private String givenName;
    private String institutionName;
    private boolean otherNamesSearchable;
    private boolean pastInstitutionsSearchable;
    private String keyword;
    private String text;

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public boolean isOtherNamesSearchable() {
        return otherNamesSearchable;
    }

    public void setOtherNamesSearchable(boolean otherNamesSearchable) {
        this.otherNamesSearchable = otherNamesSearchable;
    }

    public boolean isPastInstitutionsSearchable() {
        return pastInstitutionsSearchable;
    }

    public void setPastInstitutionsSearchable(boolean pastInstitutionsSearchable) {
        this.pastInstitutionsSearchable = pastInstitutionsSearchable;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
