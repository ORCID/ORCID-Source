package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.core.utils.Actors;
import org.orcid.frontend.web.pagination.Page;
import org.orcid.frontend.web.pagination.ResearchResourcePaginator;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.pojo.ResearchResourceGroupPojo;

/**
 * The mocked half of ResearchResourceControllerTest, which stays a database test
 * because everything it asserts -- the grouping and ordering of a page, the
 * display index that picks a group's default, and the orcid predicate that stops
 * one record deleting another's resource -- lives in
 * ResearchResourcePaginator and ResearchResourceDaoImpl.
 *
 * What is left over is genuinely the controller's: splitting a comma separated
 * id string into put codes, parsing the visibility, and passing the signed in
 * record's orcid rather than one taken from the request. That is what these
 * tests cover.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ResearchResourcesControllerTest {

    private static final String USER_ORCID = "4444-4444-4444-4446";

    @Mock
    private ResearchResourceManager researchResourceManager;

    @Mock
    private ResearchResourcePaginator paginator;

    @InjectMocks
    private ResearchResourcesController controller = new ResearchResourcesController();

    @Captor
    private ArgumentCaptor<ArrayList<Long>> idsCaptor;

    @Before
    public void before() {
        Actors.user(USER_ORCID);
    }

    @After
    public void after() {
        Actors.clear();
    }

    @Test
    public void removeWorkSplitsTheIdsAndScopesTheDeleteToTheSignedInRecord() {
        List<Long> returned = controller.removeWork("3,4,5");

        assertEquals(Arrays.asList(3L, 4L, 5L), returned);
        verify(researchResourceManager).removeResearchResources(eq(USER_ORCID), idsCaptor.capture());
        assertEquals(Arrays.asList(3L, 4L, 5L), idsCaptor.getValue());
    }

    @Test
    public void updateVisibilitysParsesTheVisibilityAndScopesTheUpdate() {
        controller.updateVisibilitys("2", "private");

        ArgumentCaptor<Visibility> visibility = ArgumentCaptor.forClass(Visibility.class);
        verify(researchResourceManager).updateVisibilities(eq(USER_ORCID), idsCaptor.capture(), visibility.capture());
        assertEquals(Arrays.asList(2L), idsCaptor.getValue());
        assertEquals(Visibility.PRIVATE, visibility.getValue());
    }

    @Test
    public void updateToMaxDisplayIsScopedToTheSignedInRecord() {
        when(researchResourceManager.updateToMaxDisplay(USER_ORCID, 1L)).thenReturn(true);

        assertTrue(controller.updateToMaxDisplay(1L));
        verify(researchResourceManager).updateToMaxDisplay(USER_ORCID, 1L);
    }

    @Test
    public void researchResourcePageIsAskedForTheSignedInRecord() {
        Page<ResearchResourceGroupPojo> page = new Page<ResearchResourceGroupPojo>();
        when(paginator.getPage(USER_ORCID, 0, 50, false, "title", true)).thenReturn(page);

        assertEquals(page, controller.getresearchResourcePage(50, 0, "title", true));
        verify(paginator).getPage(USER_ORCID, 0, 50, false, "title", true);
    }
}
