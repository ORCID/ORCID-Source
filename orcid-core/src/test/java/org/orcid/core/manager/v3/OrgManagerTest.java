package org.orcid.core.manager.v3;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.message.Iso3166Country;
import org.orcid.persistence.jpa.entities.AmbiguousOrgEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrgManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/OrgsEntityData.xml");

    @Resource(name = "orgManagerV3")
    private OrgManager orgManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test
    public void getAmbiguousOrgs() {
        List<AmbiguousOrgEntity> orgs = orgManager.getAmbiguousOrgs(0, Integer.MAX_VALUE);
        assertNotNull(orgs);
        assertEquals(2, orgs.size());
    }

    @Test
    public void testWriteAmbiguousOrgs() throws IOException {
        StringWriter writer = new StringWriter();

        orgManager.writeAmbiguousOrgs(writer);
        String result = writer.toString();

        String expected = IOUtils.toString(getClass().getResource("/org/orcid/core/manager/expected_ambiguous_orgs.csv"));
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

}
