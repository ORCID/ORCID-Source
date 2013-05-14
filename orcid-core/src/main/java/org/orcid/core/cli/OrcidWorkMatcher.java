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
package org.orcid.core.cli;

import org.orcid.jaxb.model.message.OrcidWork;

class OrcidWorkMatcher {

    private OrcidWork orcidWork;

    public OrcidWorkMatcher(OrcidWork orcidWork) {
        this.orcidWork = orcidWork;
    }

    private OrcidWork getOrcidWork() {
        return orcidWork;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrcidWorkMatcher)) {
            return false;
        }

        OrcidWork orcidWork = ((OrcidWorkMatcher) o).getOrcidWork();

        if (this.orcidWork.getPublicationDate() != null ? !this.orcidWork.getPublicationDate().equals(orcidWork.getPublicationDate())
                : orcidWork.getPublicationDate() != null) {
            return false;
        }

        if (this.orcidWork.getShortDescription() != null ? !this.orcidWork.getShortDescription().equals(orcidWork.getShortDescription()) : orcidWork
                .getShortDescription() != null) {
            return false;
        }
        if (this.orcidWork.getUrl() != null ? !this.orcidWork.getUrl().equals(orcidWork.getUrl()) : orcidWork.getUrl() != null) {
            return false;
        }

        if (this.orcidWork.getWorkCitation() != null ? !this.orcidWork.getWorkCitation().equals(orcidWork.getWorkCitation()) : orcidWork.getWorkCitation() != null) {
            return false;
        }
        if (this.orcidWork.getWorkContributors() != null ? !this.orcidWork.getWorkContributors().equals(orcidWork.getWorkContributors()) : orcidWork
                .getWorkContributors() != null) {
            return false;
        }
        if (this.orcidWork.getWorkExternalIdentifiers() != null ? !this.orcidWork.getWorkExternalIdentifiers().equals(orcidWork.getWorkExternalIdentifiers()) : orcidWork
                .getWorkExternalIdentifiers() != null) {
            return false;
        }
        if (this.orcidWork.getWorkSource() != null ? !this.orcidWork.getWorkSource().equals(orcidWork.getWorkSource()) : orcidWork.getWorkSource() != null) {
            return false;
        }
        if (this.orcidWork.getWorkTitle() != null ? !this.orcidWork.getWorkTitle().equals(orcidWork.getWorkTitle()) : orcidWork.getWorkTitle() != null) {
            return false;
        }
        if (this.orcidWork.getWorkType() != orcidWork.getWorkType()) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = this.orcidWork.getWorkTitle() != null ? this.orcidWork.getWorkTitle().hashCode() : 0;
        result = 31 * result + (this.orcidWork.getShortDescription() != null ? this.orcidWork.getShortDescription().hashCode() : 0);
        result = 31 * result + (this.orcidWork.getWorkCitation() != null ? this.orcidWork.getWorkCitation().hashCode() : 0);
        result = 31 * result + (this.orcidWork.getWorkType() != null ? this.orcidWork.getWorkType().hashCode() : 0);
        result = 31 * result + (this.orcidWork.getPublicationDate() != null ? this.orcidWork.getPublicationDate().hashCode() : 0);
        result = 31 * result + (this.orcidWork.getWorkExternalIdentifiers() != null ? this.orcidWork.getWorkExternalIdentifiers().hashCode() : 0);
        result = 31 * result + (this.orcidWork.getUrl() != null ? this.orcidWork.getUrl().hashCode() : 0);
        result = 31 * result + (this.orcidWork.getWorkContributors() != null ? this.orcidWork.getWorkContributors().hashCode() : 0);
        result = 31 * result + (this.orcidWork.getWorkSource() != null ? this.orcidWork.getWorkSource().hashCode() : 0);
        return result;
    }

}
