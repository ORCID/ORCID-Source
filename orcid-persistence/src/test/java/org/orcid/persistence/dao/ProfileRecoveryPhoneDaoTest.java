package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import jakarta.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.orcid.persistence.dao.ProfileRecoveryPhoneDao.UpsertResult;
import org.orcid.persistence.jpa.entities.ProfileRecoveryPhoneEntity;
import org.orcid.test.DatabaseTest;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Drives the DAO through its Spring proxy against the test database, so the
 * read-only transaction manager on findByOrcid and the write transaction on
 * upsert are the ones that run, not a mock's idea of them.
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
@Category(DatabaseTest.class)
public class ProfileRecoveryPhoneDaoTest extends DBUnitTest {

    private static final String ORCID = "4444-4444-4444-4441";

    @Resource(name = "profileRecoveryPhoneDao")
    private ProfileRecoveryPhoneDao dao;

    @Resource(name = "transactionTemplate")
    private TransactionTemplate transactionTemplate;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml"));
    }

    @After
    public void removeTheRow() {
        dao.deleteByOrcid(ORCID);
    }

    @Test
    public void findByOrcidAnswersNullWhenNothingIsStored() {
        assertNull(dao.findByOrcid(ORCID));
    }

    @Test
    public void upsertCreatesTheRowAndSaysSo() {
        UpsertResult result = dao.upsert(ORCID, "encrypted-one", "7890");

        assertTrue(result.isInserted());
        ProfileRecoveryPhoneEntity written = result.getEntity();
        assertNotNull(written.getId());
        assertNotNull(written.getDateCreated());
        assertEquals(written.getDateCreated(), written.getLastModified());

        ProfileRecoveryPhoneEntity read = dao.findByOrcid(ORCID);
        assertEquals("7890", read.getLastFour());
        assertEquals("encrypted-one", read.getEncryptedPhoneNumber());
        assertEquals(written.getId(), read.getId());
    }

    @Test
    public void upsertReplacesTheRowAndSaysSo() {
        UpsertResult first = dao.upsert(ORCID, "encrypted-one", "7890");
        UpsertResult second = dao.upsert(ORCID, "encrypted-two", "4321");

        assertFalse(second.isInserted());
        assertEquals(first.getEntity().getId(), second.getEntity().getId());
        assertEquals(first.getEntity().getDateCreated(), second.getEntity().getDateCreated());

        ProfileRecoveryPhoneEntity read = dao.findByOrcid(ORCID);
        assertEquals("4321", read.getLastFour());
        assertEquals("encrypted-two", read.getEncryptedPhoneNumber());
    }

    /*
     * The manager builds its answer from the returned row while its own
     * transaction is still open. The entity callback that stamps lastModified
     * only runs at flush, so upsert has to flush before returning or the row
     * it hands back still carries the previous date. Asserting inside an outer
     * transaction is what makes that visible: without the flush the commit
     * would stamp the date later and a plain call could never tell.
     */
    @Test
    public void upsertAnswersWithTheFlushedRowInsideAnOuterTransaction() throws Exception {
        Date before = dao.upsert(ORCID, "encrypted-one", "7890").getEntity().getLastModified();
        // The stamp has millisecond resolution; a replacement inside the same
        // millisecond would be indistinguishable from no stamp at all
        Thread.sleep(5);

        // Read while the outer transaction is still open: execute() commits on the
        // way out, and a commit stamps the date too, which would hide a missing flush
        Date stampedBeforeCommit = transactionTemplate.execute(status -> {
            UpsertResult second = dao.upsert(ORCID, "encrypted-two", "4321");
            assertFalse(second.isInserted());
            return second.getEntity().getLastModified();
        });

        assertTrue("lastModified must already be stamped when upsert returns", stampedBeforeCommit.after(before));
        assertEquals(stampedBeforeCommit, dao.findByOrcid(ORCID).getLastModified());
    }

    @Test
    public void deleteByOrcidReportsWhetherARowWent() {
        assertFalse(dao.deleteByOrcid(ORCID));
        dao.upsert(ORCID, "encrypted-one", "7890");
        assertTrue(dao.deleteByOrcid(ORCID));
        assertFalse(dao.deleteByOrcid(ORCID));
        assertNull(dao.findByOrcid(ORCID));
    }
}
