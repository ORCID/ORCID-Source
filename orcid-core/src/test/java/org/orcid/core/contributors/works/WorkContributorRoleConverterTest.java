package org.orcid.core.contributors.works;

import static org.junit.Assert.*;

import org.junit.Test;

public class WorkContributorRoleConverterTest {

    WorkContributorRoleConverter converter = new WorkContributorRoleConverter();

    @Test
    public void testGetDbValueFromAPIValue() {
        // legacy
        assertEquals("AUTHOR", converter.toDBRole("author"));
        assertEquals("ASSIGNEE", converter.toDBRole("assignee"));
        assertEquals("EDITOR", converter.toDBRole("editor"));
        assertEquals("CHAIR_OR_TRANSLATOR", converter.toDBRole("chair-or-translator"));
        assertEquals("CO_INVESTIGATOR", converter.toDBRole("co-investigator"));
        assertEquals("CO_INVENTOR", converter.toDBRole("co-inventor"));
        assertEquals("GRADUATE_STUDENT", converter.toDBRole("graduate-student"));
        assertEquals("OTHER_INVENTOR", converter.toDBRole("other-inventor"));
        assertEquals("PRINCIPAL_INVESTIGATOR", converter.toDBRole("principal-investigator"));
        assertEquals("POSTDOCTORAL_RESEARCHER", converter.toDBRole("postdoctoral-researcher"));
        assertEquals("SUPPORT_STAFF", converter.toDBRole("support-staff"));

        // credit
        assertEquals("CONCEPTUALIZATION", converter.toDBRole("conceptualization"));
        assertEquals("DATA_CURATION", converter.toDBRole("data curation"));
        assertEquals("EDITOR", converter.toDBRole("editor"));
        assertEquals("FORMAL_ANALYSIS", converter.toDBRole("formal analysis"));
        assertEquals("FUNDING_ACQUISITION", converter.toDBRole("funding acquisition"));
        assertEquals("INVESTIGATION", converter.toDBRole("investigation"));
        assertEquals("METHODOLOGY", converter.toDBRole("methodology"));
        assertEquals("PROJECT_ADMINISTRATION", converter.toDBRole("project administration"));
        assertEquals("RESOURCES", converter.toDBRole("resources"));
        assertEquals("SOFTWARE", converter.toDBRole("software"));
        assertEquals("SUPERVISION", converter.toDBRole("supervision"));
        assertEquals("VALIDATION", converter.toDBRole("validation"));
        assertEquals("VISUALIZATION", converter.toDBRole("visualization"));
        assertEquals("WRITING_ORIGINAL_DRAFT", converter.toDBRole("writing – original draft"));
        assertEquals("WRITING_REVIEW_EDITING", converter.toDBRole("writing – review & editing"));
    }

