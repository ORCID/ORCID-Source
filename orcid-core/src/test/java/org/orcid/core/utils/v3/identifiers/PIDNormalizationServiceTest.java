package org.orcid.core.utils.v3.identifiers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.utils.v3.identifiers.normalizers.ArxivNormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.BibcodeNormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.CaseSensitiveNormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.CurieNormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.DOINormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.ISBNNormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.ISSNNormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.Normalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.PMCNormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.PMIDNormalizer;
import org.orcid.core.utils.v3.identifiers.normalizers.URINormalizer;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.pojo.IdentifierType;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Normalisation is pure string work, so this runs on mocks.
 *
 * <p>
 * The normalizers themselves are real instances, not mocks. They are the
 * behaviour under test: a mocked {@link DOINormalizer} would return whatever
 * this test told it to and the assertions below would hold no matter what the
 * regexes did. The list is assembled by hand here because in production Spring
 * collects every {@code @Component} implementing {@link Normalizer} into
 * {@code PIDNormalizationService.normalizers}; all ten are listed so the
 * composition under test matches the one that runs. {@code init()} sorts them
 * with {@code AnnotationAwareOrderComparator}, exactly as it does in the
 * context, so declaration order here is irrelevant.
 *
 * <p>
 * The one thing that is faked is {@link IdentifierTypeManager}, which reads the
 * {@code identifier_type} table. That table is reference data rather than
 * behaviour: what the service takes from it is the set of known API type names
 * and each type's {@code case_sensitive} flag, which
 * {@link CaseSensitiveNormalizer} (highest precedence, applies to every type)
 * consults before any type-specific normalizer runs. The flags below match the
 * shipped rows: {@code add-case-to-id-types.xml} adds the column with
 * {@code DEFAULT false}, and the changesets under {@code db/updates/} that set
 * it true do so for ARK, BIBCODE, CGN, EMDB, EMPIAR, ETHOS, K10PLUS, OL, RRID
 * and URI. Three of those -- BIBCODE, RRID and URI -- are among the types a
 * registered normalizer claims, so they are the three carrying true below. That
 * is why bibcode keeps its uppercase "A" while the doi and agr values are
 * lowercased. What this test no longer proves is that those rows are still what
 * the database holds.
 */
@RunWith(MockitoJUnitRunner.class)
public class PIDNormalizationServiceTest {

    @Mock
    private IdentifierTypeManager idman;

    @InjectMocks
    private PIDNormalizationService norm = new PIDNormalizationService();

    @Before
    public void before() {
        // The four types the tests below normalise, plus every type one of the
        // registered normalizers claims in canHandle(). Both are needed:
        // init() builds map.get(type).add(n) for each claimed type and would
        // throw a NullPointerException on a type the id manager did not report.
        Map<String, IdentifierType> idTypes = new HashMap<String, IdentifierType>();
        idTypes.put("agr", identifierType("agr", false));
        idTypes.put("doi", identifierType("doi", false));
        idTypes.put("isbn", identifierType("isbn", false));
        idTypes.put("bibcode", identifierType("bibcode", true));
        idTypes.put("arxiv", identifierType("arxiv", false));
        idTypes.put("issn", identifierType("issn", false));
        idTypes.put("pmc", identifierType("pmc", false));
        idTypes.put("pmid", identifierType("pmid", false));
        idTypes.put("rrid", identifierType("rrid", true));
        idTypes.put("uri", identifierType("uri", true));
        when(idman.fetchIdentifierTypesByAPITypeName(Locale.ENGLISH)).thenReturn(idTypes);

        CaseSensitiveNormalizer caseSensitiveNormalizer = new CaseSensitiveNormalizer();
        ReflectionTestUtils.setField(caseSensitiveNormalizer, "idman", idman);

        List<Normalizer> normalizers = new ArrayList<Normalizer>(
                Arrays.asList(caseSensitiveNormalizer, new ArxivNormalizer(), new BibcodeNormalizer(), new CurieNormalizer(), new DOINormalizer(),
                        new ISBNNormalizer(), new ISSNNormalizer(), new PMCNormalizer(), new PMIDNormalizer(), new URINormalizer()));
        ReflectionTestUtils.setField(norm, "normalizers", normalizers);

        norm.init();
    }

    private IdentifierType identifierType(String name, boolean caseSensitive) {
        IdentifierType type = new IdentifierType();
        type.setName(name);
        type.setCaseSensitive(caseSensitive);
        return type;
    }

    @Test
    public void checkCaseNormalized(){
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("agr");
        id1.setValue("UPPER");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));
        
        ExternalID id2 = new ExternalID();
        id2.setRelationship(Relationship.SELF);
        id2.setType("agr");
        id2.setValue("upper");
        id2.setNormalized(new TransientNonEmptyString("upper"));   
        
        assertEquals(id1,id2);
    }
    
    @Test
    public void checkDOIAndCaseNormalized(){
        ExternalID normed = new ExternalID();
        normed.setRelationship(Relationship.SELF);
        normed.setType("doi");
        normed.setValue("10.1/upper");
        normed.setNormalized(new TransientNonEmptyString("10.1/upper"));  //everything should normalize to this.       

        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("doi");
        id1.setValue("https://dx.doi.org/10.1/UPPER");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));
        assertEquals(id1,normed);
        
        id1.setValue("http://doi.org/10.1/UPPER");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));        
        assertEquals(id1,normed);
        
        id1.setValue("10.1/UPPER");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));
        assertEquals(id1,normed);

    }
    
    @Test
    public void checkISBNAndCaseNormalized(){
        ExternalID normed = new ExternalID();
        normed.setRelationship(Relationship.SELF);
        normed.setType("isbn");
        normed.setValue("ISBN: 123-456-7-89x junk");
        normed.setNormalized(new TransientNonEmptyString("123456789X"));  //everything should normalize to this.       
        
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("isbn");
        id1.setValue("ISBN: 123-456-7-89x junk");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));        
        assertEquals(normed,id1);
    }
    
    @Test
    public void checkBibcodeAndCaseNormalized(){
        ExternalID normed = new ExternalID();
        normed.setRelationship(Relationship.SELF);
        normed.setType("bibcode");
        normed.setValue(" 123456789.A23456789 ");
        normed.setNormalized(new TransientNonEmptyString("123456789.A23456789"));  //everything should normalize to this.       
        
        ExternalID id1 = new ExternalID();
        id1.setRelationship(Relationship.SELF);
        id1.setType("bibcode");
        id1.setValue(" 123456789.A23456789 ");
        id1.setNormalized(new TransientNonEmptyString(norm.normalise(id1.getType(), id1.getValue())));        
        assertEquals(normed,id1);
    }

}
