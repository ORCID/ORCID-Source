# ORCID API v2.0 Guide

## Current State (Release Candidate Stable)
v2.0_r3 is in current development and should be avoided.

## XSDs and current state (all stable)
- [activities-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/activities-2.0.xsd) 
**stable**, developement ongoing
- [address-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/address-2.0.xsd)
**stable**, developement ongoing
- [bulk-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/bulk-2.0.xsd)
**stable**, developement ongoing
- [common-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/common_2.0/common-2.0.xsd)
**stable**, developement ongoing
- [education-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/education-2.0.xsd)
**stable**, developement ongoing
- [email-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/email-2.0.xsd)
**stable**, developement ongoing
- [employment-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/employment-2.0.xsd)
**not stable**, developement ongoing
- [error-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/error-2.0.xsd)
**stable**, developement ongoing
- [external-identifier-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/external-identifier-2.0.xsd)
**stable**, developement ongoing
- [funding-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/funding-2.0.xsd)
**stable**, developement ongoing
- [keyword-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/keyword-2.0.xsd)
**stable**, developement ongoing
- [other-names-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/other-name-2.0.xsd)
**stable**, developement ongoing
- [peer-review-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/peer-review-2.0.xsd)
**stable**, developement ongoing
- [person-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/person-2.0.xsd)
**stable**, developement ongoing
- [personal-details-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/personal-details-2.0.xsd)
**stable**, developement ongoing
- [researcher-url-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/researcher-url-2.0.xsd)
**stable**, developement ongoing
- [work-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/work-2.0.xsd)
**stable**, developement ongoing

##Changes:
###Post bulk works

- Addition of endpoint /works for bulk posting works
- Update to schema of bulk element in works
- Update to schema of error section for returning errors with bulk added works

## Sample files:

- [activities-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/activities-2.0.xml)
- [address-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/address-2.0.xml)
- [bulk-work-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/bulk-work-2.0.xml)
- [bulk-work-2.0.json] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/bulk-work-2.0.json)
- [biography-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/biography-2.0.xml)
- [credit-name-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/credit-name-2.0.xml)
- [education-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/education-2.0.xml)
- [email-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/email-2.0.xml)
- [emails-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/emails-2.0.xml)
- [employment-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/employment-2.0.xml)
- [error-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/error-2.0.xml)
- [external-identifier-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/external-identifier-2.0.xml)
- [external-identifiers-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/external-identifiers-2.0.xml)
- [funding-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/funding-2.0.xml)
- [keyword-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/keyword-2.0.xml)
- [keywords-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/keywords-2.0.xml)
- [name-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/name-2.0.xml)
- [other-name-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/other-name-2.0.xml)
- [other-names-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/other-names-2.0.xml)
- [peer-review-2.0.xml ] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/peer-review-2.0.xml )
- [person-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/person-2.0.xml)
- [personal-details-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/personal-details-2.0.xml)
- [researcher-url-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/researcher-url-2.0.xml)
- [researcher-urls-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/researcher-urls-2.0.xml)
- [work-2.0.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/work-2.0.xml)

**Note:** These files contain system-generated elements/attributes that are returned when reading items from ORCID, but should not be included when posting items to ORCID
- put-code (exception: include put-code when updating items using the PUT method)
- source
- created-date
- last-modified-date

## REST API Reference

### Swagger

