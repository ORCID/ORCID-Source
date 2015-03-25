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
package org.orcid.persistence.dao;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.DBUnitTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class OrgDisambiguatedDaoTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/OrgsEntityData.xml", "/data/ProfileEntityData.xml", "/data/OrgAffiliationEntityData.xml"); 
    
    @Resource
    private OrgDisambiguatedDao orgDisambiguatedDao;

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
    public void testFindDisambuguatedOrgsWithIncorrectPopularity() {
        List<Pair<Long, Integer>> results = orgDisambiguatedDao.findDisambuguatedOrgsWithIncorrectPopularity(10);
        assertNotNull(results);
        assertEquals(1, results.size());
        Pair<Long, Integer> pair = results.get(0);
        assertEquals(1, pair.getLeft().longValue());
        assertEquals(2, pair.getRight().intValue());
    }

}
