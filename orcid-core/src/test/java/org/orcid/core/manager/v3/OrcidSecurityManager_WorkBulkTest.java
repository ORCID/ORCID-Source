package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.error.OrcidError;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkBulk;

public class OrcidSecurityManager_WorkBulkTest extends OrcidSecurityManagerTestBase {

    @Test(expected = OrcidUnauthorizedException.class)
    public void testWorkBulkWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
        WorkBulk workBulk = new WorkBulk();
        orcidSecurityManager.checkAndFilter(ORCID_2, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        fail();
    }

    @Test
    public void testPublicWorkBulkReadPublicToken() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.setBulk(Arrays.asList(createWork(Visibility.PUBLIC, CLIENT_2)));
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertNotNull(workBulk);
        assertEquals(1, workBulk.getBulk().size());
    }
    
    @Test
    public void testMixedPublicAndLimitedWorkBulkReadPublicToken() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.setBulk(Arrays.asList(createWork(Visibility.PUBLIC, CLIENT_2), createWork(Visibility.LIMITED, CLIENT_2)));
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertNotNull(workBulk);
        assertEquals(2, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof OrcidError);
    }
    
    @Test
    public void testMixedPublicAndLimitedWorkBulkReadLimitedToken() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.setBulk(Arrays.asList(createWork(Visibility.PUBLIC, CLIENT_2), createWork(Visibility.LIMITED, CLIENT_2)));
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        orcidSecurityManager.checkAndFilter(ORCID_1, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertNotNull(workBulk);
        assertEquals(2, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof Work);
    }
    
    @Test
    public void testMixedPublicAndLimitedWorkBulkReadPublicTokenMatchingSource() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.setBulk(Arrays.asList(createWork(Visibility.PUBLIC, CLIENT_2), createWork(Visibility.LIMITED, CLIENT_2)));
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_2, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertNotNull(workBulk);
        assertEquals(2, workBulk.getBulk().size());
    }
    
    @Test
    public void testLimitedWorkBulkReadPublicTokenNoSource() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.setBulk(Arrays.asList(createWork(Visibility.LIMITED, null), createWork(Visibility.LIMITED, null)));
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_2, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertNotNull(workBulk);
        assertEquals(2, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof OrcidError);
        assertTrue(workBulk.getBulk().get(1) instanceof OrcidError);
    }
    
    @Test
    public void testLimitedWorkBulkReadPublicTokenMixedSources() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.setBulk(Arrays.asList(createWork(Visibility.PUBLIC, CLIENT_1), createWork(Visibility.LIMITED, CLIENT_2)));
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        orcidSecurityManager.checkAndFilter(ORCID_1, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertNotNull(workBulk);
        assertEquals(2, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof Work);
        assertTrue(workBulk.getBulk().get(1) instanceof OrcidError);
    }
    
    @Test
    public void testPrivateWorkBulkReadLimitedToken() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.setBulk(Arrays.asList(createWork(Visibility.PRIVATE, CLIENT_2)));
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_LIMITED);
        orcidSecurityManager.checkAndFilter(ORCID_1, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertNotNull(workBulk);
        assertEquals(1, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof OrcidError);
    }
    
    @Test
    public void testPrivateWorkBulkReadLimitedTokenMatchingSource() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.setBulk(Arrays.asList(createWork(Visibility.PRIVATE, CLIENT_2)));
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_2, ScopePathType.READ_LIMITED);
        orcidSecurityManager.checkAndFilter(ORCID_1, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertNotNull(workBulk);
        assertEquals(1, workBulk.getBulk().size());
    }

    @Test
    public void testPrivateWorkBulkReadLimitedTokenNoSource() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.setBulk(Arrays.asList(createWork(Visibility.PRIVATE, null)));
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_2, ScopePathType.READ_LIMITED);
        orcidSecurityManager.checkAndFilter(ORCID_1, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertNotNull(workBulk);
        assertEquals(1, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof OrcidError);
    }
    
    @Test
    public void testPrivateWorkBulkReadLimitedTokenMixedSources() {
        WorkBulk workBulk = new WorkBulk();
        workBulk.setBulk(Arrays.asList(createWork(Visibility.PRIVATE, CLIENT_1), createWork(Visibility.PRIVATE, CLIENT_2)));
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_2, ScopePathType.READ_LIMITED);
        orcidSecurityManager.checkAndFilter(ORCID_1, workBulk, ScopePathType.ORCID_WORKS_READ_LIMITED);
        assertNotNull(workBulk);
        assertEquals(2, workBulk.getBulk().size());
        assertTrue(workBulk.getBulk().get(0) instanceof OrcidError);
        assertTrue(workBulk.getBulk().get(1) instanceof Work);
    }

}
