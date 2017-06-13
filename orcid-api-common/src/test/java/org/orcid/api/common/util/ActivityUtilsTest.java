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
package org.orcid.api.common.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.orcid.jaxb.model.common_v2.Contributor;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.common_v2.Subtitle;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.TranslatedTitle;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.BulkElement;
import org.orcid.jaxb.model.record_v2.Citation;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.jaxb.model.record_v2.WorkContributors;
import org.orcid.jaxb.model.record_v2.WorkTitle;

public class ActivityUtilsTest {

    private static final String ORCID = "0000-0000-0000-0000";

    @Test
    public void setPathToActivityTest() {
        Work w = new Work();
        w.setPutCode(123456L);
        assertNull(w.getPath());
        ActivityUtils.setPathToActivity(w, ORCID);
        assertEquals("/" + ORCID + "/work/123456", w.getPath());
    }

    @Test
    public void setPathToEducationsTest() {
        Educations x = getEducations();
        ActivityUtils.setPathToEducations(x, ORCID);
        assertEquals("/" + ORCID + "/education/123", x.getSummaries().get(0).getPath());
    }

    @Test
    public void setPathToEmploymentsTest() {
        Employments x = getEmployments();
        ActivityUtils.setPathToEmployments(x, ORCID);
        assertEquals("/" + ORCID + "/employment/123", x.getSummaries().get(0).getPath());
    }

    @Test
    public void setPathToFundingsTest() {
        Fundings x = getFundings();
        ActivityUtils.setPathToFundings(x, ORCID);
        assertEquals("/" + ORCID + "/funding/123", x.getFundingGroup().get(0).getFundingSummary().get(0).getPath());
    }

    @Test
    public void setPathToWorksTest() {
        Works x = getWorks();
        ActivityUtils.setPathToWorks(x, ORCID);
        assertEquals("/" + ORCID + "/work/123", x.getWorkGroup().get(0).getWorkSummary().get(0).getPath());
    }

    @Test
    public void setPathToPeerReviewsTest() {
        PeerReviews x = getPeerReviews();
        ActivityUtils.setPathToPeerReviews(x, ORCID);
        assertEquals("/" + ORCID + "/peer-review/123", x.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());
    }

