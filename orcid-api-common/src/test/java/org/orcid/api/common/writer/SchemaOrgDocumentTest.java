package org.orcid.api.common.writer;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.jena.ext.com.google.common.collect.Sets;
import org.junit.Test;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument.SchemaOrgAddress;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument.SchemaOrgAffiliation;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument.SchemaOrgExternalID;
import org.orcid.api.common.writer.schemaorg.SchemaOrgDocument.SchemaOrgWork;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.Lists;

public class SchemaOrgDocumentTest {

    // annoying. With reverse google sees two funders with one fundee each, and
    // two works with one author each.
    // without the works and funding, it looks like a person.

    public static final String expectedDoc = "{\n" +
            "  \"@context\" : \"http://schema.org\",\n" +
            "  \"@type\" : \"Person\",\n" +
            "  \"@id\" : \"https://orcid.org/0000-0000-0000-000X\",\n" +
            "  \"mainEntityOfPage\" : \"https://orcid.org/0000-0000-0000-000X\",\n" +
            "  \"name\" : \"Joe McJoeface\",\n" +
            "  \"givenName\" : \"Joseph\",\n" +
            "  \"familyName\" : \"McJoeFace\",\n" +
            "  \"alternateName\" : \"Face\",\n" +
            "  \"address\" : [ {\n" +
            "    \"addressCountry\" : \"UK\",\n" +
            "    \"@type\" : \"PostalAddress\"\n" +
            "  }, {\n" +
            "    \"addressCountry\" : \"US\",\n" +
            "    \"@type\" : \"PostalAddress\"\n" +
            "  } ],\n" +
            "  \"alumniOf\" : [ {\n" +
            "    \"@type\" : \"Organization\",\n" +
            "    \"@id\" : \"https://grid.ac.uk/1\",\n" +
            "    \"name\" : \"Org1\",\n" +
            "    \"alternateName\" : \"DegreeTitle1\"\n" +
            "  }, {\n" +
            "    \"@type\" : \"Organization\",\n" +
            "    \"@id\" : \"https://lei.org/2222\",\n" +
            "    \"leiCode\" : \"2222\",\n" +
            "    \"name\" : \"Org2\",\n" +
            "    \"alternateName\" : \"DegreeTitle2\",\n" +
            "    \"identifier\" : {\n" +
            "      \"@type\" : \"PropertyValue\",\n" +
            "      \"propertyID\" : \"RINGGGOLD\",\n" +
            "      \"value\" : \"A\"\n" +
            "    },\n" +
            "    \"sameAs\" : \"http://grid.ac/B\"\n" +
            "  } ],\n" +
            "  \"affiliation\" : [ {\n" +
            "    \"@type\" : \"Organization\",\n" +
            "    \"@id\" : \"https://grid.ac.uk/3\",\n" +
            "    \"name\" : \"Org3\",\n" +
            "    \"alternateName\" : \"JobTitle1\"\n" +
            "  }, {\n" +
            "    \"@type\" : \"Organization\",\n" +
            "    \"name\" : \"Org4\",\n" +
            "    \"alternateName\" : \"JobTitle2\",\n" +
            "    \"identifier\" : {\n" +
            "      \"@type\" : \"PropertyValue\",\n" +
            "      \"propertyID\" : \"RINGGGOLD\",\n" +
            "      \"value\" : \"C\"\n" +
            "    }\n" +
            "  } ],\n" +
            "  \"@reverse\" : {\n" +
            "    \"funder\" : [ {\n" +
            "      \"@type\" : \"Organization\",\n" +
            "      \"@id\" : \"https://doi.org/5\",\n" +
            "      \"name\" : \"Org5\",\n" +
            "      \"alternateName\" : \"GrantTitle1\"\n" +
            "    }, {\n" +
            "      \"@type\" : \"Organization\",\n" +
            "      \"@id\" : \"https://doi.org/6\",\n" +
            "      \"leiCode\" : \"2222\",\n" +
            "      \"name\" : \"Org6\",\n" +
            "      \"alternateName\" : \"GrantTitle2\",\n" +
            "      \"identifier\" : {\n" +
            "        \"@type\" : \"PropertyValue\",\n" +
            "        \"propertyID\" : \"grant_number\",\n" +
            "        \"value\" : \"abcd\"\n" +
            "      }\n" +
            "    } ],\n" +
            "    \"creator\" : [ {\n" +
            "      \"@type\" : \"CreativeWork\",\n" +
            "      \"@id\" : \"https://doi.org/1\",\n" +
            "      \"name\" : \"WorkTitle1\"\n" +
            "    }, {\n" +
            "      \"@type\" : \"CreativeWork\",\n" +
            "      \"@id\" : \"https://doi.org/2\",\n" +
            "      \"name\" : \"WorkTitle2\",\n" +
            "      \"identifier\" : [ {\n" +
            "        \"@type\" : \"PropertyValue\",\n" +
            "        \"propertyID\" : \"isbn\",\n" +
            "        \"value\" : \"1234567890\"\n" +
            "      }, {\n" +
            "        \"@type\" : \"PropertyValue\",\n" +
            "        \"propertyID\" : \"pmc\",\n" +
            "        \"value\" : \"12345678\"\n" +
            "      } ],\n" +
            "      \"sameAs\" : \"http://worldcat.org/isbn/1234567890\"\n" +
            "    } ]\n" +
            "  },\n" +
            "  \"url\" : \"http://joe.com\",\n" +
            "  \"identifier\" : [ {\n" +
            "    \"@type\" : \"PropertyValue\",\n" +
            "    \"propertyID\" : \"scopusID\",\n" +
            "    \"value\" : \"123\"\n" +
            "  }, {\n" +
            "    \"@type\" : \"PropertyValue\",\n" +
            "    \"propertyID\" : \"researcherID\",\n" +
            "    \"value\" : \"456\"\n" +
            "  } ]\n" +
            "}";
    @Test
    public void testSerialisation() throws JsonGenerationException, JsonMappingException, IOException {
        SchemaOrgDocument doc = new SchemaOrgDocument();
        doc.id = "https://orcid.org/0000-0000-0000-000X";
        doc.mainEntityOfPage = doc.id;
        doc.name = "Joe McJoeface";
        doc.givenName = "Joseph";
        doc.familyName = "McJoeFace";
        doc.alternateName = Lists.newArrayList("Face");

        doc.address.add(new SchemaOrgAddress("UK"));
        doc.address.add(new SchemaOrgAddress("US"));

        doc.alumniOf.add(new SchemaOrgAffiliation("https://grid.ac.uk/1", "Org1", "DegreeTitle1", null));
        doc.alumniOf.add(new SchemaOrgAffiliation("https://lei.org/2222", "Org2", "DegreeTitle2", "2222", new SchemaOrgExternalID("RINGGGOLD", "A"),
                new SchemaOrgExternalID("grid", "http://grid.ac/B")));
        //add twice, second should be ignored
        doc.alumniOf.add(new SchemaOrgAffiliation("https://lei.org/2222", "Org2", "DegreeTitle2", "2222", new SchemaOrgExternalID("RINGGGOLD", "A"),
                new SchemaOrgExternalID("grid", "http://grid.ac/B")));

        doc.affiliation.add(new SchemaOrgAffiliation("https://grid.ac.uk/3", "Org3", "JobTitle1", null));
        // just a ringgold
        doc.affiliation.add(new SchemaOrgAffiliation(null, "Org4", "JobTitle2", null, new SchemaOrgExternalID("RINGGGOLD", "C")));

        doc.worksAndFunding.creator.add(new SchemaOrgWork("https://doi.org/1", "WorkTitle1", null));
        doc.worksAndFunding.creator.add(new SchemaOrgWork("https://doi.org/2", "WorkTitle2", Sets.newLinkedHashSet(Lists.newArrayList("http://worldcat.org/isbn/1234567890")),
                new SchemaOrgExternalID("isbn", "1234567890"), new SchemaOrgExternalID("pmc", "12345678")));

        doc.worksAndFunding.funder.add(new SchemaOrgAffiliation("https://doi.org/5", "Org5", "GrantTitle1", null));// fundref
        // fundref and lei
        doc.worksAndFunding.funder.add(new SchemaOrgAffiliation("https://doi.org/6", "Org6", "GrantTitle2", "2222", new SchemaOrgExternalID("grant_number", "abcd")));

        doc.url.add("http://joe.com");// just one, to test unwrapped arrays

        doc.identifier.add(new SchemaOrgExternalID("scopusID", "123"));
        doc.identifier.add(new SchemaOrgExternalID("researcherID", "456"));

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, true);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        StringWriter s = new StringWriter();
        objectMapper.writer().writeValue(s, doc);
        assertEquals(expectedDoc, s.toString());
    }
}
