package org.orcid.api.common.security.filter.impl;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.orcid.core.security.visibility.filter.impl.VisibilityFilterV2Impl;
import org.orcid.jaxb.model.record.Visibility;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;

/**
 * 
 * @author Will Simpson
 *
 */
public class VisibilityFilterV2ImplTest {

    private Unmarshaller unmarshaller;

    private VisibilityFilterV2Impl visibilityFilter = new VisibilityFilterV2Impl();

    @Before
    public void before() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ActivitiesSummary.class);
        unmarshaller = context.createUnmarshaller();
    }
    
    @Test
    public void testUnmarshall() throws JAXBException, IOException{
        ActivitiesSummary activitiesSummary = getActivitiesSummary("/activities-protected-full-latest.xml");
        String expected = IOUtils.toString(getClass().getResourceAsStream("/activities-protected-full-latest.xml"), "UTF-8");
        assertEquals(expected, activitiesSummary.toString());
    }

    @Ignore("Not yet implemented")
    @Test
    public void testFilterActivities() throws JAXBException {
        ActivitiesSummary activitiesSummary = getActivitiesSummary("/activities-protected-full-latest.xml");
        ActivitiesSummary expectedActivitiesSummary = getActivitiesSummary("/activities-stripped-latest.xml");
        visibilityFilter.filter(activitiesSummary, Visibility.PUBLIC);
        assertEquals(expectedActivitiesSummary.toString(), activitiesSummary.toString());
    }

    private ActivitiesSummary getActivitiesSummary(String path) throws JAXBException {
        return (ActivitiesSummary) unmarshaller.unmarshal(getClass().getResourceAsStream(path));
    }

}
