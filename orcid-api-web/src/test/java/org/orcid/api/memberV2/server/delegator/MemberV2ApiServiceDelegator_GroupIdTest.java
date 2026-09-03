package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.core.exception.DuplicatedGroupIdRecordException;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.InvalidIssnException;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.test.helper.Utils;

/**
 * The group-id endpoints of the member v2 delegator, on mocks.
 *
 * <p>
 * These are the only endpoints in the family with non-trivial logic of the
 * delegator's own: the "issn:" branch that turns a request for an unknown ISSN
 * group id into a freshly minted ORCID-sourced record, and the strip that
 * removes invisible control characters from a submitted name. Both are asserted
 * here. Everything that happens once {@code GroupIdRecordManager} is reached --
 * ISSN validation, the ISSN lookup, the "not found" on a deleted record --
 * belongs to that manager's own tests.
 */
public class MemberV2ApiServiceDelegator_GroupIdTest extends MemberV2ApiServiceDelegatorMockBase {

    @Test
    public void testGetGroupIdRecord() {
        GroupIdRecord stored = groupIdRecord(2L, "issn:0000-0002", "TestGroup2", "TestDescription2");
        when(groupIdRecordManagerReadOnly.getGroupIdRecord(2L)).thenReturn(stored);

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
        // An ISSN group id that does not exist yet is not created on the client's
        // behalf: the delegator mints an ORCID-sourced record for it and then
        // refuses the request, so that the client re-reads it rather than owning
        // it.
        GroupIdRecord toCreate = Utils.getGroupIdRecord();
        when(groupIdRecordManager.exists("issn:1234-5678")).thenReturn(false);
        when(groupIdRecordManager.createOrcidSourceIssnGroupIdRecord("issn:1234-5678", "1234-5678"))
                .thenReturn(groupIdRecord(50L, "issn:1234-5678", "something", "something"));

        try {
            serviceDelegator.createGroupIdRecord(toCreate);
            fail();
        } catch (DuplicatedGroupIdRecordException e) {
            verify(orcidSecurityManager).checkScopes(ScopePathType.GROUP_ID_RECORD_UPDATE);
            verify(groupIdRecordManager).createOrcidSourceIssnGroupIdRecord("issn:1234-5678", "1234-5678");
            verify(groupIdRecordManager, never()).createGroupIdRecord(any(GroupIdRecord.class));
        }
    }

    @Test
    public void testCreateNonIssnGroupIdRecord() {
        GroupIdRecord toCreate = Utils.getNonIssnGroupIdRecord();
        when(groupIdRecordManager.exists("publons:errrmmmmm")).thenReturn(false);
        when(groupIdRecordManager.createGroupIdRecord(any(GroupIdRecord.class)))
                .thenReturn(groupIdRecord(51L, "publons:errrmmmmm", "TestGroup5", "TestDescription5"));

        Response response = serviceDelegator.createGroupIdRecord(toCreate);

        assertNotNull(response.getMetadata().get("Location").get(0));
        assertEquals(Long.valueOf(51), Utils.getPutCode(response));
        verify(orcidSecurityManager).checkScopes(ScopePathType.GROUP_ID_RECORD_UPDATE);
        // a non-ISSN group id takes the ordinary path, not the ISSN one
        verify(groupIdRecordManager, never()).createOrcidSourceIssnGroupIdRecord(anyString(), anyString());
        verify(groupIdRecordManager).createGroupIdRecord(any(GroupIdRecord.class));
    }

