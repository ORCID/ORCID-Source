package org.orcid.admin.cli;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.frontend.spring.session.redis.OrcidRedisIndexedSessionRepository;
import org.orcid.persistence.dao.ProfileDao;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class SetForcePasswordResetTest {

    @Mock
    private ProfileDao profileDao;

    @Mock
    private OrcidRedisIndexedSessionRepository sessionRepository;

    private SetForcePasswordReset setForcePasswordReset;

    @Before
    public void setUp() {
        setForcePasswordReset = new SetForcePasswordReset();
        ReflectionTestUtils.setField(setForcePasswordReset, "profileDao", profileDao);
        ReflectionTestUtils.setField(setForcePasswordReset, "sessionRepository", sessionRepository);
    }

    @Test
    public void executeShouldUpdateForcePasswordResetInTenThousandSizedBatches() {
        List<String> orcidIds = new ArrayList<>();
        for (int i = 0; i < 25001; i++) {
            orcidIds.add(String.format("0000-0000-0000-%04d", i));
        }

        ReflectionTestUtils.setField(setForcePasswordReset, "orcidIds", String.join(",", orcidIds));

        when(profileDao.orcidExists(anyString())).thenReturn(true);
        when(profileDao.updateForcePasswordReset(any(List.class), any(Date.class))).thenAnswer(invocation -> ((List<?>) invocation.getArgument(0)).size());
        when(sessionRepository.findByIndexNameAndIndexValue(eq(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME), anyString()))
                .thenReturn(Collections.emptyMap());

        setForcePasswordReset.execute();

        ArgumentCaptor<List> batchCaptor = ArgumentCaptor.forClass(List.class);
        verify(profileDao, times(3)).updateForcePasswordReset(batchCaptor.capture(), any(Date.class));
        verify(sessionRepository, times(25001)).findByIndexNameAndIndexValue(eq(FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME), anyString());

        List<List> batches = batchCaptor.getAllValues();
        assertEquals(10000, batches.get(0).size());
        assertEquals(10000, batches.get(1).size());
        assertEquals(5001, batches.get(2).size());
    }
}
