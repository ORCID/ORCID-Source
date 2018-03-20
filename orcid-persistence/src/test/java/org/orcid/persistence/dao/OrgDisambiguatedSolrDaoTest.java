package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
public class OrgDisambiguatedSolrDaoTest {

    @Resource
    private OrgDisambiguatedSolrDao orgDisambiguatedSolrDao;

    @Test
    public void testPesistAndFindById() {
        OrgDisambiguatedSolrDocument doc = new OrgDisambiguatedSolrDocument();
        doc.setOrgDisambiguatedId(1L);
        doc.setOrgDisambiguatedName("Test org name");
        doc.setOrgDisambiguatedCity("Haywards Heath");
        doc.setOrgDisambiguatedRegion("West Sussex");
        doc.setOrgDisambiguatedCountry("GB");
        List<String> orgNames = new ArrayList<>();
        orgNames.add("Test org name");
        orgNames.add("Test organisation name");
        doc.setOrgNames(orgNames);

        orgDisambiguatedSolrDao.persist(doc);

        OrgDisambiguatedSolrDocument result = orgDisambiguatedSolrDao.findById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getOrgDisambiguatedId().longValue());
        assertEquals("Test org name", result.getOrgDisambiguatedName());
    }

}
