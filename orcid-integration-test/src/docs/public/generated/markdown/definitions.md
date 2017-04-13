## Definitions
### ActivitiesSummary
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|last-modified-date||false|LastModifiedDate||
|educations||false|Educations||
|employments||false|Employments||
|fundings||false|Fundings||
|peer-reviews||false|PeerReviews||
|works||false|Works||
|path||false|string||


### Amount
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||
|currency-code||false|string||


### Citation
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|citation-type||true|enum (FORMATTED_UNSPECIFIED, BIBTEX, FORMATTED_APA, FORMATTED_HARVARD, FORMATTED_IEEE, FORMATTED_MLA, FORMATTED_VANCOUVER, FORMATTED_CHICAGO, RIS)||
|citation-value||true|string||


### Contributor
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|contributor-orcid||false|ContributorOrcid||
|credit-name||false|CreditName||
|contributor-email||false|ContributorEmail||
|contributor-attributes||false|ContributorAttributes||


### ContributorAttributes
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|contributor-sequence||true|enum (FIRST, ADDITIONAL)||
|contributor-role||true|enum (AUTHOR, ASSIGNEE, EDITOR, CHAIR_OR_TRANSLATOR, CO_INVESTIGATOR, CO_INVENTOR, GRADUATE_STUDENT, OTHER_INVENTOR, PRINCIPAL_INVESTIGATOR, POSTDOCTORAL_RESEARCHER, SUPPORT_STAFF)||


### ContributorEmail
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||


### ContributorOrcid
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|uri||false|string||
|path||false|string||
|host||false|string||


### Country
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|enum (AF, AX, AL, DZ, AS, AD, AO, AI, AQ, AG, AR, AM, AW, AU, AT, AZ, BS, BH, BD, BB, BY, BE, BZ, BJ, BM, BT, BO, BQ, BA, BW, BV, BR, IO, BN, BG, BF, BI, KH, CM, CA, CV, KY, CF, TD, CL, CN, CX, CC, CO, KM, CG, CD, CK, CR, CI, HR, CU, CW, CY, CZ, DK, DJ, DM, DO, EC, EG, SV, GQ, ER, EE, ET, FK, FO, FJ, FI, FR, GF, PF, TF, GA, GM, GE, DE, GH, GI, GR, GL, GD, GP, GU, GT, GG, GN, GW, GY, HT, HM, VA, HN, HK, HU, IS, IN, ID, IR, IQ, IE, IM, IL, IT, JM, JP, JE, JO, KZ, KE, KI, KP, KR, KW, KG, LA, LV, LB, LS, LR, LY, LI, LT, LU, MO, MK, MG, MW, MY, MV, ML, MT, MH, MQ, MR, MU, YT, MX, FM, MD, MC, MN, ME, MS, MA, MZ, MM, NA, NR, NP, NL, NC, NZ, NI, NE, NG, NU, NF, MP, NO, OM, PK, PW, PS, PA, PG, PY, PE, PH, PN, PL, PT, PR, QA, RE, RO, RU, RW, BL, SH, KN, LC, MF, PM, VC, WS, SM, ST, SA, SN, RS, SC, SL, SG, SX, SK, SI, SB, SO, ZA, GS, SS, ES, LK, SD, SR, SJ, SZ, SE, CH, SY, TJ, TZ, TH, TL, TG, TK, TO, TT, TN, TR, TM, TC, TV, UG, UA, AE, GB, US, UM, UY, UZ, VU, VE, VN, VG, VI, WF, EH, YE, ZM, ZW, TW, XK)||


### CreatedDate
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string (date-time)||


### CreditName
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||


### Day
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||


### DisambiguatedOrganization
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|disambiguated-organization-identifier||true|string||
|disambiguation-source||true|string||


### Education
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|created-date||false|CreatedDate||
|last-modified-date||false|LastModifiedDate||
|source||false|Source||
|path||false|string||
|department-name||false|string||
|role-title||false|string||
|start-date||false|FuzzyDate||
|end-date||false|FuzzyDate||
|organization||true|Organization||
|visibility||false|enum (LIMITED, REGISTERED_ONLY, PUBLIC)||


### EducationSummary
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|created-date||false|CreatedDate||
|last-modified-date||false|LastModifiedDate||
|source||false|Source||
|department-name||false|string||
|role-title||false|string||
|start-date||false|FuzzyDate||
|end-date||false|FuzzyDate||
|organization||false|Organization||
|visibility||false|enum (LIMITED, REGISTERED_ONLY, PUBLIC)||
|put-code||false|integer (int64)||
|path||false|string||


### Educations
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|last-modified-date||false|LastModifiedDate||
|education-summary||false|EducationSummary array||
|path||false|string||


