/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.forms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.junit.Test;
import org.orcid.core.crossref.CrossRefMetadata;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkType;

/**
 * 
 * @author Will Simpson
 * 
 */
public class CurrentWorkTest {

    @Test
    public void testGetOricdWork() throws IOException {
        CurrentWork currentWork = new CurrentWork();
        currentWork.setVisibility(Visibility.PUBLIC.value());
        currentWork.setTitle("Neanderthal Man");
        currentWork.setSubtitle("Being oldfashioned");
        currentWork.setDescription("A book about neanderthal men");
        currentWork.setCitation("Carberry, Josiah S., John W. Spaeth, and Paulo Di Fillipo. \"Neanderthal Man.\" Book of Prehistory. N.p.: Brown UP, "
                + "2012. 450-70. 09 Oct. 2012. Web.");
        currentWork.setCitationType(CitationType.FORMATTED_UNSPECIFIED.value());
        currentWork.setWorkType(WorkType.BOOK.value());
        currentWork.setYear("2012");
        currentWork.setMonth("10");
        currentWork.setDay("09");
        CurrentWorkExternalId extId = new CurrentWorkExternalId();
        extId.setType(WorkExternalIdentifierType.DOI.value());
        extId.setId("10.5555/12345ABCDE");
        List<CurrentWorkExternalId> extIds = new ArrayList<CurrentWorkExternalId>();
        extIds.add(extId);
        currentWork.setUrl("http://neanderthalmen.com");
        currentWork.setCurrentWorkExternalIds(extIds);
        CurrentWorkContributor currentWorkContributor = new CurrentWorkContributor();
        currentWorkContributor.setOrcid("4444-4444-4444-4449");
        currentWorkContributor.setCreditName("J. S. Carberry");
        currentWorkContributor.setEmail("jscarberry@semantico.com");
        currentWorkContributor.setRole("author");
        currentWorkContributor.setSequence("first");
        List<CurrentWorkContributor> currentWorkContributors = new ArrayList<CurrentWorkContributor>();
        currentWorkContributors.add(currentWorkContributor);
        currentWork.setCurrentWorkContributors(currentWorkContributors);

        OrcidWork orcidWork = currentWork.getOrcidWork();

        String expected = IOUtils.toString(getClass().getResourceAsStream("example_current_work.xml"));
        assertEquals(expected, orcidWork.toString());
    }

