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
import org.mockito.Mockito;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.GivenPermissionToManagerReadOnly;
import org.orcid.persistence.dao.GivenPermissionToDao;
import org.orcid.persistence.jpa.entities.GivenPermissionByEntity;
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;
import org.orcid.persistence.jpa.entities.ProfileSummaryEntity;
import org.orcid.pojo.DelegateForm;
import org.springframework.test.util.ReflectionTestUtils;

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
    public void testRemoveForProfile() {
        GivenPermissionToDao dao = (GivenPermissionToDao) ReflectionTestUtils.getField(givenPermissionToManager, "givenPermissionToDao");
        GivenPermissionToDao mockDao = Mockito.mock(GivenPermissionToDao.class);
        ReflectionTestUtils.setField(givenPermissionToManager, "givenPermissionToDao", mockDao);
        Mockito.when(mockDao.findByGiver(Mockito.eq("orcid"))).thenReturn(getPermissionsGiven());
        Mockito.when(mockDao.findByReceiver(Mockito.eq("orcid"))).thenReturn(getPermissionsReceived());
        
        try {
            givenPermissionToManager.removeAllForProfile("orcid");
            Mockito.verify(mockDao, Mockito.times(1)).remove(Mockito.eq("orcid"), Mockito.eq("orcid0"));
            Mockito.verify(mockDao, Mockito.times(1)).remove(Mockito.eq("orcid"), Mockito.eq("orcid1"));
            Mockito.verify(mockDao, Mockito.times(1)).remove(Mockito.eq("orcid"), Mockito.eq("orcid2"));
            
            Mockito.verify(mockDao, Mockito.times(1)).remove(Mockito.eq("orcid0"), Mockito.eq("orcid"));
            Mockito.verify(mockDao, Mockito.times(1)).remove(Mockito.eq("orcid1"), Mockito.eq("orcid"));
            Mockito.verify(mockDao, Mockito.times(1)).remove(Mockito.eq("orcid2"), Mockito.eq("orcid"));
        } finally {
            ReflectionTestUtils.setField(givenPermissionToManager, "givenPermissionToDao", dao);
        }
    }

    private List<GivenPermissionByEntity> getPermissionsReceived() {
        List<GivenPermissionByEntity> permissions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            GivenPermissionByEntity e = new GivenPermissionByEntity();
            e.setApprovalDate(new Date());
            e.setDateCreated(new Date());
            e.setGiver(getProfileSummaryEntity("orcid" + i));
            e.setReceiver("orcid");
            e.setLastModified(new Date());
            permissions.add(e);
        }
        return permissions;
    }

    private ProfileSummaryEntity getProfileSummaryEntity(String orcid) {
        ProfileSummaryEntity e = new ProfileSummaryEntity();
        e.setId(orcid);
        return e;
    }

    private List<GivenPermissionToEntity> getPermissionsGiven() {
        List<GivenPermissionToEntity> permissions = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            GivenPermissionToEntity e = new GivenPermissionToEntity();
            e.setApprovalDate(new Date());
            e.setDateCreated(new Date());
            e.setReceiver(getProfileSummaryEntity("orcid" + i));
            e.setGiver("orcid");
            e.setLastModified(new Date());
            permissions.add(e);
        }
        return permissions;
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