### Employment
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|created-date||false|CreatedDate||
|last-modified-date||false|LastModifiedDate||
|source||false|Source||
|path||false|string||
|department-name||false|string||
|role-title||false|string||
|start-date||false|FuzzyDate||
|end-date||false|FuzzyDate||
|organization||true|Organization||
|visibility||false|enum (LIMITED, REGISTERED_ONLY, PUBLIC)||


### EmploymentSummary
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|created-date||false|CreatedDate||
|last-modified-date||false|LastModifiedDate||
|source||false|Source||
|department-name||false|string||
|role-title||false|string||
|start-date||false|FuzzyDate||
|end-date||false|FuzzyDate||
|organization||false|Organization||
|visibility||false|enum (LIMITED, REGISTERED_ONLY, PUBLIC)||
|put-code||false|integer (int64)||
|path||false|string||


### Employments
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|last-modified-date||false|LastModifiedDate||
|employment-summary||false|EmploymentSummary array||
|path||false|string||


### ExternalID
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|external-id-type||true|string||
|external-id-value||true|string||
|external-id-url||false|Url||
|external-id-relationship||false|enum (PART_OF, SELF)||


### ExternalIDs
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|external-id||false|ExternalID array||


### Funding
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|created-date||false|CreatedDate||
|last-modified-date||false|LastModifiedDate||
|source||false|Source||
|path||false|string||
|type||true|enum (GRANT, CONTRACT, AWARD, SALARY_AWARD)||
|organization-defined-type||false|OrganizationDefinedFundingSubType||
|title||true|FundingTitle||
|short-description||false|string||
|amount||false|Amount||
|url||false|Url||
|start-date||false|FuzzyDate||
|end-date||false|FuzzyDate||
|external-ids||false|ExternalIDs||
|contributors||false|FundingContributors||
|organization||true|Organization||
|visibility||false|enum (LIMITED, REGISTERED_ONLY, PUBLIC)||


### FundingContributor
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|contributor-orcid||false|ContributorOrcid||
|credit-name||false|CreditName||
|contributor-email||false|ContributorEmail||
|contributor-attributes||false|FundingContributorAttributes||


### FundingContributorAttributes
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|contributor-role||true|enum (LEAD, CO_LEAD, SUPPORTED_BY, OTHER_CONTRIBUTION)||


### FundingContributors
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|contributor||false|FundingContributor array||


### FundingExternalIdentifier
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|external-identifier-type||false|enum (GRANT_NUMBER)||
|external-identifier-value||false|string||
|relationship||false|enum (PART_OF, SELF)||
|external-identifier-url||false|Url||


### FundingExternalIdentifiers
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|externalIdentifier||false|FundingExternalIdentifier array||


### FundingGroup
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|last-modified-date||false|LastModifiedDate||
|external-ids||false|ExternalIDs||
|summary||false|FundingSummary array||


### FundingSummary
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|created-date||false|CreatedDate||
|last-modified-date||false|LastModifiedDate||
|source||false|Source||
|title||true|FundingTitle||
|external-ids||false|ExternalIDs||
|type||true|enum (GRANT, CONTRACT, AWARD, SALARY_AWARD)||
|start-date||false|FuzzyDate||
|end-date||false|FuzzyDate||
|organization||true|Organization||
|visibility||false|enum (LIMITED, REGISTERED_ONLY, PUBLIC)||
|put-code||false|integer (int64)||
|path||false|string||
|display-index||false|string||


### FundingTitle
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|title||false|Title||
|translated-title||false|TranslatedTitle||


### Fundings
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|last-modified-date||false|LastModifiedDate||
|group||false|FundingGroup array||
|path||false|string||


### FuzzyDate
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|year||true|Year||
|month||false|Month||
|day||false|Day||


### Identifier
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|external-identifier-type||false|string||
|external-identifier-id||false|string||


### Identifiers
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|identifier||false|Identifier array||


### LastModifiedDate
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string (date-time)||


### Month
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||


### Organization
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|name||true|string||
|address||true|OrganizationAddress||
|disambiguated-organization||false|DisambiguatedOrganization||


