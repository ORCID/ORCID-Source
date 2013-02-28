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

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class SecurityQuestionDaoTest extends DBUnitTest {

    @Resource(name = "securityQuestionDao")
    private GenericDao<SecurityQuestionEntity, Integer> securityQuestionDao;

    @Test
    @Rollback(true)
    public void testMergeFindAndRemove() {
        SecurityQuestionEntity question = new SecurityQuestionEntity();
        question.setQuestion("What is your pet's name?");
        securityQuestionDao.merge(question);
        assertNotNull(question.getId());
        SecurityQuestionEntity retrieved = securityQuestionDao.find(question.getId());
        assertNotNull(retrieved);
        assertEquals("What is your pet's name?", retrieved.getQuestion());
        securityQuestionDao.remove(question.getId());
        retrieved = securityQuestionDao.find(question.getId());
        assertNull(retrieved);
    }

}