    @Test
    public void setPathToActivitiesSummaryTest() {
        ActivitiesSummary x = new ActivitiesSummary();
        x.setEducations(getEducations());
        x.setEmployments(getEmployments());
        x.setFundings(getFundings());
        x.setWorks(getWorks());
        x.setPeerReviews(getPeerReviews());
        ActivityUtils.setPathToActivity(x, ORCID);
        assertEquals("/" + ORCID + "/activities", x.getPath());
        assertEquals("/" + ORCID + "/education/123", x.getEducations().getSummaries().get(0).getPath());
        assertEquals("/" + ORCID + "/employment/123", x.getEmployments().getSummaries().get(0).getPath());
        assertEquals("/" + ORCID + "/funding/123", x.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPath());
        assertEquals("/" + ORCID + "/work/123", x.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        assertEquals("/" + ORCID + "/peer-review/123", x.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPath());
    }

    @Test
    public void cleanEmptyActivitiesSummaryTest() {
        WorkSummary s = getEmptyWorkSummary();
        ActivitiesSummary x = new ActivitiesSummary();
        Works w = new Works();
        WorkGroup g = new WorkGroup();
        g.getWorkSummary().add(s);
        w.getWorkGroup().add(g);
        x.setWorks(w);
        assertNotNull(x.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle());
        ActivityUtils.cleanEmptyFields(x);
        assertNull(x.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle());
    }

    @Test
    public void cleanWorksTest() {
        WorkSummary s = getEmptyWorkSummary();
        Works w = new Works();
        WorkGroup g = new WorkGroup();
        g.getWorkSummary().add(s);
        w.getWorkGroup().add(g);
        assertNotNull(w.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle());
        ActivityUtils.cleanEmptyFields(w);
        assertNull(w.getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTranslatedTitle());
    }

    @Test
    public void cleanWorkSummaryTest() {
        WorkSummary s = getEmptyWorkSummary();
        assertNotNull(s.getTitle().getTranslatedTitle());
        ActivityUtils.cleanEmptyFields(s);
        assertNull(s.getTitle().getTranslatedTitle());
    }

    @Test
    public void cleanWorkTest() {
        Work w = getEmptyWork();
        assertNotNull(w.getWorkTitle().getTranslatedTitle());
        assertNotNull(w.getWorkCitation());
        assertNotNull(w.getWorkContributors().getContributor().get(0).getCreditName());
        ActivityUtils.cleanEmptyFields(w);
        assertNull(w.getWorkTitle().getTranslatedTitle());
        assertNull(w.getWorkCitation());
        assertNull(w.getWorkContributors().getContributor().get(0).getCreditName());
    }

    @Test
    public void cleanBulkElementTest() {
        BulkElement b = getEmptyWork();
        assertNotNull(((Work) b).getWorkTitle().getTranslatedTitle());
        assertNotNull(((Work) b).getWorkCitation());
        assertNotNull(((Work) b).getWorkContributors().getContributor().get(0).getCreditName());
        ActivityUtils.cleanEmptyFields(b);
        assertNull(((Work) b).getWorkTitle().getTranslatedTitle());
        assertNull(((Work) b).getWorkCitation());
        assertNull(((Work) b).getWorkContributors().getContributor().get(0).getCreditName());
    }

    @Test
    public void cleanBulkWorkTest() {
        WorkBulk bulk = new WorkBulk();
        Work w = getEmptyWork();
        bulk.getBulk().add(w);
        assertNotNull(((Work) bulk.getBulk().get(0)).getWorkTitle().getTranslatedTitle());
        assertNotNull(((Work) bulk.getBulk().get(0)).getWorkCitation());
        assertNotNull(((Work) bulk.getBulk().get(0)).getWorkContributors().getContributor().get(0).getCreditName());
        ActivityUtils.cleanEmptyFields(bulk);
        assertNull(((Work) bulk.getBulk().get(0)).getWorkTitle().getTranslatedTitle());
        assertNull(((Work) bulk.getBulk().get(0)).getWorkCitation());
        assertNull(((Work) bulk.getBulk().get(0)).getWorkContributors().getContributor().get(0).getCreditName());
    }

    private Educations getEducations() {
        Educations x = new Educations();
        EducationSummary e = new EducationSummary();
        e.setPutCode(123L);
        x.getSummaries().add(e);
        return x;
    }

    private Employments getEmployments() {
        Employments x = new Employments();
        EmploymentSummary e = new EmploymentSummary();
        e.setPutCode(123L);
        x.getSummaries().add(e);
        return x;
    }

    private Fundings getFundings() {
        Fundings x = new Fundings();
        FundingGroup g = new FundingGroup();
        FundingSummary e = new FundingSummary();
        e.setPutCode(123L);
        g.getFundingSummary().add(e);
        x.getFundingGroup().add(g);
        return x;
    }

    private PeerReviews getPeerReviews() {
        PeerReviews x = new PeerReviews();
        PeerReviewGroup g = new PeerReviewGroup();
        PeerReviewSummary e = new PeerReviewSummary();
        e.setPutCode(123L);
        g.getPeerReviewSummary().add(e);
        x.getPeerReviewGroup().add(g);
        return x;
    }

    private Works getWorks() {
        Works x = new Works();
        WorkGroup g = new WorkGroup();
        WorkSummary e = new WorkSummary();
        e.setPutCode(123L);
        g.getWorkSummary().add(e);
        x.getWorkGroup().add(g);
        return x;
    }

    private WorkSummary getEmptyWorkSummary() {
        WorkSummary s = new WorkSummary();
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title(""));
        title.setSubtitle(new Subtitle(""));
        title.setTranslatedTitle(new TranslatedTitle(""));
        s.setTitle(title);
        return s;
    }

    private Work getEmptyWork() {
        Work w = new Work();
        // Title
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title(""));
        title.setSubtitle(new Subtitle(""));
        title.setTranslatedTitle(new TranslatedTitle(""));
        w.setWorkTitle(title);
        // Citation
        w.setWorkCitation(new Citation());
        WorkContributors wc = new WorkContributors();
        // Contributors
        Contributor c = new Contributor();
        c.setCreditName(new CreditName(""));
        wc.getContributor().add(c);
        w.setWorkContributors(wc);
        return w;
    }
}