    @Test
    public void testCreateFromOrcidWork() throws JAXBException {
    	//Test work without language fields
        JAXBContext context = JAXBContext.newInstance(OrcidWork.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        OrcidWork orcidWork = (OrcidWork) unmarshaller.unmarshal(getClass().getResourceAsStream("example_current_work.xml"));
        assertNotNull(orcidWork);

        CurrentWork currentWork = new CurrentWork(orcidWork);
        assertEquals("Neanderthal Man", currentWork.getTitle());
        assertEquals("Being oldfashioned", currentWork.getSubtitle());
        assertEquals("A book about neanderthal men", currentWork.getDescription());
        assertEquals(
                "Carberry, Josiah S., John W. Spaeth, and Paulo Di Fillipo. \"Neanderthal Man.\" Book of Prehistory. N.p.: Brown UP, 2012. 450-70. 09 Oct. 2012. Web.",
                currentWork.getCitation());
        assertEquals(CitationType.FORMATTED_UNSPECIFIED.value(), currentWork.getCitationType());
        assertEquals("2012", currentWork.getYear());
        assertEquals("10", currentWork.getMonth());
        assertEquals("09", currentWork.getDay());
        assertNotNull(currentWork.getCurrentWorkExternalIds());
        assertEquals(1, currentWork.getCurrentWorkExternalIds().size());
        assertEquals("doi", currentWork.getCurrentWorkExternalIds().get(0).getType());
        assertEquals("10.5555/12345ABCDE", currentWork.getCurrentWorkExternalIds().get(0).getId());
        assertEquals("http://neanderthalmen.com", currentWork.getUrl());
        assertNotNull(currentWork.getCurrentWorkContributors());
        assertEquals(1, currentWork.getCurrentWorkContributors().size());
        assertEquals("4444-4444-4444-4449", currentWork.getCurrentWorkContributors().get(0).getOrcid());
        assertEquals("J. S. Carberry", currentWork.getCurrentWorkContributors().get(0).getCreditName());
        assertEquals("first", currentWork.getCurrentWorkContributors().get(0).getSequence());
        assertEquals("author", currentWork.getCurrentWorkContributors().get(0).getRole());
        
        //Test work with language fields        
        unmarshaller = context.createUnmarshaller();
        orcidWork = (OrcidWork) unmarshaller.unmarshal(getClass().getResourceAsStream("example_current_work_with_translated_title.xml"));
        assertNotNull(orcidWork);

        currentWork = new CurrentWork(orcidWork);
        assertEquals("Neanderthal Man", currentWork.getTitle());
        assertEquals("Being oldfashioned", currentWork.getSubtitle());
        assertEquals("A book about neanderthal men", currentWork.getDescription());
        assertEquals(
                "Carberry, Josiah S., John W. Spaeth, and Paulo Di Fillipo. \"Neanderthal Man.\" Book of Prehistory. N.p.: Brown UP, 2012. 450-70. 09 Oct. 2012. Web.",
                currentWork.getCitation());
        assertEquals(CitationType.FORMATTED_UNSPECIFIED.value(), currentWork.getCitationType());
        assertEquals("2012", currentWork.getYear());
        assertEquals("10", currentWork.getMonth());
        assertEquals("09", currentWork.getDay());
        assertNotNull(currentWork.getCurrentWorkExternalIds());
        assertEquals(1, currentWork.getCurrentWorkExternalIds().size());
        assertEquals("doi", currentWork.getCurrentWorkExternalIds().get(0).getType());
        assertEquals("10.5555/12345ABCDE", currentWork.getCurrentWorkExternalIds().get(0).getId());
        assertEquals("http://neanderthalmen.com", currentWork.getUrl());
        assertNotNull(currentWork.getCurrentWorkContributors());
        assertEquals(1, currentWork.getCurrentWorkContributors().size());
        assertEquals("4444-4444-4444-4449", currentWork.getCurrentWorkContributors().get(0).getOrcid());
        assertEquals("J. S. Carberry", currentWork.getCurrentWorkContributors().get(0).getCreditName());
        assertEquals("first", currentWork.getCurrentWorkContributors().get(0).getSequence());
        assertEquals("author", currentWork.getCurrentWorkContributors().get(0).getRole());
        assertEquals("en_US", currentWork.getLanguageCode());
        assertEquals("Neanderthal Man - The journal title", currentWork.getJournalTitle());
        assertEquals("Hombre neandertal", currentWork.getTranslatedTitle());
        assertEquals("es_CR", currentWork.getTranslatedTitleLanguageCode());
    }

    @Test
    public void testCreateFromCrossRefMetaData() throws JsonParseException, JsonMappingException, IOException {
        CrossRefMetadata metadata = new CrossRefMetadata();
        String doi = "10.1017/CBO9780511523816.003";
        metadata.setDoi(doi);
        String title = "Spanish agriculture: the long view";
        metadata.setTitle(title);
        String fullCitation = "Simpson, J &amp; Simpson, J, 2009, , Cambridge University Press, Cambridge.";
        metadata.setFullCitation(fullCitation);
        String shortCitation = "(Simpson & Simpson 2009, pp. 13-32)";
        metadata.setShortCitation(shortCitation);
        String coins = "ctx_ver=Z39.88-2004&rft_val_fmt=info:ofi/fmt:kev:mtx:journal&rft_id=info:doi/10.1017/CBO9780511523816.003&rtf.genre=book&rtf.spage=13&rtf.epage=32&rtf.date=2009&rtf.aulast=Simpson&rtf.aufirst=James&rtf.auinit=J&rtf.btitle=null";
        metadata.setCoins(coins);

        CurrentWork currentWork = new CurrentWork(metadata);

        assertNotNull(currentWork.getCurrentWorkExternalIds());
        assertEquals(1, currentWork.getCurrentWorkExternalIds().size());
        assertEquals(WorkExternalIdentifierType.DOI.value(), currentWork.getCurrentWorkExternalIds().get(0).getType());
        assertEquals(doi, currentWork.getCurrentWorkExternalIds().get(0).getId());
        assertEquals(title, currentWork.getTitle());
        assertEquals(fullCitation, currentWork.getCitation());
        assertEquals("2009", currentWork.getYear());
        assertNotNull(currentWork.getCurrentWorkContributors());
        assertEquals(1, currentWork.getCurrentWorkContributors().size());
        assertEquals("Simpson James", currentWork.getCurrentWorkContributors().get(0).getCreditName());
        assertEquals(ContributorRole.AUTHOR.value(), currentWork.getCurrentWorkContributors().get(0).getRole());
        assertEquals(SequenceType.FIRST.value(), currentWork.getCurrentWorkContributors().get(0).getSequence());
    }

}
