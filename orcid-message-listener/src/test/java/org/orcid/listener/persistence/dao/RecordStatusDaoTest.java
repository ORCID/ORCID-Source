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
package org.orcid.listener.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.listener.persistence.entities.RecordStatusEntity;
import org.orcid.listener.persistence.managers.RecordStatusManager;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-message-listener-test-context.xml" })
public class RecordStatusDaoTest {
	@Resource
	private RecordStatusDao recordStatusDao;
	
	@Test
	@Transactional
	public void createTest() {
		String orcid = "0000-0000-0000-0000";
		assertFalse(recordStatusDao.exists(orcid));
		recordStatusDao.create(orcid, AvailableBroker.DUMP_STATUS_1_2_API, RecordStatusManager.OK);
		assertTrue(recordStatusDao.exists(orcid));
	}
	
	@Test
	@Transactional
	public void existsTest() {
		recordStatusDao.create("0000-0000-0001", AvailableBroker.DUMP_STATUS_1_2_API, RecordStatusManager.OK);
		recordStatusDao.create("0000-0000-0002", AvailableBroker.DUMP_STATUS_1_2_API, RecordStatusManager.OK);
		recordStatusDao.create("0000-0000-0003", AvailableBroker.DUMP_STATUS_1_2_API, RecordStatusManager.OK);
		recordStatusDao.create("0000-0000-0004", AvailableBroker.DUMP_STATUS_1_2_API, RecordStatusManager.OK);
		assertTrue(recordStatusDao.exists("0000-0000-0001"));
		assertTrue(recordStatusDao.exists("0000-0000-0002"));
		assertTrue(recordStatusDao.exists("0000-0000-0003"));
		assertTrue(recordStatusDao.exists("0000-0000-0004"));
		assertFalse(recordStatusDao.exists("0000-0000-0005"));
		assertFalse(recordStatusDao.exists("0000-0000-0006"));
		assertFalse(recordStatusDao.exists("0000-0000-0007"));
	}
	
	@Test
	@Transactional
	public void getTest() {
		String orcid = "0000-0000-1000";
		recordStatusDao.create(orcid, AvailableBroker.DUMP_STATUS_1_2_API, 100);
		RecordStatusEntity entity = recordStatusDao.get(orcid);
		assertNotNull(entity);
		assertEquals(orcid, entity.getId());
		assertEquals(Integer.valueOf(100), entity.getDumpStatus12Api());
		assertNull(entity.getDumpStatus20Api());
		assertNotNull(entity.getDateCreated());
		assertNotNull(entity.getLastModified());
	}
	
	@Test	
	@Transactional
	public void updateStatus1Test() throws InterruptedException {
		String orcid = "0000-0000-1001";
		recordStatusDao.create(orcid, AvailableBroker.DUMP_STATUS_1_2_API, RecordStatusManager.OK);		
		assertTrue(recordStatusDao.exists(orcid));
		assertTrue(recordStatusDao.updateStatus(orcid, AvailableBroker.DUMP_STATUS_1_2_API));
		assertTrue(recordStatusDao.updateStatus(orcid, AvailableBroker.DUMP_STATUS_2_0_API));
		assertFalse(recordStatusDao.updateStatus("0000-0000-0000-2000", AvailableBroker.DUMP_STATUS_1_2_API));
		assertFalse(recordStatusDao.updateStatus("0000-0000-0000-2000", AvailableBroker.DUMP_STATUS_2_0_API));
	}
	
	@Test
	@Transactional
	public void updateStatus2Test() {		
		String orcid = "0000-0000-1002";
		recordStatusDao.create(orcid, AvailableBroker.DUMP_STATUS_1_2_API, RecordStatusManager.OK);
		
		assertTrue(recordStatusDao.exists(orcid));
		
		assertTrue(recordStatusDao.updateStatus(orcid, AvailableBroker.DUMP_STATUS_1_2_API, 1250));
		assertTrue(recordStatusDao.updateStatus(orcid, AvailableBroker.DUMP_STATUS_2_0_API, 5000));
		
		assertFalse(recordStatusDao.updateStatus("0000-0000-0000-2000", AvailableBroker.DUMP_STATUS_1_2_API, 1250));
		assertFalse(recordStatusDao.updateStatus("0000-0000-0000-2000", AvailableBroker.DUMP_STATUS_2_0_API, 5000));		
	}
}