    @Test
    public void testToLegacyRoleValue() {
        // legacy
        assertEquals("author", converter.toLegacyRoleValue("AUTHOR"));
        assertEquals("assignee", converter.toLegacyRoleValue("ASSIGNEE"));
        assertEquals("editor", converter.toLegacyRoleValue("EDITOR"));
        assertEquals("chair-or-translator", converter.toLegacyRoleValue("CHAIR_OR_TRANSLATOR"));
        assertEquals("co-investigator", converter.toLegacyRoleValue("CO_INVESTIGATOR"));
        assertEquals("co-inventor", converter.toLegacyRoleValue("CO_INVENTOR"));
        assertEquals("graduate-student", converter.toLegacyRoleValue("GRADUATE_STUDENT"));
        assertEquals("other-inventor", converter.toLegacyRoleValue("OTHER_INVENTOR"));
        assertEquals("principal-investigator", converter.toLegacyRoleValue("PRINCIPAL_INVESTIGATOR"));
        assertEquals("postdoctoral-researcher", converter.toLegacyRoleValue("POSTDOCTORAL_RESEARCHER"));
        assertEquals("support-staff", converter.toLegacyRoleValue("SUPPORT_STAFF"));

        // credit
        assertNull(converter.toLegacyRoleValue("CONCEPTUALIZATION"));
        assertNull(converter.toLegacyRoleValue("DATA_CURATION"));
        assertEquals("editor", converter.toLegacyRoleValue("EDITOR")); // special case, part of both sets of roles
        assertNull(converter.toLegacyRoleValue("FORMAL_ANALYSIS"));
        assertNull(converter.toLegacyRoleValue("FUNDING_ACQUISITION"));
        assertEquals("co-investigator", converter.toLegacyRoleValue("INVESTIGATION"));
        assertNull(converter.toLegacyRoleValue("METHODOLOGY"));
        assertNull(converter.toLegacyRoleValue("PROJECT_ADMINISTRATION"));
        assertNull(converter.toLegacyRoleValue("RESOURCES"));
        assertNull(converter.toLegacyRoleValue("SOFTWARE"));
        assertNull(converter.toLegacyRoleValue("SUPERVISION"));
        assertNull(converter.toLegacyRoleValue("VALIDATION"));
        assertNull(converter.toLegacyRoleValue("VISUALIZATION"));
        assertEquals("author", converter.toLegacyRoleValue("WRITING_ORIGINAL_DRAFT"));
        assertEquals("editor", converter.toLegacyRoleValue("WRITING_REVIEW_EDITING"));
    }
    
    @Test
    public void testToRoleValue() {
        // legacy
        assertEquals("author", converter.toRoleValue("AUTHOR"));
        assertEquals("assignee", converter.toRoleValue("ASSIGNEE"));
        assertEquals("editor", converter.toRoleValue("EDITOR"));
        assertEquals("chair-or-translator", converter.toRoleValue("CHAIR_OR_TRANSLATOR"));
        assertEquals("co-investigator", converter.toRoleValue("CO_INVESTIGATOR"));
        assertEquals("co-inventor", converter.toRoleValue("CO_INVENTOR"));
        assertEquals("graduate-student", converter.toRoleValue("GRADUATE_STUDENT"));
        assertEquals("other-inventor", converter.toRoleValue("OTHER_INVENTOR"));
        assertEquals("principal-investigator", converter.toRoleValue("PRINCIPAL_INVESTIGATOR"));
        assertEquals("postdoctoral-researcher", converter.toRoleValue("POSTDOCTORAL_RESEARCHER"));
        assertEquals("support-staff", converter.toRoleValue("SUPPORT_STAFF"));

        // credit
        assertEquals("conceptualization", converter.toRoleValue("CONCEPTUALIZATION"));
        assertEquals("data curation", converter.toRoleValue("DATA_CURATION"));
        assertEquals("editor", converter.toRoleValue("EDITOR"));
        assertEquals("formal analysis", converter.toRoleValue("FORMAL_ANALYSIS"));
        assertEquals("funding acquisition", converter.toRoleValue("FUNDING_ACQUISITION"));
        assertEquals("investigation", converter.toRoleValue("INVESTIGATION"));
        assertEquals("methodology", converter.toRoleValue("METHODOLOGY"));
        assertEquals("project administration", converter.toRoleValue("PROJECT_ADMINISTRATION"));
        assertEquals("resources", converter.toRoleValue("RESOURCES"));
        assertEquals("software", converter.toRoleValue("SOFTWARE"));
        assertEquals("supervision", converter.toRoleValue("SUPERVISION"));
        assertEquals("validation", converter.toRoleValue("VALIDATION"));
        assertEquals("visualization", converter.toRoleValue("VISUALIZATION"));
        assertEquals("writing – original draft", converter.toRoleValue("WRITING_ORIGINAL_DRAFT"));
        assertEquals("writing – review & editing", converter.toRoleValue("WRITING_REVIEW_EDITING"));
    }

}
