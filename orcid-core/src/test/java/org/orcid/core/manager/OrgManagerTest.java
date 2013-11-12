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

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrgManagerTest extends BaseTest {

    @Resource
    private OrgManager orgManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/ProfileEntityData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Test
    public void getAmbiguousOrgs() {
        List<OrgEntity> orgs = orgManager.getAmbiguousOrgs();
        assertNotNull(orgs);
        assertEquals(2, orgs.size());
        for (OrgEntity org : orgs) {
            assertNull("Org should not contain disambiguated org: " + org.getName(), org.getOrgDisambiguated());
        }
    }

    @Test
    public void testWriteAmbiguousOrgs() throws IOException {
        StringWriter writer = new StringWriter();
        
        orgManager.writeAmbiguousOrgs(writer);
        String result = writer.toString();

        String expected = IOUtils.toString(getClass().getResource("expected_ambiguous_orgs.csv"));
        assertEquals(expected, result);
    }

    @Test
    public void testGetOrgs() {
        List<OrgEntity> orgs = orgManager.getOrgs("an", 0, 10);
        assertNotNull(orgs);
        assertEquals(2, orgs.size());
        assertEquals("An Institution", orgs.get(0).resolveName());
        assertEquals("Another Institution", orgs.get(1).resolveName());
    }

    @Test
    @Transactional
    public void testCreateUpdateWhenAlreadyExists() {
        OrgEntity inputOrg = new OrgEntity();
        inputOrg.setName("An institution");
        inputOrg.setCity("London");
        inputOrg.setCountry(Iso3166Country.GB);

        OrgEntity resultOrg = orgManager.createUpdate(inputOrg);

        assertNotNull(resultOrg);
        assertEquals(inputOrg.getName(), resultOrg.getName());
        assertEquals(inputOrg.getCity(), resultOrg.getCity());
        assertEquals(inputOrg.getRegion(), resultOrg.getRegion());
        assertEquals(inputOrg.getCountry(), resultOrg.getCountry());
        assertEquals(1, resultOrg.getId().longValue());
    }

    @Test
    @Transactional
    public void testCreateUpdateWhenDoesNotAlreadyExists() {
        OrgEntity inputOrg = new OrgEntity();
        inputOrg.setName("Le Institution");
        inputOrg.setCity("Paris");
        inputOrg.setCountry(Iso3166Country.FR);

        OrgEntity resultOrg = orgManager.createUpdate(inputOrg);

        assertNotNull(resultOrg);
        assertEquals(inputOrg.getName(), resultOrg.getName());
        assertEquals(inputOrg.getCity(), resultOrg.getCity());
        assertEquals(inputOrg.getRegion(), resultOrg.getRegion());
        assertEquals(inputOrg.getCountry(), resultOrg.getCountry());
        assertFalse(resultOrg.getId().equals(1));
    }

}
