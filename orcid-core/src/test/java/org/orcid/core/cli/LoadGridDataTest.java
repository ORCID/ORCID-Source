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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.persistence.dao.OrgDisambiguatedDao;
import org.orcid.persistence.dao.OrgDisambiguatedExternalIdentifierDao;

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
    public void execute_Stats_Test_1() throws URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader()
                .getResource("grid/grid_1_orgs_4_external_identifiers.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();
        assertEquals(1L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(4L, loadGridData.getAddedExternalIdentifiers());                        
    }

    @Test
    public void execute_Stats_Test_2() throws URISyntaxException {
        Path path = Paths.get(getClass().getClassLoader()
                .getResource("grid/grid_4_orgs_24_external_identifiers.json").toURI());
        File testFile = path.toFile();
        loadGridData.setFileToLoad(testFile);
        loadGridData.execute();
        assertEquals(4L, loadGridData.getAddedDisambiguatedOrgs());
        assertEquals(24L, loadGridData.getAddedExternalIdentifiers());
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
