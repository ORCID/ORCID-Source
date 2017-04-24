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
package org.orcid.core.manager;

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
import org.orcid.persistence.jpa.entities.GivenPermissionToEntity;

public class GivenPermissionToManagerTest extends BaseTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml");

    private static final String GIVER = "0000-0000-0000-0006";
    private static final String RECEIVER = "0000-0000-0000-0003";

    @Resource
    private GivenPermissionToManager givenPermissionToManager;

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
        GivenPermissionToEntity entity = givenPermissionToManager.findByGiverAndReceiverOrcid(GIVER, RECEIVER);
        assertNotNull(entity);
        assertEquals(GIVER, entity.getGiver());
        assertEquals(RECEIVER, entity.getReceiver().getId());
    }

    @Test
    public void testRemove() {
        // Create one
        givenPermissionToManager.create(RECEIVER, GIVER);
        // Find it
        GivenPermissionToEntity entity = givenPermissionToManager.findByGiverAndReceiverOrcid(RECEIVER, GIVER);
        assertNotNull(entity);
        assertEquals(RECEIVER, entity.getGiver());
        assertEquals(GIVER, entity.getReceiver().getId());

        Date rLastModifiedBefore = profileEntityManager.getLastModifiedDate(RECEIVER);
        Date gLastModifiedBefore = profileEntityManager.getLastModifiedDate(GIVER);
        
        // Delete it
        givenPermissionToManager.remove(RECEIVER, GIVER);
        // Verify it was deleted
        entity = givenPermissionToManager.findByGiverAndReceiverOrcid(RECEIVER, GIVER);
        assertNull(entity);

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

        GivenPermissionToEntity entity = givenPermissionToManager.findByGiverAndReceiverOrcid(RECEIVER, GIVER);
        assertNotNull(entity);
        assertEquals(RECEIVER, entity.getGiver());
        assertEquals(GIVER, entity.getReceiver().getId());

        Date rLastModifiedAfter = profileEntityManager.getLastModifiedDate(RECEIVER);
        Date gLastModifiedAfter = profileEntityManager.getLastModifiedDate(GIVER);

        assertTrue(rLastModifiedAfter.after(rLastModifiedBefore));
        assertTrue(gLastModifiedAfter.after(gLastModifiedBefore));

        // Delete it
        givenPermissionToManager.remove(RECEIVER, GIVER);
    }
}
