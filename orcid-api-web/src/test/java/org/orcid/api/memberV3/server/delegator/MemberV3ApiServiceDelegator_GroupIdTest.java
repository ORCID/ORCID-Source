package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.core.exception.DuplicatedGroupIdRecordException;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.InvalidIssnException;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord;
import org.orcid.jaxb.model.v3.release.groupid.GroupIdRecords;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the group-id endpoints of the member V3 API.
 *
 * <p>
 * The old versions of these tests reached through {@code ReflectionTestUtils}
 * into {@code GroupIdRecordManagerImpl}'s own {@code issnClient},
 * {@code issnValidator} and {@code groupIdRecordDao}, so they were really
 * manager tests wearing a delegator's clothes: what they asserted -- that a
 * missing ISSN record is fetched from the ISSN service and persisted against the
 * ORCID source client -- is {@code GroupIdRecordManager} behaviour and belongs to
 * its own test. What is left here, and is genuinely the delegator's, is the
 * branch in {@code createGroupIdRecord} and
 * {@code findGroupIdRecordByGroupId}: an unknown group id that looks like an
 * ISSN is created from the ISSN service rather than from the request, and
 * creation then reports a duplicate.
 */
public class MemberV3ApiServiceDelegator_GroupIdTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private GroupIdRecord groupIdRecord(Long putCode, String groupId, String name, String description, String type) {
        GroupIdRecord record = new GroupIdRecord();
        record.setPutCode(putCode);
        record.setGroupId(groupId);
        record.setName(name);
        record.setDescription(description);
        record.setType(type);
        record.setLastModifiedDate(lastModified());
        record.setCreatedDate(created());
        return record;
    }

    @Test
    public void testGetGroupIdRecord() {
        when(groupIdRecordManagerReadOnly.getGroupIdRecord(Long.valueOf("2")))
                .thenReturn(groupIdRecord(2L, "issn:0000-0002", "TestGroup2", "TestDescription2", "publisher"));

        Response response = serviceDelegator.viewGroupIdRecord(Long.valueOf("2"));
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        Utils.verifyLastModified(groupIdRecord.getLastModifiedDate());
        assertEquals(Long.valueOf(2), groupIdRecord.getPutCode());
        assertEquals("issn:0000-0002", groupIdRecord.getGroupId());
        assertEquals("TestGroup2", groupIdRecord.getName());
        assertEquals("TestDescription2", groupIdRecord.getDescription());
        assertEquals("publisher", groupIdRecord.getType());
        verify(orcidSecurityManager).checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
    }

    @Test
    public void testCreateGroupIdRecord() throws Exception {
        // An unknown group id that looks like an ISSN is not created from the
        // request: the delegator asks the manager to build it from the ISSN
        // service instead, and reports the request as a duplicate.
        when(groupIdRecordManager.exists("issn:1234-5678")).thenReturn(false);
        when(groupIdRecordManager.createOrcidSourceIssnGroupIdRecord("issn:1234-5678", "1234-5678"))
                .thenReturn(groupIdRecord(100L, "issn:1234-5678", "something", null, "journal"));

        try {
            serviceDelegator.createGroupIdRecord(Utils.getGroupIdRecord());
            fail();
        } catch (DuplicatedGroupIdRecordException e) {
            verify(groupIdRecordManager).createOrcidSourceIssnGroupIdRecord("issn:1234-5678", "1234-5678");
            verify(orcidSecurityManager).checkScopes(ScopePathType.GROUP_ID_RECORD_UPDATE);
        }
    }

    @Test
    public void testCreateNonIssnGroupIdRecord() {
        when(groupIdRecordManager.exists("publons:errrmmmmm")).thenReturn(false);
        when(groupIdRecordManager.createGroupIdRecord(any(GroupIdRecord.class)))
                .thenReturn(groupIdRecord(101L, "publons:errrmmmmm", "TestGroup5", "TestDescription5", "publisher"));

        Response response = serviceDelegator.createGroupIdRecord(Utils.getNonIssnGroupIdRecord());
        assertNotNull(response.getMetadata().get("Location").get(0));
        assertEquals(Long.valueOf(101L), Utils.getPutCode(response));
        verify(groupIdRecordManager).createGroupIdRecord(any(GroupIdRecord.class));
    }

    @Test
    public void testUpdateGroupIdRecord() {
        when(groupIdRecordManagerReadOnly.getGroupIdRecord(Long.valueOf("3")))
                .thenReturn(groupIdRecord(3L, "issn:0000-0003", "TestGroup3", "TestDescription3", "publisher"))
                .thenReturn(groupIdRecord(3L, "issn:0000-0003", "TestGroup33", "TestDescription3", "publisher"));
        when(groupIdRecordManager.updateGroupIdRecord(eq(Long.valueOf("3")), any(GroupIdRecord.class)))
                .thenReturn(groupIdRecord(3L, "issn:0000-0003", "TestGroup33", "TestDescription3", "publisher"));

        Response response = serviceDelegator.viewGroupIdRecord(Long.valueOf("3"));
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        Utils.verifyLastModified(groupIdRecord.getLastModifiedDate());

        // Verify the name
        assertEquals(groupIdRecord.getName(), "TestGroup3");
        // Set a new name for update
        groupIdRecord.setName("TestGroup33");
        serviceDelegator.updateGroupIdRecord(groupIdRecord, Long.valueOf("3"));
        verify(orcidSecurityManager).checkScopes(ScopePathType.GROUP_ID_RECORD_UPDATE);

        // Get the entity again and verify the name
        response = serviceDelegator.viewGroupIdRecord(Long.valueOf("3"));
        assertNotNull(response);
        GroupIdRecord groupIdRecordNew = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecordNew);
        Utils.verifyLastModified(groupIdRecordNew.getLastModifiedDate());
        // Verify the name
        assertEquals(groupIdRecordNew.getName(), "TestGroup33");
    }

    @Test(expected = GroupIdRecordNotFoundException.class)
    public void testDeleteGroupIdRecord() {
        when(groupIdRecordManagerReadOnly.getGroupIdRecord(5L)).thenReturn(groupIdRecord(5L, "issn:0000-0005", "TestGroup5", "TestDescription5", "publisher"))
                .thenThrow(new GroupIdRecordNotFoundException());

        // Verify if the record exists
        Response response = serviceDelegator.viewGroupIdRecord(5L);
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);

        // Delete the record
        serviceDelegator.deleteGroupIdRecord(5L);
        verify(groupIdRecordManager).deleteGroupIdRecord(5L);

        // Throws a record not found exception
        serviceDelegator.viewGroupIdRecord(5L);
    }

    @Test
    public void testGetGroupIdRecords() {
        GroupIdRecords records = new GroupIdRecords();
        records.setPage(1);
        records.setPageSize(5);
        records.setTotal(4);
        records.getGroupIdRecord().add(groupIdRecord(1L, "issn:0000-0001", "TestGroup1", "TestDescription1", "publisher"));
        records.getGroupIdRecord().add(groupIdRecord(2L, "issn:0000-0002", "TestGroup2", "TestDescription2", "publisher"));
        records.getGroupIdRecord().add(groupIdRecord(3L, "issn:0000-0003", "TestGroup3", "TestDescription3", "publisher"));
        records.getGroupIdRecord().add(groupIdRecord(4L, "issn:0000-0004", "TestGroup4", "TestDescription4", "publisher"));
        when(groupIdRecordManagerReadOnly.getGroupIdRecords("5", "1")).thenReturn(records);

        Response response = serviceDelegator.viewGroupIdRecords("5", "1");
        assertNotNull(response);
        GroupIdRecords groupIdRecords1 = (GroupIdRecords) response.getEntity();
        assertNotNull(groupIdRecords1);
        assertNotNull(groupIdRecords1.getGroupIdRecord());
        Utils.verifyLastModified(groupIdRecords1.getLastModifiedDate());
        // The DBUnit version could only bound this, because other tests in the
        // same context created group ids. The fixture is fixed now, so assert it.
        assertEquals(4, groupIdRecords1.getTotal());
        assertEquals(4, groupIdRecords1.getGroupIdRecord().size());
        verify(orcidSecurityManager).checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
    }

    @Test
    public void testFindGroupIdByName() {
        when(groupIdRecordManager.findGroupIdRecordByName("TestGroup1"))
                .thenReturn(Optional.of(groupIdRecord(1L, "issn:0000-0001", "TestGroup1", "TestDescription1", "publisher")));

        Response response = serviceDelegator.findGroupIdRecordByName("TestGroup1");
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        assertEquals("TestGroup1", groupIdRecord.getName());
        verify(orcidSecurityManager).checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
    }

    @Test
    public void testFindGroupIdByGroupId() {
        when(groupIdRecordManager.findByGroupId("issn:0000-0001"))
                .thenReturn(Optional.of(groupIdRecord(1L, "issn:0000-0001", "TestGroup1", "TestDescription1", "publisher")));

        Response response = serviceDelegator.findGroupIdRecordByGroupId("issn:0000-0001");
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        assertEquals("issn:0000-0001", groupIdRecord.getGroupId());
        verify(orcidSecurityManager).checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
    }

    @Test
    public void testFindGroupIdRecordByNonExistentIssnGroupId() throws Exception {
        // Unknown, but it looks like an ISSN: the delegator must not answer with
        // an empty record, it must ask the manager to create one from the ISSN
        // service and return that.
        when(groupIdRecordManager.findByGroupId("issn:98765432")).thenReturn(Optional.empty());
        when(groupIdRecordManager.createOrcidSourceIssnGroupIdRecord("issn:98765432", "98765432"))
                .thenReturn(groupIdRecord(200L, "issn:98765432", "some journal", null, "journal"));

        Response response = serviceDelegator.findGroupIdRecordByGroupId("issn:98765432");
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        assertEquals("issn:98765432", groupIdRecord.getGroupId());
        assertEquals("some journal", groupIdRecord.getName());
        assertEquals("journal", groupIdRecord.getType());
        verify(groupIdRecordManager).createOrcidSourceIssnGroupIdRecord("issn:98765432", "98765432");
    }

    @Test
    public void testCreateGroupIdRecordWithNonExistentIssnGroupId() throws Exception {
        when(groupIdRecordManager.exists("issn:98765432")).thenReturn(false);
        when(groupIdRecordManager.createOrcidSourceIssnGroupIdRecord("issn:98765432", "98765432"))
                .thenReturn(groupIdRecord(200L, "issn:98765432", "some journal", null, "journal"));

        GroupIdRecord record = groupIdRecord(null, "issn:98765432", "some journal", null, "journal");

        try {
            serviceDelegator.createGroupIdRecord(record);
            fail();
        } catch (DuplicatedGroupIdRecordException e) {
            // The record the caller supplied is discarded in favour of the one
            // built from the ISSN service.
            verify(groupIdRecordManager).createOrcidSourceIssnGroupIdRecord("issn:98765432", "98765432");
            verify(groupIdRecordManager, never()).createGroupIdRecord(any(GroupIdRecord.class));
        }
    }

    @Test
    public void testCreateGroupIdRecordWithAnotherNonExistentIssnGroupId() throws Exception {
        when(groupIdRecordManager.exists("issn:9876-543X")).thenReturn(false);
        when(groupIdRecordManager.createOrcidSourceIssnGroupIdRecord("issn:9876-543X", "9876-543X"))
                .thenReturn(groupIdRecord(201L, "issn:9876-543X", "some journal", null, "journal"));

        GroupIdRecord record = groupIdRecord(null, "issn:9876-543X", "some journal", null, "journal");

        try {
            serviceDelegator.createGroupIdRecord(record);
            fail();
        } catch (DuplicatedGroupIdRecordException e) {
            verify(groupIdRecordManager).createOrcidSourceIssnGroupIdRecord("issn:9876-543X", "9876-543X");
            verify(groupIdRecordManager, never()).createGroupIdRecord(any(GroupIdRecord.class));
        }
    }

    @Test
    public void testCreateGroupIdRecordWithInvalidIssnGroupId() throws Exception {
        when(groupIdRecordManager.exists("issn:ermmmmm")).thenReturn(false);
        // Whether the issn is well formed is GroupIdRecordManager's decision; the
        // delegator's contract is to let InvalidIssnException through untouched
        // rather than reporting a duplicate.
        doThrow(new InvalidIssnException()).when(groupIdRecordManager).createOrcidSourceIssnGroupIdRecord("issn:ermmmmm", "ermmmmm");

        GroupIdRecord record = groupIdRecord(null, "issn:ermmmmm", "some journal", null, "journal");

        try {
            serviceDelegator.createGroupIdRecord(record);
            fail();
        } catch (InvalidIssnException e) {
            assertTrue(true);
        }
    }
}
