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
package org.orcid.api.common.writer.cerif;

/** Derived from https://github.com/EKT/CERIF-Tools/blob/master/CERIF%201.6/cerif-jpa-model/src/main/java/gr/ekt/cerif/enumerations/semantics/ClassSchemeEnum.java
 * 
 * @author tom
 *
 */
public enum CerifClassSchemeEnum {
    
    CERIF_ENTITIES("6e0d9af0-1cd6-11e1-8bc2-0800200c9a66","CERIF Entities"),
    MEREOTOPOLOGICAL_STRUCTURE("065bd11e-0f80-4a5e-a695-54bed2e255a5","Mereotopological Structure"),
    PERSON_TITLES("b93fc7ba-90be-4825-a1fd-363bd2fd1432","Person Titles"),
    PERSON_DEGREE_LEVELS_OF_STUDY("a1a55095-45c2-456c-9869-add5d726c676","Person Degree Levels of Study"),
    PERSON_PROFESSIONAL_RELATIONSHIPS("6b2b7d24-3491-11e1-b86c-0800200c9a66","Person Professional Relationships"),
    INTER_ORGANISATIONAL_STRUCTURE("cf7772d0-3477-11e1-b86c-0800200c9a66","Inter-Organisational Structure"),
    INTER_PUBLICATION_RELATIONS("759af932-34ae-11e1-b86c-0800200c9a66","Inter-Publication Relations"),
    INTER_PATENT_RELATIONS("50772538-d92f-4048-ae76-93e1659afc8b","Inter-Patent Relations"),
    INTER_PRODUCT_RELATIONS("f5c75446-de28-4c4b-8739-8f6223a09e88","Inter-Product Relations"),
    INTER_EVENT_RELATIONS("ac90d633-8ad6-4c23-b45a-cc4f91309843","Inter-Event Relations"),
    INTER_FACILITY_RELATIONS("da0e5a01-c73e-4489-8cf7-917e9efcdad4","Inter-Facility Relations"),
    INTER_FUNDING_RELATIONS("abdca247-7514-4620-beb1-9fb919fe76b0","Inter-Funding Relations"),
    INTER_EQUIPMENT_RELATIONS("623d2471-8d16-40c9-915a-df496da086be","Inter-Equipment Relations"),
    INTER_SERVICE_RELATIONS("39f413d8-e5ee-409a-95f1-d204b78508b9","Inter-Service Relations"),
    INTER_OUTPUT_RELATIONS("a7e0dc90-1be4-4fd9-9ff7-bdfb8a95a1eb","Inter-Output Relations"),
    ACTIVITY_STRUCTURE("759af930-34ae-11e1-b86c-0800200c9a66","Activity Structure"),
    ACTIVITY_OUTPUT_CONTRIBUTIONS("ad87a6e2-5093-4550-86da-ead48cc3f385","Activity Output Contributions"),
    ACTIVITY_FINANCE_CATEGORIES("c855b95d-bf01-44eb-a1f3-17b0f0901599","Activity Finance Categories"),
    ACTIVITY_FINANCE_CATEGORY_AMOUNTS("63468e45-3055-4ba8-b644-6262c96f62bd","Activity Finance Category Amounts"),
    PROJECT_OUTPUT_ROLES("759af931-34ae-11e1-b86c-0800200c9a66","Project Output Roles"),
    PROJECT_OUTCOME_RELATIONS("355408c0-4afa-417d-a7cb-642b74c6dff9","Project Outcome Relations"),
    PROJECT_RESEARCH_INFRASTRUCTURE_RELATIONS("6df06582-34bd-11e1-b86c-0800200c9a66","Project Research Infrastructure Relations"),
    ORGANISATION_FUNDING_ROLES("759af937-34ae-11e1-b86c-0800200c9a66","Organisation Funding Roles"),
    ORGANISATION_PROJECT_ENGAGEMENTS("6b2b7d25-3491-11e1-b86c-0800200c9a66","Organisation Project Engagements"),
    ORGANISATION_OUTPUT_CONTRIBUTIONS("6b2b7d26-3491-11e1-b86c-0800200c9a66","Organisation Output Contributions"),
    ORGANISATION_OUTPUT_ROLES("877161b4-00d2-42c8-a368-aaa35262f3a8","Organisation Output Roles"),
    ORGANISATION_RESEARCH_INFRASTRUCTURE_ROLES("759af93e-34ae-11e1-b86c-0800200c9a66","Organisation Research Infrastructure Roles"),
    ORGANISATION_MEASUREMENT_RELATIONS("aef32e66-d31a-40db-bbd9-d1df96522987","Organisation Measurement Relations"),
    ORGANISATION_CONTACT_DETAILS("fee53e30-de3a-421b-80e0-9b3fe3a3c170","Organisation Contact Details"),
    PERSON_NAMES("7375609d-cfa6-45ce-a803-75de69abe21f","Person Names"),
    PERSON_RESEARCH_INFRASTRUCTURE_ROLES("3fe69a55-34c3-11e1-b86c-0800200c9a66","Person Research Infrastructure Roles"),
    PERSON_EMPLOYMENT_TYPES("e9616dbd-0d38-4b7d-a6cd-3c4df1e95462","Person Employment Types"),
    PERSON_ORGANISATION_ROLES("994069a0-1cd6-11e1-8bc2-0800200c9a66","Person Organisation Roles"),
    PERSON_EVENT_INVOLVEMENTS("b4de9a8f-3a4d-4233-9a9f-3b624e4ad74f","Person Event Involvements"),
    PERSON_PROJECT_ENGAGEMENTS("94fefd50-1d00-11e1-8bc2-0800200c9a66","Person Project Engagements"),
    PERSON_OUTPUT_CONTRIBUTIONS("b7135ad0-1d00-11e1-8bc2-0800200c9a66","Person Output Contributions"),
    PERSON_FUNDING_ROLES("759af934-34ae-11e1-b86c-0800200c9a66","Person Funding Roles"),
    PERSON_CONTACT_DETAILS("05cc5ff9-bc58-4743-ab59-46e5013e0039","Person Contact Details"),
    RESEARCH_OUTPUT("6832797c-2f56-4336-b4ff-dc0ba2275dfa","Research Output"),
    OUTPUT_RESEARCH_INFRASTRUCTURE_RELATIONS("6df0658e-34bd-11e1-b86c-0800200c9a66","Output Research Infrastructure Relations"),
    OUTPUT_FUNDING_ROLES("759af933-34ae-11e1-b86c-0800200c9a66","Output Funding Roles"),
    RESEARCH_INFRASTRUCTURE("dbdd1fdb-128b-414c-ba2b-fa26a25a4bc6","Research Infrastructure"),
    RESEARCH_INFRASTRUCTURE_ACCESS("4490bcae-1632-460d-947c-c230130c013b","Research Infrastructure Access"),
    RESEARCH_INFRASTRUCTURE_USAGE("47761818-0e55-41a6-a21e-1e72f9a8922e","Research Infrastructure Usage"),
    RESEARCH_INFRASTRUCTURE_RELATIONS("4c2f217d-98ad-4c49-bbb0-399e28d7a8c9","Research Infrastructure Relations"),
    RESEARCH_INFRASTRUCTURE_COSTINGS("1c603df8-6949-4f07-bbc3-3388df7dcd2c","Research Infrastructure Costings"),
    RESEARCH_INFRASTRUCTURE_FUNDING_ROLES("3fe69a58-34c3-11e1-b86c-0800200c9a66","Research Infrastructure Funding Roles"),
    FUNDING_SOURCE_DOCUMENT_RELATIONS("c1aef353-5dbb-46a2-86bf-709bb4be3b4d","Funding Source Document Relations"),
    IDENTIFIER_SERVICE_ROLES("5a270628-f593-4ff4-a44a-95660c76e182","Identifier Service Roles"),
    RESEARCH_INFRASTRUCTURE_STATUSES("1eb17479-09de-49b8-9fed-1a54d697ea1c","Research Infrastructure Statuses"),
    ACTIVITY_STATUSES("759af93a-34ae-11e1-b86c-0800200c9a66","Activity Statuses"),
    RESEARCH_INFRASTRUCTURE_TYPES("759af93d-34ae-11e1-b86c-0800200c9a66","Research Infrastructure Types"),
    OUTPUT_TYPES("759af938-34ae-11e1-b86c-0800200c9a66","Output Types"),
    ORGANISATION_TYPES("759af939-34ae-11e1-b86c-0800200c9a66","Organisation Types"),
    FUNDER_TYPES("7e21f287-2c00-443b-ae61-fd9ed9e71333","Funder Types"),
    EVENT_TYPES("e489092b-82a9-4c24-a357-d94dc49eec9b","Event Types"),
    IDENTIFIER_TYPES("bccb3266-689d-4740-a039-c96594b4d916","Identifier Types"),
    FUNDING_SOURCE_TYPES("759af93b-34ae-11e1-b86c-0800200c9a66","Funding Source Types"),
    EDUCATION_DOMAIN_TERMS("b0ca7692-7fda-499f-be40-b44a7bc4f7c7","Education Domain Terms"),
    MEDIA_RELATIONS("40d45f50-db4b-449c-b4f6-ae202b220e5a","Media Relations"),
    ELECTRONIC_ADDRESS_TYPES("1227a225-db7a-444d-a74b-3dd4b438b420","Electronic Address Types"),
    ACTIVITY_TYPES("47c7efda-bb6e-44ad-bfd3-8be5b6b5cd02","Activity Types"),
    ACTIVITY_SUBTYPES("794234b8-25bb-46df-9d26-ae660bca64bc","Activity Subtypes"),
    ACTIVITY_FUNDING_TYPES("a620795c-7015-482e-bdba-43a761b337a1","Activity Funding Types"),
    PUBLICATION_STATUSES("40e90e2f-446d-460a-98e5-5dce57550c48","Publication Statuses"),
    PEER_REVIEWS("4bdfc4ac-7c74-456d-9d3b-b1e4267b90a9","Peer Reviews"),
    OUTPUT_QUALITY_LEVELS("1332d166-4c65-481b-adde-aac0bdc475a3","Output Quality Levels"),
    OPEN_SCIENCE_COSTS("e87469e5-d6bd-458c-b7ef-90e314749c51","Open Science Costs"),
    VERIFICATION_STATUSES("2ad984e8-33ae-4f0b-927e-d292c28750e3","Verification Statuses"),
    USAGE("46fa3468-e5c6-4f71-8d6e-c5998d0efcb0", "Usage"),
    CLASSSCHEME_CLASSIFICATIONS("bcc9792b-d8f9-4e70-af7c-f3889e2228e0", "ClassScheme Classifications");
    
    private final String uuid;

    private final String name;
    
    
    /**
     * @param uuid
     * @param name
     */
    private CerifClassSchemeEnum(String uuid, String name) {
            this.uuid = uuid;
            this.name = name;
    }
    
    /**
     * @return the uuid
     */
    public String getUuid() {
            return uuid;
    }

    /**
     * @return the name
     */
    public String getName() {
            return name;
    }
    
    
    public static CerifClassSchemeEnum fromUuid(String uuid) {
            for (CerifClassSchemeEnum c : CerifClassSchemeEnum.values()) {
                    if (c.uuid.equals(uuid)) {
                            return c;
                    }
            }
            return null;
    }
    
    public static CerifClassSchemeEnum fromName(String name) {
            for (CerifClassSchemeEnum enumeration : CerifClassSchemeEnum.values()) {
                    if (enumeration.name.equals(name)) {
                            return enumeration;
                    }
            }
            return null;
    }

    /**
     * Return a string representation of this code.
     */
    @Override
    public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("ClassEnum [");
            sb.append("uuid=" + uuid + ", name=" + name);
            sb.append("]");
            return sb.toString();
    }


}