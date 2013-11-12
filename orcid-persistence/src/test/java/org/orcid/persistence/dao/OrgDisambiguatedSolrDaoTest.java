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
package org.orcid.persistence.dao;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.solr.entities.OrgDisambiguatedSolrDocument;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
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
