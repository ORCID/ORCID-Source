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
package org.orcid.core.manager;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.Subtitle;
import org.orcid.jaxb.model.message.Title;
import org.orcid.jaxb.model.message.WorkExternalIdentifier;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkExternalIdentifiers;
import org.orcid.jaxb.model.message.WorkTitle;

public class OrcidProfileCleanerTest extends BaseTest {

    @Resource
    OrcidProfileCleaner orcidProfileCleaner;

    @Test
    public void testSimpleClean() {
        OrcidWork orcidWork = new OrcidWork();
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("Test Title"));
        workTitle.setSubtitle(new Subtitle(""));
        orcidWork.setWorkTitle(workTitle);
        WorkExternalIdentifiers workExternalIdentifiers = new WorkExternalIdentifiers();
        orcidWork.setWorkExternalIdentifiers(workExternalIdentifiers);
        WorkExternalIdentifier workExternalIdentifier1 = new WorkExternalIdentifier();
        workExternalIdentifiers.getWorkExternalIdentifier().add(workExternalIdentifier1);
        workExternalIdentifier1.setWorkExternalIdentifierType(WorkExternalIdentifierType.DOI);
        workExternalIdentifier1.setWorkExternalIdentifierId(new WorkExternalIdentifierId("work-doi"));

        orcidProfileCleaner.clean(orcidWork);

        assertEquals("Test Title", orcidWork.getWorkTitle().getTitle().getContent());
        assertEquals("work-doi", orcidWork.getWorkExternalIdentifiers().getWorkExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        assertNull("Subtitle should be null", orcidWork.getWorkTitle().getSubtitle());
    }

}
