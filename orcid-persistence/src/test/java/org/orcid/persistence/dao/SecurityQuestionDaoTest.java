package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.SecurityQuestionEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
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
