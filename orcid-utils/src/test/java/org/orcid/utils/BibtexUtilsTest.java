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
package org.orcid.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.jbibtex.ObjectResolutionException;
import org.junit.Test;

/**
 * 2011-2012 - Semantico Ltd.
 * 
 * @author Declan Newman (declan) Date: 05/10/2012
 */
public class BibtexUtilsTest {

    @Test
    public void testValidParse() throws Exception {
        // @formatter:off
        String citation = BibtexUtils.toCitation("@Article{Aravind:2011:NDB,\n" + "  author =       \"Alex A. Aravind and Wim H. Hesselink\",\n"
                + "  title =        \"Nonatomic dual bakery algorithm with bounded tokens\",\n" + "  journal =      \"Acta Informatica\",\n"
                + "  volume =       \"48\",\n" + "  number =       \"2\",\n" + "  pages =        \"67--96\",\n" + "  month =        \"2\",\n"
                + "  year =         \"2011\",\n" + "  CODEN =        \"AINFA2\",\n" + "  ISSN =         \"0001-5903 (print), 1432-0525 (electronic)\",\n"
                + "  ISSN-L =       \"0001-5903\"\n" + "}");
        // @formatter:on

        assertNotNull(citation);
        assertEquals("Alex A. Aravind and Wim H. Hesselink, (2011). \"Nonatomic dual bakery algorithm with bounded tokens\", Acta Informatica, vol. 48, no. 2, "
                + "pp. 67--96", citation);
    }

    @Test(expected = ObjectResolutionException.class)
    public void testInvalidVariable() throws Exception {
        // @formatter:off
        BibtexUtils.toCitation("@Article{Aravind:2011:NDB,\n" + "  author =       VARIABLE,\n"
                + "  title =        \"Nonatomic dual bakery algorithm with bounded tokens\",\n" + "  journal =      \"Acta Informatica\",\n" + "}");
        // @formatter:on
    }

    @Test
    public void testValidityOfBadBibtex() {
        // Extra { after @article
        assertFalse(BibtexUtils
                .isValid("@article{{10.1371/journal.pgen.1002992,\n"
                        + "\n"
                        + "author = {Amambua-Ngwa, , Alfred AND Tetteh, , Kevin K. A. AND Manske, , Magnus AND Gomez-Escobar, , Natalia AND Stewart, , Lindsay B. AND Deerhake, , M. Elizabeth AND Cheeseman, , Ian H. AND Newbold, , Christopher I. AND Holder, , Anthony A. AND Knuepfer, , Ellen AND Janha, , Omar AND Jallow, , Muminatou AND Campino, , Susana AND MacInnis, , Bronwyn AND Kwiatkowski, , Dominic P. AND Conway, , David J.},\n"
                        + "\n"
                        + "journal = {PLoS Genet},\n"
                        + "\n"
                        + "publisher = {Public Library of Science},\n"
                        + "\n"
                        + "title = {Population Genomic Scan for Candidate Signatures of Balancing Selection to Guide Antigen Characterization in Malaria Parasites},\n"
                        + "\n"
                        + "year = {2012},\n"
                        + "\n"
                        + "month = {11},\n"
                        + "\n"
                        + "volume = {8},\n"
                        + "\n"
                        + "url = {http://dx.doi.org/10.1371%2Fjournal.pgen.1002992},\n"
                        + "\n"
                        + "pages = {e1002992},\n"
                        + "\n"
                        + "abstract = {&lt;title&gt;Author Summary&lt;/title&gt;&lt;p&gt;The memory component of acquired immune responses selects for distinctive patterns of polymorphism in genes encoding important target antigens of pathogens. These are detectable by surveying for evidence of balancing selection, as previously illustrated in analyses of genes encoding malaria parasite antigens that are candidate targets of naturally acquired immunity. For a comprehensive screen to discover targets of immunity in the major human malaria parasite &lt;italic&gt;Plasmodium falciparum&lt;/italic&gt;, an endemic population in West Africa was sampled and genome sequence data obtained from 65 clinical isolates, allowing analysis of polymorphism in almost all protein-coding genes. Antigen genes previously studied by capillary re-sequencing in independent population samples had highly concordant indices in the genome-wide analysis here, and this has identified other genes with stronger evidence of balancing selection, now prioritized for functional study and potential vaccine candidacy. The statistical signatures consistent with such selection were particularly common in genes with peak expression at the stage that invades erythrocytes, and members of several gene families were represented. The strongest signature was in the &lt;italic&gt;msp3&lt;/italic&gt;-like gene PF10_0355, so we studied the transcript and protein product in parasites, revealing an unexpected pattern of phase variable expression. Variation in expression of polymorphic antigens under balancing selection may be more common than previously thought, requiring further study to assess vaccine candidacy.&lt;/p&gt;},\n"
                        + "\n" + "number = {11},\n" + "\n" + "doi = {10.1371/journal.pgen.1002992}\n" + "\n" + "}"));
        assertFalse(BibtexUtils.isValid("@article{RoeverEtAl2007a,\n\nauthor = {R\\"));
    }

}
