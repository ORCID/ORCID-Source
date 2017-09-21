/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.persistence.constants.OrganizationStatus;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedEntity;
import org.orcid.persistence.jpa.entities.OrgDisambiguatedExternalIdentifierEntity;

public class LoadGridDataTest {

    @Mock
    private OrgDisambiguatedExternalIdentifierDao orgDisambiguatedExternalIdentifierDao;
    @Mock
    private OrgDisambiguatedDao orgDisambiguatedDao;

    private LoadGridData loadGridData = new LoadGridData();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        loadGridData.setOrgDisambiguatedDao(orgDisambiguatedDao);
        loadGridData.setOrgDisambiguatedExternalIdentifierDao(orgDisambiguatedExternalIdentifierDao);
    }

    @Test
    public void execute_Create3Institutes_Test() {
        fail();
    }

    @Test
    public void execute_Create0Institutes_Test() {
        fail();
    }

    @Test
    public void execute_Update3Institutes_Test() {
        fail();
    }

    @Test
    public void execute_Update0Institutes_Test() {
        fail();
    }

    @Test
    public void execute_Update1Institutes_Test() {
        fail();
    }

    @Test
    public void execute_Deprecated1Institutes_Test() {
        fail();
    }

    @Test
    public void execute_Obsolete1Institutes_Test() {
        fail();
    }

    @Test
    public void execute_Create1ExternalIdentifier_Test() {
        fail();
    }

    @Test
    public void execute_Create1Insitute_Update1Institute_Deprecate1Insitute_Obsolete1Insitute_Create1ExternalIdentifier_Test() {
        fail();
    }    
}
