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

import static org.junit.Assert.*;

import org.junit.Test;
import org.orcid.jaxb.model.message.Contributor;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;

public class PublicationTest {

    @Test
    public void testGetOrcidWork() {
        String doi = "10.1029\\/2002JD002436";
        String title = "5 on chemistry and nitrate aerosol formation in the lower troposphere under photosmog conditions";
        String fullCitation = "Riemer, N, 2003, '5 on chemistry and nitrate aerosol formation in the lower troposphere under photosmog conditions', <i>Journal of Geophysical Research<\\/i>, vol. 30, no. D4, p. 1255.";
        String coins = "ctx_ver=Z39.88-2004&rft_val_fmt=info:ofi\\/fmt:kev:mtx:journal&rft_id=info:doi\\/10.1029\\/2002JD002436&rtf.genre=journal-article&rtf.spage=1255&rtf.date=2003&rtf.aulast=Riemer&rtf.aufirst=N.&rtf.auinit=N&rtf.atitle=5 on chemistry and nitrate aerosol formation in the lower troposphere under photosmog conditions&rtf.jtitle=Journal of Geophysical Research&rtf.volume=30&rtf.issue=D4";

        Publication publication = new Publication();
        publication.setDoi(doi);
        publication.setTitle(title);
        publication.setFullCitation(fullCitation);
        publication.setCoins(coins);

        OrcidWork orcidWork = publication.getOrcidWork();
        assertNotNull(orcidWork);
        assertEquals(doi, orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        assertEquals(WorkExternalIdentifierType.DOI, orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier().get(0).getWorkExternalIdentifierType());
        assertEquals(title, orcidWork.getWorkTitle().getTitle().getContent());
        assertEquals(fullCitation, orcidWork.getWorkCitation().getCitation());
        PublicationDate publicationDate = orcidWork.getPublicationDate();
        assertNotNull(publicationDate);
        assertEquals(null, publicationDate.getDay());
        assertEquals(null, publicationDate.getMonth());
        assertEquals("2003", publicationDate.getYear().getValue());
        assertEquals(1, orcidWork.getWorkContributors().getContributor().size());
        Contributor contributor = orcidWork.getWorkContributors().getContributor().get(0);
        assertEquals("Riemer N.", contributor.getCreditName().getContent());
        assertEquals(ContributorRole.AUTHOR, contributor.getContributorAttributes().getContributorRole());
        assertEquals(SequenceType.FIRST, contributor.getContributorAttributes().getContributorSequence());
    }

}
