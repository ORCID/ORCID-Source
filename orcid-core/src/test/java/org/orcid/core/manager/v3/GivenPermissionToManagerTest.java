package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.GivenPermissionToManagerReadOnly;
import org.orcid.pojo.DelegateForm;

public class GivenPermissionToManagerTest extends BaseTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml");

    private static final String GIVER = "0000-0000-0000-0006";
    private static final String RECEIVER = "0000-0000-0000-0003";

    @Resource
    private GivenPermissionToManager givenPermissionToManager;

    @Resource
    private GivenPermissionToManagerReadOnly givenPermissionToManagerReadOnly;
    
    @Resource
    private ProfileEntityManager profileEntityManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }

    @Test
    public void testFindByGiverAndReceiverOrcid() {
        DelegateForm form = givenPermissionToManagerReadOnly.findByGiverAndReceiverOrcid(GIVER, RECEIVER);
        assertNotNull(form);
        assertEquals(GIVER, form.getGiverOrcid().getPath());
        assertEquals(RECEIVER, form.getReceiverOrcid().getPath());
    }

    @Test
    public void testRemove() {
        // Create one
        givenPermissionToManager.create(RECEIVER, GIVER);
        // Find it
        DelegateForm form = givenPermissionToManagerReadOnly.findByGiverAndReceiverOrcid(RECEIVER, GIVER);
        assertNotNull(form);
        assertEquals(RECEIVER, form.getGiverOrcid().getPath());
        assertEquals(GIVER, form.getReceiverOrcid().getPath());

        Date rLastModifiedBefore = profileEntityManager.getLastModifiedDate(RECEIVER);
        Date gLastModifiedBefore = profileEntityManager.getLastModifiedDate(GIVER);
        
        // Delete it
        givenPermissionToManager.remove(RECEIVER, GIVER);
        // Verify it was deleted
        form = givenPermissionToManagerReadOnly.findByGiverAndReceiverOrcid(RECEIVER, GIVER);
        assertNull(form);

        Date rLastModifiedAfter = profileEntityManager.getLastModifiedDate(RECEIVER);
        Date gLastModifiedAfter = profileEntityManager.getLastModifiedDate(GIVER);

        assertTrue(rLastModifiedAfter.after(rLastModifiedBefore));
        assertTrue(gLastModifiedAfter.after(gLastModifiedBefore));
    }

    @Test
    public void testCreate() {
        Date rLastModifiedBefore = profileEntityManager.getLastModifiedDate(RECEIVER);
        Date gLastModifiedBefore = profileEntityManager.getLastModifiedDate(GIVER);

        // Create one
        givenPermissionToManager.create(RECEIVER, GIVER);

        DelegateForm form = givenPermissionToManagerReadOnly.findByGiverAndReceiverOrcid(RECEIVER, GIVER);
        assertNotNull(form);
        assertEquals(RECEIVER, form.getGiverOrcid().getPath());
        assertEquals(GIVER, form.getReceiverOrcid().getPath());

        Date rLastModifiedAfter = profileEntityManager.getLastModifiedDate(RECEIVER);
        Date gLastModifiedAfter = profileEntityManager.getLastModifiedDate(GIVER);

        assertTrue(rLastModifiedAfter.after(rLastModifiedBefore));
        assertTrue(gLastModifiedAfter.after(gLastModifiedBefore));

        // Delete it
        givenPermissionToManager.remove(RECEIVER, GIVER);
    }
}
