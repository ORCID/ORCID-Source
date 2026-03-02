package org.orcid.core.manager.impl;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.ActivitiesSummaryManager;
import org.orcid.core.manager.DOIManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.manager.read_only.RecordNameManagerReadOnly;
import org.orcid.jaxb.model.common_v2.PublicationDate;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.Year;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Citation;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.jaxb.model.record_v2.WorkType;

@RunWith(MockitoJUnitRunner.class)
public class BibtexManagerTest {

    private static final String ORCID = "0000-0000-0000-0003";

    @InjectMocks
    private BibtexManagerImpl bibtexManager;

    @Mock
    private ActivitiesSummaryManager activitiesManager;

    @Mock
    private WorkManager workManager;

    @Mock
    private DOIManager doiManager;

    @Mock
    private RecordNameManagerReadOnly recordNameManagerReadOnly;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(recordNameManagerReadOnly.fetchDisplayableCreditName(eq(ORCID))).thenReturn("Credit Name");
    }

    @Test
    public void testGenerateBibtex() {
        ActivitiesSummary summary = new ActivitiesSummary();
        Works works = new Works();
        List<WorkGroup> groups = new ArrayList<>();

        // Work 15: SELF PRIVATE
        WorkGroup group15 = new WorkGroup();
        WorkSummary summary15 = new WorkSummary();
        summary15.setPutCode(15L);
        group15.getWorkSummary().add(summary15);
        groups.add(group15);

        // Work 14: SELF LIMITED
        WorkGroup group14 = new WorkGroup();
        WorkSummary summary14 = new WorkSummary();
        summary14.setPutCode(14L);
        group14.getWorkSummary().add(summary14);
        groups.add(group14);

        works.getWorkGroup().addAll(groups);
        summary.setWorks(works);

        when(activitiesManager.getActivitiesSummary(eq(ORCID))).thenReturn(summary);

        Work work15 = createWork(15L, "SELF PRIVATE", "5");
        Work work14 = createWork(14L, "SELF LIMITED", "4");

        when(workManager.getWork(eq(ORCID), eq(15L))).thenReturn(work15);
        when(workManager.getWork(eq(ORCID), eq(14L))).thenReturn(work14);

        String bib = bibtexManager.generateBibtexReferenceList(ORCID);
        Assert.assertTrue(bib.startsWith("@article{Credit_Name"));
        Assert.assertTrue(bib.contains(",\ntitle={SELF PRIVATE},\nauthor={Credit Name},\ndoi={5},\nurl={http://doi.org/5},\nyear={2016}\n}"));
        Assert.assertTrue(bib.contains(",\ntitle={SELF LIMITED},\nauthor={Credit Name},\ndoi={4},\nurl={http://doi.org/4},\nyear={2016}\n}"));
        Assert.assertTrue(bib.endsWith("year={2016}\n}"));
    }

    private Work createWork(Long putCode, String titleStr, String doiValue) {
        Work w = new Work();
        w.setPutCode(putCode);
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title(titleStr));
        w.setWorkTitle(title);
        w.setWorkType(WorkType.JOURNAL_ARTICLE);

        ExternalIDs ids = new ExternalIDs();
        ExternalID id = new ExternalID();
        id.setType("doi");
        id.setValue(doiValue);
        id.setRelationship(Relationship.SELF);
        ids.getExternalIdentifier().add(id);
        w.setWorkExternalIdentifiers(ids);

        PublicationDate date = new PublicationDate();
        date.setYear(new Year(2016));
        w.setPublicationDate(date);

        return w;
    }

    @Test
    public void testGenerateBibtexForSingleWorkFromCitationField() {
        Work w = new Work();
        Citation c = new Citation();
        c.setWorkCitationType(CitationType.BIBTEX);
        c.setCitation("HELLO");
        w.setWorkCitation(c);
        String bib = bibtexManager.generateBibtex(ORCID, w);
        Assert.assertEquals("HELLO", bib);
    }

    @Test
    public void testGenerateBibtexForSingleWorkEsaped() {
        Work w = new Work();
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("Escapes θ à À È © ë Ö ì"));
        w.setWorkTitle(title);
        w.setWorkType(WorkType.JOURNAL_ARTICLE);
        w.setPutCode(100L);
        String bib = bibtexManager.generateBibtex(ORCID, w);
        Assert.assertEquals("@article{Credit_Name100,\ntitle={Escapes \\texttheta {\\`a} \\`{A} \\`{E} \\textcopyright {\\\"e} {\\\"O} {\\`i}},\nauthor={Credit Name}\n}", bib);
    }

    @Test
    public void testDOIManagerIsInvoked() {
        when(doiManager.fetchDOIBibtex("111")).thenReturn("OK");
        Work w = new Work();
        w.setWorkExternalIdentifiers(new ExternalIDs());
        ExternalID id = new ExternalID();
        id.setType("doi");
        id.setValue("111");
        w.getExternalIdentifiers().getExternalIdentifier().add(id);
        String bib = bibtexManager.generateBibtex(ORCID, w);
        Assert.assertEquals("OK", bib);
    }
}
