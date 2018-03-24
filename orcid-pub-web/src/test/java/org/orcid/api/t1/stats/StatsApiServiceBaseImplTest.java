package org.orcid.api.t1.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.t1.stats.delegator.StatsApiServiceDelegator;
import org.orcid.core.manager.read_only.StatisticsManagerReadOnly;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;
import org.orcid.statistics.dao.StatisticsDao;
import org.orcid.statistics.jpa.entities.StatisticKeyEntity;
import org.orcid.statistics.jpa.entities.StatisticValuesEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml", "classpath:orcid-t1-security-context.xml" })
@SuppressWarnings("deprecation")
public class StatsApiServiceBaseImplTest {

    @Resource(name = "statsApiServiceDelegator")
    StatsApiServiceDelegator serviceDelegator;

    // the class that contains the thing we're mocking
    @Resource
    StatisticsManagerReadOnly statsManagerReadOnly;

    StatisticsDao statisticsDao = mock(StatisticsDao.class);

    @Before
    public void init() {
        // create our mock data
        List<StatisticValuesEntity> statsTimelineValues = new ArrayList<StatisticValuesEntity>();
        List<StatisticValuesEntity> statsSummaryValues = new ArrayList<StatisticValuesEntity>();

        StatisticValuesEntity a = new StatisticValuesEntity();
        a.setId(1l);
        a.setStatisticName(StatisticsEnum.KEY_LIVE_IDS.value());
        a.setStatisticValue(100l);
        StatisticKeyEntity akey = new StatisticKeyEntity();
        akey.setGenerationDate(new Date(2000, 1, 1));
        akey.setId(200L);
        a.setKey(akey);

        StatisticValuesEntity b = new StatisticValuesEntity();
        b.setId(1l);
        b.setStatisticName(StatisticsEnum.KEY_LIVE_IDS.value());
        b.setStatisticValue(101l);
        StatisticKeyEntity bkey = new StatisticKeyEntity();
        bkey.setGenerationDate(new Date(1999, 1, 1));
        bkey.setId(201L);
        b.setKey(bkey);

        StatisticValuesEntity c = new StatisticValuesEntity();
        c.setId(1l);
        c.setStatisticName(StatisticsEnum.KEY_NUMBER_OF_WORKS.value());
        c.setStatisticValue(102l);
        c.setKey(akey);

        statsTimelineValues.add(a);
        statsTimelineValues.add(b);
        statsSummaryValues.add(a);
        statsSummaryValues.add(c);

        // mock the methods used
        when(statisticsDao.getLatestKey()).thenReturn(akey);
        when(statisticsDao.getStatistic(StatisticsEnum.KEY_LIVE_IDS.value())).thenReturn(statsTimelineValues);
        when(statisticsDao.getStatistic(200l)).thenReturn(statsSummaryValues);

        // mock the methods used
        StatisticKeyEntity key200 = new StatisticKeyEntity();
        key200.setId(200L);
        key200.setGenerationDate(new Date(2000, 1, 1));
        
        StatisticKeyEntity key201 = new StatisticKeyEntity();
        key201.setId(201L);
        key201.setGenerationDate(new Date(1999, 1, 1));
        
        when(statisticsDao.getKey(200L)).thenReturn(key200);
        when(statisticsDao.getKey(201L)).thenReturn(key201);
        
        TargetProxyHelper.injectIntoProxy(statsManagerReadOnly, "statisticsDaoReadOnly", statisticsDao);
        
        // setup security context
        ArrayList<GrantedAuthority> roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        Authentication auth = new AnonymousAuthenticationToken("anonymous", "anonymous", roles);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testViewStatsSummary() {
        assertEquals(200, serviceDelegator.getStatsSummary().getStatus());
        StatisticsSummary s = (StatisticsSummary) serviceDelegator.getStatsSummary().getEntity();
        assertEquals(s.getDate(), new Date(2000, 1, 1));
        assertEquals(s.getStatistics().size(), 2);
        assertEquals((long) s.getStatistics().get(StatisticsEnum.KEY_LIVE_IDS.value()), 100l);
        assertEquals((long) s.getStatistics().get(StatisticsEnum.KEY_NUMBER_OF_WORKS.value()), 102l);
    }

    @Test
    public void testViewStatsTimeline() {                        
        assertNotNull(serviceDelegator.getStatsSummary());        
        assertEquals(200, serviceDelegator.getStatsSummary().getStatus());
        serviceDelegator.updateToLatestStatisticsTimeline();
        Response r = serviceDelegator.getStatsTimeline(StatisticsEnum.KEY_LIVE_IDS);
        assertEquals(Response.Status.OK.getStatusCode(), r.getStatus());  
        
        StatisticsTimeline s = (StatisticsTimeline) r.getEntity();        
        assertNotNull(s);
        assertNotNull(s.getStatisticName());
        assertEquals(s.getStatisticName(), StatisticsEnum.KEY_LIVE_IDS.value());
        assertEquals(s.getTimeline().size(), 2);
        Long time1 = new Date(1999, 1, 1).getTime();
        assertEquals((long) s.getTimeline().get(time1), 101l);
        Long time2 = new Date(2000, 1, 1).getTime();
        assertEquals((long) s.getTimeline().get(time2), 100l);
    }
    
    @Test
    public void testEnumAndToStringListMatch(){
        StatisticsEnum[] it = StatisticsEnum.values();
        String list = it[0].value();
        for (int i=1;i<it.length;i++){
            list += ","+it[i].value();
        }
        assertEquals(StatisticsEnum.allowableSwaggerValues, list);
    }
    
}