The swagger interfaces to the API available at:
- [member](https://api.orcid.org/v2.0/)
- [public](https://pub.orcid.org/v2.0/)

### Activities summary
| Item                    | HTTP method | Scope                    | URL                                                      |
|-------------------------|-------------|--------------------------|----------------------------------------------------------|
| Read activities summary | GET         | /read-limited            | https://api.sandbox.orcid.org/v2.0/[ORCID]/activities |

### Individual activities
| Action             | HTTP method | Scope                    | URL                                                                      |
|--------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Add an activity    | POST        | /activities/update       | https://api.sandbox.orcid.org/v2.0/[ORCID]/[ACTIVITY-TYPE]            |
| Read an activity   | GET         | /read-limited            | https://api.sandbox.orcid.org/v2.0/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |
| Update an activity | PUT         | /activities/update       | https://api.sandbox.orcid.org/v2.0/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |
| Delete an activity | DELETE      | /activities/update       | https://api.sandbox.orcid.org/v2.0/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |

### Bulk activities
| Action             | HTTP method | Scope                    | URL                                                                      |
|--------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Add works		     | POST        | /activities/update       | https://api.sandbox.orcid.org/v2.0/[ORCID]/works		             |

### Biography details
| Action             | HTTP method | Scope                    | URL                                                                      |
|--------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Add  section       | POST        | /person/update       	  | https://api.sandbox.orcid.org/v2.0/[ORCID]/[SECTION]            		 |
| Read section       | GET         | /read-limited       	  | https://api.sandbox.orcid.org/v2.0/[ORCID]/[SECTION]/[PUT-CODE]  	 |
| Update section     | PUT         | /person/update       	  | https://api.sandbox.orcid.org/v2.0/[ORCID]/[SECTION]/[PUT-CODE]  	 |
| Delete section     | DELETE      | /person/update       	  | https://api.sandbox.orcid.org/v2.0/[ORCID]/[SECTION]/[PUT-CODE]  	 |



[ORCID] is the ORCID iD for the record.

[ACTIVITY-TYPE] can be one of the following:
- education
- employment
- work
- funding
- peer-review

[SECTION] can be one of the following:
- address
- email *
- external-identifiers
- keywords
- other-names
- researcher-urls
- person *
- personal-details *

**Note:** email, person and personal-details sections are only for reading purposes

## Examples
### Read Record Items
| Item               | v2.0 Scope               | v1.2 Scope (deprecated)                               | Example cURL Statement                                         |
|--------------------|--------------------------|-------------------------------------------------------|----------------------------------------------------------------|
| Activities summary | /read-limited            |  /orcid-profile/read-limited                          | ```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/activities'```|
| Address	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/address'```|
| Education          | /read-limited            | /orcid-profile/read-limited /affiliations/read-limited |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/education/14613'```|
| Email     		| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/email'```|
| Employment         | /read-limited            | /orcid-profile/read-limited /affiliations/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/employment/14612'```| 
| External Identifiers	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/external-identifiers'```|
| Funding            | /read-limited            | /orcid-profile/read-limited /funding/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/funding/2629'```|
| Keywords     	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/keywords'```|
| Other names     	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/other-names'```|
| Peer review        | /read-limited            |  NONE  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/peer-review/1579'```|
| Personal details	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/personal-details'```|
| Person	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/person'```|
| Researcher URL     | /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/researcher-urls'```|
| Work               | /read-limited            | /orcid-profile/read-limited <br>/orcid-works/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/work/469271'```|

### Add Record Items
|Item    |v2.0 Scope       | v1.2 Scope (deprecated)| Example cURL Statement  |
|---------|----------------|------------------------|-------------------------|
| Address	|/person/update           | /orcid-bio/update   			|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/address.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/address```|
| Education  |/activities/update  |/affiliations/create|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/education' ```|
| Employment |  /activities/update  |/affiliations/create  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/employment' ```| 
| External Identifiers	| /person/update            | /orcid-bio/external-identifiers/create	|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/external_identifier.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/external-identifiers```|
| Funding  |/activities/update  |/funding/create  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/funding'```|
| Keywords     	|/person/update            | /orcid-bio/update   		|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/keyword.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/keywords```|
| Other names  |/person/update|  /orcid-bio/update |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/other-name.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/other-names'```|
| Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/peer-review'```|
| Researcher URL  |/person/update|  /orcid-bio/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/researcher-url.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/researcher-urls'```|
| Work  |/activities/update|  /orcid-works/create  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/work'```|
| Bulk works  |/activities/update|  /orcid-works/create  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/works.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/works'```|

### Update Record Items
|Item  |v2.0 Scope  |v1.2 Scope (deprecated)| Example cURL Statement  |
|------|----------------|-----------------------|---------------------|
| Address	|/person/update            | /orcid-bio/update   			|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/address.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/address/[PUT-CODE]```|
| Education  |/activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/education/[PUT-CODE]' ```|
| Employment |  /activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/employment/[PUT-CODE]' ```|
| External Identifiers	|/person/update            | /orcid-bio/update 	|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/external_identifier.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/external-identifiers/[PUT-CODE]```|
| Funding  |/activities/update  |/funding/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/funding/[PUT-CODE]'```|
| Keywords     	|/person/update            | /orcid-bio/update   		|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/keyword.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/keywords/[PUT-CODE]```|
| Other names  |/person/update|  /orcid-bio/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/other-name.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/other-names/[PUT-CODE]'```|
| Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/peer-review/[PUT-CODE]'```|
| Researcher URL  |/person/update|  /orcid-bio/update |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/researcher-url.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/researcher-urls/[PUT-CODE]'```|
| Work  |/activities/update|  /orcid-works/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/work/[PUT-CODE]'```|

When updating a record item, include the put code as an attribute in the root element, ex: ```<education:education put-code="14775" xmlns:common="http://www.orcid.org/ns/common" xmlns:education="http://www.orcid.org/ns/education" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.orcid.org/ns/education ../education-2.0.xsd ">```

### Delete Record Items
|Item  |v2.0 Scope      |v1.2 Scope (deprecated)| Example cURL Statement  |
|------|----------------|-----------------------|-------------------------|
| Address	| /read-limited            | /orcid-bio/update   	|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/address/[PUT-CODE]```|
| Education  |/activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/education/[PUT-CODE]'  ```|
| Employment |  /activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/employment/[PUT-CODE]' ```|
| External Identifiers	| /read-limited    | /orcid-bio/update 	|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/external-identifiers/[PUT-CODE]```|
| Funding  |/activities/update  |/funding/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/funding/[PUT-CODE]'```|
| Keywords     	| /read-limited            | /orcid-bio/update 	|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/keywords/[PUT-CODE]```|
| Other names  |/person/update|  /orcid-bio/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/researcher-urls/[PUT-CODE]'```|
| Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/peer-review/[PUT-CODE]'```|
| Researcher URL  |/person/update|  /orcid-bio/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/researcher-urls/[PUT-CODE]'```|
| Work  |/activities/update|  /orcid-works/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-1306-4180/work/[PUT-CODE]'```|