### OrganizationAddress
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|city||true|string||
|region||false|string||
|country||true|enum (AF, AX, AL, DZ, AS, AD, AO, AI, AQ, AG, AR, AM, AW, AU, AT, AZ, BS, BH, BD, BB, BY, BE, BZ, BJ, BM, BT, BO, BQ, BA, BW, BV, BR, IO, BN, BG, BF, BI, KH, CM, CA, CV, KY, CF, TD, CL, CN, CX, CC, CO, KM, CG, CD, CK, CR, CI, HR, CU, CW, CY, CZ, DK, DJ, DM, DO, EC, EG, SV, GQ, ER, EE, ET, FK, FO, FJ, FI, FR, GF, PF, TF, GA, GM, GE, DE, GH, GI, GR, GL, GD, GP, GU, GT, GG, GN, GW, GY, HT, HM, VA, HN, HK, HU, IS, IN, ID, IR, IQ, IE, IM, IL, IT, JM, JP, JE, JO, KZ, KE, KI, KP, KR, KW, KG, LA, LV, LB, LS, LR, LY, LI, LT, LU, MO, MK, MG, MW, MY, MV, ML, MT, MH, MQ, MR, MU, YT, MX, FM, MD, MC, MN, ME, MS, MA, MZ, MM, NA, NR, NP, NL, NC, NZ, NI, NE, NG, NU, NF, MP, NO, OM, PK, PW, PS, PA, PG, PY, PE, PH, PN, PL, PT, PR, QA, RE, RO, RU, RW, BL, SH, KN, LC, MF, PM, VC, WS, SM, ST, SA, SN, RS, SC, SL, SG, SX, SK, SI, SB, SO, ZA, GS, SS, ES, LK, SD, SR, SJ, SZ, SE, CH, SY, TJ, TZ, TH, TL, TG, TK, TO, TT, TN, TR, TM, TC, TV, UG, UA, AE, GB, US, UM, UY, UZ, VU, VE, VN, VG, VI, WF, EH, YE, ZM, ZW, TW, XK)||


### OrganizationDefinedFundingSubType
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||


### PeerReview
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|created-date||false|CreatedDate||
|last-modified-date||false|LastModifiedDate||
|source||false|Source||
|reviewer-role||false|enum (REVIEWER, EDITOR, MEMBER, CHAIR, ORGANIZER)||
|review-identifiers||false|ExternalIDs||
|review-url||false|Url||
|review-type||false|enum (REVIEW, EVALUATION)||
|review-completion-date||false|FuzzyDate||
|review-group-id||true|string||
|subject-external-identifier||false|ExternalID||
|subject-container-name||false|Title||
|subject-type||false|enum (ARTISTIC_PERFORMANCE, BOOK_CHAPTER, BOOK_REVIEW, BOOK, CONFERENCE_ABSTRACT, CONFERENCE_PAPER, CONFERENCE_POSTER, DATA_SET, DICTIONARY_ENTRY, DISCLOSURE, DISSERTATION, EDITED_BOOK, ENCYCLOPEDIA_ENTRY, INVENTION, JOURNAL_ARTICLE, JOURNAL_ISSUE, LECTURE_SPEECH, LICENSE, MAGAZINE_ARTICLE, MANUAL, NEWSLETTER_ARTICLE, NEWSPAPER_ARTICLE, ONLINE_RESOURCE, OTHER, PATENT, REGISTERED_COPYRIGHT, REPORT, RESEARCH_TECHNIQUE, RESEARCH_TOOL, SPIN_OFF_COMPANY, STANDARDS_AND_POLICY, SUPERVISED_STUDENT_PUBLICATION, TECHNICAL_STANDARD, TEST, TRADEMARK, TRANSLATION, WEBSITE, WORKING_PAPER, UNDEFINED)||
|subject-name||false|WorkTitle||
|subject-url||false|Url||
|convening-organization||true|Organization||
|visibility||false|enum (LIMITED, REGISTERED_ONLY, PUBLIC)||
|path||false|string||


### PeerReviewGroup
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|last-modified-date||false|LastModifiedDate||
|external-ids||false|ExternalIDs||
|summary||false|PeerReviewSummary array||


### PeerReviewSummary
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|created-date||false|CreatedDate||
|last-modified-date||false|LastModifiedDate||
|source||false|Source||
|external-ids||false|ExternalIDs||
|completion-date||false|FuzzyDate||
|review-group-id||true|string||
|convening-organization||true|Organization||
|visibility||false|enum (LIMITED, REGISTERED_ONLY, PUBLIC)||
|put-code||false|integer (int64)||
|path||false|string||
|display-index||false|string||


### PeerReviews
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|last-modified-date||false|LastModifiedDate||
|group||false|PeerReviewGroup array||
|path||false|string||


### PublicationDate
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|year||true|Year||
|month||false|Month||
|day||false|Day||
|media-type||false|enum (PRINT, ONLINE, OTHER)||


### Source
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|source-orcid||false|SourceOrcid||
|source-client-id||false|SourceClientId||
|source-name||false|SourceName||


### SourceClientId
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|uri||false|string||
|path||false|string||
|host||false|string||


### SourceName
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||


### SourceOrcid
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|uri||false|string||
|path||false|string||
|host||false|string||


### StatisticsSummary
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|date||false|string (date-time)||
|statistics||false|object||


### StatisticsTimeline
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|timeline||false|object||
|statistic-name||false|string||


### StatsTimelineList
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|timelines||false|StatisticsTimeline array||


