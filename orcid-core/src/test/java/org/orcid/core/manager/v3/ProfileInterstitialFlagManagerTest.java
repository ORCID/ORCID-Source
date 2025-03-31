package org.orcid.core.manager.v3;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.v3.impl.ProfileInterstitialFlagManagerImpl;
import org.orcid.persistence.dao.ProfileInterstitialFlagDao;
import org.orcid.persistence.jpa.entities.ProfileInterstitialFlagEntity;
import org.orcid.test.TargetProxyHelper;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ProfileInterstitialFlagManagerTest {
    @Mock
    private ProfileInterstitialFlagDao profileInterstitialFlagDao;

    @Mock
    private ProfileInterstitialFlagDao profileInterstitialFlagDaoReadOnly;

    ProfileInterstitialFlagManager pifm = new ProfileInterstitialFlagManagerImpl();


    private static final String ORCID = "0000-0000-0000-0001";
    private static final String ORCID_TWO = "0000-0000-0000-0002";
    private static final String INTERSTITIAL_FLAG = "DOMAIN_INTERSTITIAL";
    private static final String INTERSTITIAL_FLAG_TWO = "AFFILIATION_INTERSTITIAL";


    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(pifm, "profileInterstitialFlagDao", profileInterstitialFlagDao);
        TargetProxyHelper.injectIntoProxy(pifm, "profileInterstitialFlagDaoReadOnly", profileInterstitialFlagDaoReadOnly);

        ProfileInterstitialFlagEntity pif1 = new ProfileInterstitialFlagEntity();
        ProfileInterstitialFlagEntity pif2 = new ProfileInterstitialFlagEntity();

        pif1.setInterstitialName(INTERSTITIAL_FLAG);
        pif1.setOrcid(ORCID);

        pif2.setInterstitialName(INTERSTITIAL_FLAG_TWO);
        pif2.setOrcid(ORCID);

        when(profileInterstitialFlagDaoReadOnly.findByOrcid(eq(ORCID))).thenReturn(List.of(pif1, pif2));
        when(profileInterstitialFlagDaoReadOnly.findByOrcid(eq(ORCID_TWO))).thenReturn(List.of(pif2));
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFlag_nullOrcid() {
        pifm.addInterstitialFlag(null, INTERSTITIAL_FLAG);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addFlag_nullInterstitialName() {
        pifm.addInterstitialFlag(ORCID, null);
    }

    @Test
    public void addFlag_success() {
        pifm.addInterstitialFlag(ORCID, INTERSTITIAL_FLAG);
        verify(profileInterstitialFlagDao, times(1)).addInterstitialFlag(eq(ORCID), eq(INTERSTITIAL_FLAG));
    }

    @Test
    public void findByOrcid() {
        List<String> flags = pifm.findByOrcid(ORCID);
        assertEquals(flags.size(), 2);
        assertEquals(flags.get(0), INTERSTITIAL_FLAG);
        assertEquals(flags.get(1), INTERSTITIAL_FLAG_TWO);

        List<String> flags2 = pifm.findByOrcid(ORCID_TWO);
        assertEquals(flags2.size(), 1);
        assertEquals(flags2.get(0), INTERSTITIAL_FLAG_TWO);
    }
}