    @Test
    public void testUpdateGroupIdRecord() {
        // The name arrives with an invisible control character in it; the
        // delegator strips it before the record reaches the manager.
        GroupIdRecord toUpdate = groupIdRecord(3L, "issn:0000-0003", "TestGroup33" + '\u0098', "TestDescription3");
        when(groupIdRecordManager.updateGroupIdRecord(eq(3L), any(GroupIdRecord.class)))
                .thenReturn(groupIdRecord(3L, "issn:0000-0003", "TestGroup33", "TestDescription3"));

        Response response = serviceDelegator.updateGroupIdRecord(toUpdate, Long.valueOf("3"));

        assertNotNull(response);
        GroupIdRecord groupIdRecordNew = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecordNew);
        Utils.verifyLastModified(groupIdRecordNew.getLastModifiedDate());
        assertEquals("TestGroup33", groupIdRecordNew.getName());
        verify(orcidSecurityManager).checkScopes(ScopePathType.GROUP_ID_RECORD_UPDATE);
        ArgumentCaptor<GroupIdRecord> submitted = ArgumentCaptor.forClass(GroupIdRecord.class);
        verify(groupIdRecordManager).updateGroupIdRecord(eq(3L), submitted.capture());
        assertEquals("TestGroup33", submitted.getValue().getName());
    }

    @Test(expected = GroupIdRecordNotFoundException.class)
    public void testDeleteGroupIdRecord() {
        when(groupIdRecordManagerReadOnly.getGroupIdRecord(5L)).thenReturn(groupIdRecord(5L, "issn:0000-0005", "TestGroup5", "TestDescription5"))
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
        records.setTotal(4);
        records.setPage(1);
        records.setPageSize(5);
        records.getGroupIdRecord().add(groupIdRecord(1L, "issn:0000-0001", "TestGroup1", "TestDescription1"));
        records.getGroupIdRecord().add(groupIdRecord(2L, "issn:0000-0002", "TestGroup2", "TestDescription2"));
        records.getGroupIdRecord().add(groupIdRecord(3L, "issn:0000-0003", "TestGroup3", "TestDescription3"));
        records.getGroupIdRecord().add(groupIdRecord(4L, "issn:0000-0004", "TestGroup4", "TestDescription4"));
        when(groupIdRecordManagerReadOnly.getGroupIdRecords("5", "1")).thenReturn(records);

        Response response = serviceDelegator.viewGroupIdRecords("5", "1");

        assertNotNull(response);
        GroupIdRecords groupIdRecords1 = (GroupIdRecords) response.getEntity();
        assertNotNull(groupIdRecords1);
        assertNotNull(groupIdRecords1.getGroupIdRecord());

        int total = groupIdRecords1.getTotal();
        if (total < 3 || total > 5) {
            fail("There are more group ids than the expected, we are expecting between 3 and 5, total: " + total);
        }
        // the latest last-modified of the page is computed by the delegator
        Utils.verifyLastModified(groupIdRecords1.getLastModifiedDate());
        verify(orcidSecurityManager).checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
    }

    @Test
    public void testFindGroupIdByName() {
        when(groupIdRecordManager.findGroupIdRecordByName("TestGroup1"))
                .thenReturn(Optional.of(groupIdRecord(1L, "issn:0000-0001", "TestGroup1", "TestDescription1")));

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
                .thenReturn(Optional.of(groupIdRecord(1L, "issn:0000-0001", "TestGroup1", "TestDescription1")));

        Response response = serviceDelegator.findGroupIdRecordByGroupId("issn:0000-0001");

        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        assertEquals("issn:0000-0001", groupIdRecord.getGroupId());
        verify(orcidSecurityManager).checkScopes(ScopePathType.GROUP_ID_RECORD_READ);
        // an existing group id must not be minted again
        verify(groupIdRecordManager, never()).createOrcidSourceIssnGroupIdRecord(anyString(), anyString());
    }

    @Test
    public void testFindGroupIdRecordByNonExistentIssnGroupId() throws Exception {
        // The delegator's own ISSN branch: an unknown "issn:" group id is minted
        // on the spot and returned. Whether the ISSN is real, and what name it
        // gets, is GroupIdRecordManagerImpl's business.
        when(groupIdRecordManager.findByGroupId("issn:98765432")).thenReturn(Optional.empty());
        when(groupIdRecordManager.createOrcidSourceIssnGroupIdRecord("issn:98765432", "98765432"))
                .thenReturn(groupIdRecord(60L, "issn:98765432", "some journal", "some journal"));

        Response response = serviceDelegator.findGroupIdRecordByGroupId("issn:98765432");

        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        assertEquals("issn:98765432", groupIdRecord.getGroupId());
        assertEquals("some journal", groupIdRecord.getName());
        verify(groupIdRecordManager).createOrcidSourceIssnGroupIdRecord("issn:98765432", "98765432");
    }

    @Test
    public void testCreateGroupIdRecordWithNonExistentIssnGroupId() throws Exception {
        GroupIdRecord record = groupIdRecord(null, "issn:98765432", "some journal", "some journal");
        record.setType("journal");
        when(groupIdRecordManager.exists("issn:98765432")).thenReturn(false);
        when(groupIdRecordManager.createOrcidSourceIssnGroupIdRecord("issn:98765432", "98765432"))
                .thenReturn(groupIdRecord(60L, "issn:98765432", "some journal", "some journal"));

        try {
            serviceDelegator.createGroupIdRecord(record);
            fail();
        } catch (DuplicatedGroupIdRecordException e) {
            verify(groupIdRecordManager).createOrcidSourceIssnGroupIdRecord("issn:98765432", "98765432");
            verify(groupIdRecordManager, never()).createGroupIdRecord(any(GroupIdRecord.class));
        }
    }

    @Test
    public void testCreateGroupIdRecordWithAnotherNonExistentIssnGroupId() throws Exception {
        GroupIdRecord record = groupIdRecord(null, "issn:9876-543X", "some journal", "some journal");
        record.setType("journal");
        when(groupIdRecordManager.exists("issn:9876-543X")).thenReturn(false);
        when(groupIdRecordManager.createOrcidSourceIssnGroupIdRecord("issn:9876-543X", "9876-543X"))
                .thenReturn(groupIdRecord(61L, "issn:9876-543X", "some journal", "some journal"));

        try {
            serviceDelegator.createGroupIdRecord(record);
            fail();
        } catch (DuplicatedGroupIdRecordException e) {
            // the issn is taken out of the group id by the delegator's own regex,
            // which has to cope with the trailing X of a check digit
            verify(groupIdRecordManager).createOrcidSourceIssnGroupIdRecord("issn:9876-543X", "9876-543X");
            verify(groupIdRecordManager, never()).createGroupIdRecord(any(GroupIdRecord.class));
        }
    }

    @Test
    public void testCreateGroupIdRecordWithInvalidIssnGroupId() throws Exception {
        // The ISSN itself is validated inside GroupIdRecordManagerImpl.
        GroupIdRecord record = groupIdRecord(null, "issn:ermmmmm", "some journal", "some journal");
        record.setType("journal");
        when(groupIdRecordManager.exists("issn:ermmmmm")).thenReturn(false);
        doThrow(new InvalidIssnException()).when(groupIdRecordManager).createOrcidSourceIssnGroupIdRecord("issn:ermmmmm", "ermmmmm");

        try {
            serviceDelegator.createGroupIdRecord(record);
            fail();
        } catch (InvalidIssnException e) {
            verify(groupIdRecordManager, never()).createGroupIdRecord(any(GroupIdRecord.class));
        }
    }

    // ------------------------------------------------------------- fixtures

    private GroupIdRecord groupIdRecord(Long putCode, String groupId, String name, String description) {
        GroupIdRecord record = new GroupIdRecord();
        record.setPutCode(putCode);
        record.setGroupId(groupId);
        record.setName(name);
        record.setDescription(description);
        record.setType("publisher");
        record.setCreatedDate(createdDate());
        record.setLastModifiedDate(lastModified());
        return record;
    }
}