### Subtitle
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||


### Title
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||


### TranslatedTitle
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||
|language-code||false|string||


### Url
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||


### Work
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|created-date||false|CreatedDate||
|last-modified-date||false|LastModifiedDate||
|source||false|Source||
|path||false|string||
|title||false|WorkTitle||
|journal-title||false|Title||
|short-description||false|string||
|citation||false|Citation||
|type||false|enum (ARTISTIC_PERFORMANCE, BOOK_CHAPTER, BOOK_REVIEW, BOOK, CONFERENCE_ABSTRACT, CONFERENCE_PAPER, CONFERENCE_POSTER, DATA_SET, DICTIONARY_ENTRY, DISCLOSURE, DISSERTATION, EDITED_BOOK, ENCYCLOPEDIA_ENTRY, INVENTION, JOURNAL_ARTICLE, JOURNAL_ISSUE, LECTURE_SPEECH, LICENSE, MAGAZINE_ARTICLE, MANUAL, NEWSLETTER_ARTICLE, NEWSPAPER_ARTICLE, ONLINE_RESOURCE, OTHER, PATENT, REGISTERED_COPYRIGHT, REPORT, RESEARCH_TECHNIQUE, RESEARCH_TOOL, SPIN_OFF_COMPANY, STANDARDS_AND_POLICY, SUPERVISED_STUDENT_PUBLICATION, TECHNICAL_STANDARD, TEST, TRADEMARK, TRANSLATION, WEBSITE, WORKING_PAPER, UNDEFINED)||
|publication-date||false|PublicationDate||
|external-ids||false|ExternalIDs||
|url||false|Url||
|contributors||false|WorkContributors||
|language-code||false|string||
|country||false|Country||
|visibility||false|enum (LIMITED, REGISTERED_ONLY, PUBLIC)||


### WorkBulk
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|bulk||false|BulkElement array||


### WorkContributors
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|contributor||false|Contributor array||


### WorkExternalIdentifier
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|external-identifier-type||true|enum (OTHER_ID, ASIN, ASIN_TLD, ARXIV, BIBCODE, DOI, EID, ISBN, ISSN, JFM, JSTOR, LCCN, MR, OCLC, OL, OSTI, PMC, PMID, RFC, SSRN, ZBL, AGR, CBA, CIT, CTX, ETHOS, HANDLE, HIR, PAT, SOURCE_WORK_ID, URI, URN, WOSUID)||
|external-identifier-id||true|WorkExternalIdentifierId||
|relationship||false|enum (PART_OF, SELF)||
|external-identifier-url||false|Url||


### WorkExternalIdentifierId
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||


### WorkExternalIdentifiers
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|work-external-identifier||false|WorkExternalIdentifier array||


### WorkGroup
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|last-modified-date||false|LastModifiedDate||
|external-ids||false|ExternalIDs||
|work-summary||false|WorkSummary array||


### WorkSummary
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|put-code||false|integer (int64)||
|created-date||false|CreatedDate||
|last-modified-date||false|LastModifiedDate||
|source||false|Source||
|title||false|WorkTitle||
|external-ids||false|ExternalIDs||
|type||false|enum (ARTISTIC_PERFORMANCE, BOOK_CHAPTER, BOOK_REVIEW, BOOK, CONFERENCE_ABSTRACT, CONFERENCE_PAPER, CONFERENCE_POSTER, DATA_SET, DICTIONARY_ENTRY, DISCLOSURE, DISSERTATION, EDITED_BOOK, ENCYCLOPEDIA_ENTRY, INVENTION, JOURNAL_ARTICLE, JOURNAL_ISSUE, LECTURE_SPEECH, LICENSE, MAGAZINE_ARTICLE, MANUAL, NEWSLETTER_ARTICLE, NEWSPAPER_ARTICLE, ONLINE_RESOURCE, OTHER, PATENT, REGISTERED_COPYRIGHT, REPORT, RESEARCH_TECHNIQUE, RESEARCH_TOOL, SPIN_OFF_COMPANY, STANDARDS_AND_POLICY, SUPERVISED_STUDENT_PUBLICATION, TECHNICAL_STANDARD, TEST, TRADEMARK, TRANSLATION, WEBSITE, WORKING_PAPER, UNDEFINED)||
|publication-date||false|PublicationDate||
|visibility||false|enum (LIMITED, REGISTERED_ONLY, PUBLIC)||
|path||false|string||
|display-index||false|string||


### WorkTitle
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|title||false|Title||
|subtitle||false|Subtitle||
|translated-title||false|TranslatedTitle||


### Works
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|last-modified-date||false|LastModifiedDate||
|group||false|WorkGroup array||
|path||false|string||


### Year
|Name|Description|Required|Schema|Default|
|----|----|----|----|----|
|value||false|string||


