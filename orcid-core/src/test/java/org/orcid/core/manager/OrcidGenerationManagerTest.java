package org.orcid.core.manager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.ehcache.Cache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.crypto.OrcidCheckDigitGenerator;
import org.orcid.core.manager.impl.OrcidGenerationManagerImpl;

@RunWith(MockitoJUnitRunner.class)
public class OrcidGenerationManagerTest {

    private static final int SAMPLE_SIZE = 2000;

    @Mock
    private ProfileEntityManager profileEntityManager;

    @Mock
    private Cache<String, String> recentOrcidCache;

    @InjectMocks
    private OrcidGenerationManagerImpl orcidGenerationManager;

    private final Set<String> cachedOrcids = new HashSet<String>(SAMPLE_SIZE);

    @Before
    public void setUp() {
        when(profileEntityManager.orcidExists(anyString())).thenReturn(false);
        when(recentOrcidCache.containsKey(anyString())).thenAnswer(invocation -> cachedOrcids.contains(invocation.getArgument(0)));
        doAnswer(invocation -> {
            cachedOrcids.add(invocation.getArgument(0));
            return null;
        }).when(recentOrcidCache).put(anyString(), anyString());
    }

    @Test
    public void testCreateNewOrcidV2() {
        Set<String> orcids = new HashSet<String>(SAMPLE_SIZE);
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            String orcid = orcidGenerationManager.createNewOrcid();

            assertNotNull("ORCID is null", orcid);
            assertTrue("ORCID is in wrong format " + orcid, orcid.matches("(\\d{4}-){3}\\d{3}[\\dX]"));
            assertTrue("ORCID has invalid check character " + orcid, OrcidCheckDigitGenerator.validate(orcid));
            assertFalse("ORCID has already been used " + orcid + " number of elements cached: " + orcids.size(), orcids.contains(orcid));

            String baseDigits = orcid.substring(0, orcid.length() - 1).replace("-", "");
            long numericOrcid = Long.valueOf(baseDigits);
            assertTrue("Numeric value of ORCID is too low " + orcid, numericOrcid >= OrcidGenerationManager.ORCID_BASE_V2_MIN);
            assertTrue("Numeric value of ORCID is too high " + orcid, numericOrcid <= OrcidGenerationManager.ORCID_BASE_V2_MAX);

            orcids.add(orcid);
        }
    }
}
