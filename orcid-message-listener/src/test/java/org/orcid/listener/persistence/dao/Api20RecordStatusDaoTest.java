package org.orcid.listener.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.listener.persistence.entities.Api20RecordStatusEntity;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class Api20RecordStatusDaoTest {
    private static final String ORCID = "0000-0000-0000-0000";
    
    @Resource
    private Api20RecordStatusDao dao;
    
    @PersistenceContext
    protected EntityManager entityManager;

    @Test
    @Transactional
    public void createTest() {
        assertFalse(dao.exists(ORCID));
        dao.create(ORCID, true, Arrays.asList());
        assertTrue(dao.exists(ORCID));
        entityManager.remove(dao.get(ORCID));
    }
    
    @Test
    @Transactional
    public void getTest() {
        dao.create(ORCID, true, Arrays.asList());
        Api20RecordStatusEntity e = dao.get(ORCID);
        assertNotNull(e);
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getSummaryLastIndexed());
        assertEquals(Integer.valueOf(0), e.getSummaryStatus());
        assertEquals(Integer.valueOf(0), e.getEducationsStatus());
        assertEquals(Integer.valueOf(0), e.getEmploymentsStatus());
        assertEquals(Integer.valueOf(0), e.getFundingsStatus());
        assertEquals(Integer.valueOf(0), e.getPeerReviewsStatus());
        assertEquals(Integer.valueOf(0), e.getSummaryStatus());
        assertEquals(Integer.valueOf(0), e.getWorksStatus());
        
        assertNotNull(e.getEducationsLastIndexed());
        assertNotNull(e.getEmploymentsLastIndexed());
        assertNotNull(e.getFundingsLastIndexed());
        assertNotNull(e.getPeerReviewsLastIndexed());
        assertNotNull(e.getWorksLastIndexed()); 
        entityManager.remove(dao.get(ORCID));
    }
    
    @Test
    @Transactional
    public void updateTest() {
        dao.create(ORCID, true, Arrays.asList());
        Api20RecordStatusEntity entity = dao.get(ORCID);
        Date lastIndexedDate = entity.getEmploymentsLastIndexed();
        
        // Set it to all failed
        dao.update(ORCID, false, Arrays.asList(ActivityType.values()));
        
        Api20RecordStatusEntity e = dao.get(ORCID);
        assertNotNull(e);        
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getSummaryLastIndexed());
        assertEquals(Integer.valueOf(1), e.getSummaryStatus());
        assertEquals(Integer.valueOf(1), e.getEducationsStatus());
        assertEquals(Integer.valueOf(1), e.getEmploymentsStatus());
        assertEquals(Integer.valueOf(1), e.getFundingsStatus());
        assertEquals(Integer.valueOf(1), e.getPeerReviewsStatus());
        assertEquals(Integer.valueOf(1), e.getSummaryStatus());
        assertEquals(Integer.valueOf(1), e.getWorksStatus());
        
        assertEquals(lastIndexedDate, e.getEducationsLastIndexed());
        assertEquals(lastIndexedDate, e.getEmploymentsLastIndexed());
        assertEquals(lastIndexedDate, e.getFundingsLastIndexed());
        assertEquals(lastIndexedDate, e.getPeerReviewsLastIndexed());
        assertEquals(lastIndexedDate, e.getWorksLastIndexed()); 
        
        // Set it to all worked
        dao.update(ORCID, true, Arrays.asList());
        
        e = dao.get(ORCID);
        assertNotNull(e);        
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getLastModified());
        assertNotNull(e.getSummaryLastIndexed());
        
        lastIndexedDate = e.getLastModified();
        
        assertEquals(Integer.valueOf(0), e.getSummaryStatus());
        assertEquals(Integer.valueOf(0), e.getEducationsStatus());
        assertEquals(Integer.valueOf(0), e.getEmploymentsStatus());
        assertEquals(Integer.valueOf(0), e.getFundingsStatus());
        assertEquals(Integer.valueOf(0), e.getPeerReviewsStatus());
        assertEquals(Integer.valueOf(0), e.getSummaryStatus());
        assertEquals(Integer.valueOf(0), e.getWorksStatus());
        
        assertEquals(lastIndexedDate, e.getEducationsLastIndexed());
        assertEquals(lastIndexedDate, e.getEmploymentsLastIndexed());
        assertEquals(lastIndexedDate, e.getFundingsLastIndexed());
        assertEquals(lastIndexedDate, e.getPeerReviewsLastIndexed());
        assertEquals(lastIndexedDate, e.getWorksLastIndexed());         
        
        List<ActivityType> failedElements = new ArrayList<ActivityType>();
        failedElements.add(ActivityType.EDUCATIONS);
        dao.update(ORCID, false, failedElements);
        failedElements.add(ActivityType.EMPLOYMENTS);
        dao.update(ORCID, false, failedElements);
        failedElements.add(ActivityType.FUNDINGS);
        dao.update(ORCID, false, failedElements);
        failedElements.add(ActivityType.PEER_REVIEWS);
        dao.update(ORCID, false, failedElements);
        failedElements.add(ActivityType.WORKS);
        dao.update(ORCID, false, failedElements);
        
        e = dao.get(ORCID);
        assertNotNull(e);        
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getSummaryLastIndexed());
        
        lastIndexedDate = e.getLastModified();
        
        assertEquals(Integer.valueOf(1), e.getWorksStatus());
        assertEquals(Integer.valueOf(5), e.getPeerReviewsStatus());
        assertEquals(Integer.valueOf(8), e.getFundingsStatus());
        assertEquals(Integer.valueOf(9), e.getEmploymentsStatus());
        assertEquals(Integer.valueOf(10), e.getEducationsStatus());
        assertEquals(Integer.valueOf(11), e.getSummaryStatus());
        
        assertNotNull(e.getEducationsLastIndexed());
        assertNotNull(e.getEmploymentsLastIndexed());
        assertNotNull(e.getFundingsLastIndexed());
        assertNotNull(e.getPeerReviewsLastIndexed());
        assertNotNull(e.getWorksLastIndexed());
        
        dao.update(ORCID, true, Arrays.asList());
        
        e = dao.get(ORCID); 
        
        assertNotNull(e);        
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getSummaryLastIndexed());
        
        lastIndexedDate = e.getLastModified();
        
        assertEquals(Integer.valueOf(0), e.getSummaryStatus());
        assertEquals(Integer.valueOf(0), e.getEducationsStatus());
        assertEquals(Integer.valueOf(0), e.getEmploymentsStatus());
        assertEquals(Integer.valueOf(0), e.getFundingsStatus());
        assertEquals(Integer.valueOf(0), e.getPeerReviewsStatus());
        assertEquals(Integer.valueOf(0), e.getSummaryStatus());
        assertEquals(Integer.valueOf(0), e.getWorksStatus());
        
        assertEquals(lastIndexedDate, e.getEducationsLastIndexed());
        assertEquals(lastIndexedDate, e.getEmploymentsLastIndexed());
        assertEquals(lastIndexedDate, e.getFundingsLastIndexed());
        assertEquals(lastIndexedDate, e.getPeerReviewsLastIndexed());
        assertEquals(lastIndexedDate, e.getWorksLastIndexed());         
        
        entityManager.remove(dao.get(ORCID));
    }
}
