# ORCID API v2.0_rc2 Guide

## Current State (Release Candidate Stable)
As of 2016-07-15 changes to v2.0_rc2 will be avoided. Further model changes will be expressed in rc_3.
A Release Candidate (RC) is the built to help ORCID and members check if any critical problems have gone
undetected into the code during the previous development period. Release candidates are NOT suggested for production use.

## XSDs and current state (all stable)
- [activities-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/activities-2.0_rc2.xsd) 
**stable**, developement ongoing
- [address-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/address-2.0_rc2.xsd)
**stable**, developement ongoing
- [common-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/common_2.0_rc2/common-2.0_rc2.xsd)
**stable**, developement ongoing
- [education-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/education-2.0_rc2.xsd)
**stable**, developement ongoing
- [email-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/email-2.0_rc2.xsd)
**stable**, developement ongoing
- [employment-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/employment-2.0_rc2.xsd)
**not stable**, developement ongoing
- [error-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/error-2.0_rc2.xsd)
**stable**, developement ongoing
- [external-identifier-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/external-identifier-2.0_rc2.xsd)
**stable**, developement ongoing
- [funding-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/funding-2.0_rc2.xsd)
**stable**, developement ongoing
- [keyword-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/keyword-2.0_rc2.xsd)
**stable**, developement ongoing
- [other-names-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/other-name-2.0_rc2.xsd)
**stable**, developement ongoing
- [peer-review-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/peer-review-2.0_rc2.xsd)
**stable**, developement ongoing
- [person-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/person-2.0_rc2.xsd)
**stable**, developement ongoing
- [personal-details-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/personal-details-2.0_rc2.xsd)
**stable**, developement ongoing
- [researcher-url-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/researcher-url-2.0_rc2.xsd)
**stable**, developement ongoing
- [work-2.0_rc2.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/work-2.0_rc2.xsd)
**stable**, developement ongoing

##Changes:
###Person section

- Addition of [person section](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/person-2.0_rc2.xsd) corresponding to the v1.2 orcid-bio field. The children of the person field are:
  - [address](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/address-2.0_rc2.xsd)
  - [email](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/email-2.0_rc2.xsd)
  - [external-identifiers](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/person-external-identifier-2.0_rc2.xsd)
  - [keywords](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/keyword-2.0_rc2.xsd)
  - [other-names](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/other-name-2.0_rc2.xsd)
  - [personal-details](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/personal-details-2.0_rc2.xsd)
  - [researcher-urls](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/researcher-url-2.0_rc2.xsd)
- Source and creation date is captured with other-names, country, keywords, researcher-urls, and external identifiers.
- Display index is returned with repeatable person fields
- Address (county) is repeatable
- API 2.0_rc2 can not be used to edit non-repeatable person fields: give-name, family-name, biography. 

###External identifiers

 - Identifiers now use the common schema element *external-id* this affects how identifiers are recorded in the works, funding and peer-review sections
```
<external-id>
    <external-id-type/>
    <external-id-value/>
    <external-id-url/>  
</external-id>
```

###Last modified dates

 - Last-modified-date is now available for every section of the record, in addition to the last-modified-date on individual items.

## Sample XML files:

- [activities-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/activities-2.0_rc2.xml)
- [address-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/address-2.0_rc2.xml)
- [biography-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/biography-2.0_rc2.xml)
- [credit-name-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/credit-name-2.0_rc2.xml)
- [education-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/education-2.0_rc2.xml)
- [email-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/email-2.0_rc2.xml)
- [emails-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/emails-2.0_rc2.xml)
- [employment-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/employment-2.0_rc2.xml)
- [error-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/error-2.0_rc2.xml)
- [external-identifier-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/external-identifier-2.0_rc2.xml)
- [external-identifiers-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/external-identifiers-2.0_rc2.xml)
- [funding-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/funding-2.0_rc2.xml)
- [keyword-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/keyword-2.0_rc2.xml)
- [keywords-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/keywords-2.0_rc2.xml)
- [name-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/name-2.0_rc2.xml)
- [other-name-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/other-name-2.0_rc2.xml)
- [other-names-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/other-names-2.0_rc2.xml)
- [peer-review-2.0_rc2.xml ] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/peer-review-2.0_rc2.xml )
- [person-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/person-2.0_rc2.xml)
- [personal-details-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/personal-details-2.0_rc2.xml)
- [researcher-url-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/researcher-url-2.0_rc2.xml)
- [researcher-urls-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/researcher-urls-2.0_rc2.xml)
- [work-2.0_rc2.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc2/samples/work-2.0_rc2.xml)

**Note:** These files contain system-generated elements/attributes that are returned when reading items from ORCID, but should not be included when posting items to ORCID
- put-code (exception: include put-code when updating items using the PUT method)
- source
- created-date
- last-modified-date

## REST API Reference

### Swagger

The swagger interfaces to the API available at:
- [member](https://api.orcid.org/v2.0_rc2/)
- [public](https://pub.orcid.org/v2.0_rc2/)

### Activities summary
| Item                    | HTTP method | Scope                    | URL                                                      |
|-------------------------|-------------|--------------------------|----------------------------------------------------------|
| Read activities summary | GET         | /read-limited            | https://api.sandbox.orcid.org/v2.0_rc2/[ORCID]/activities |

### Individual activities
| Action             | HTTP method | Scope                    | URL                                                                      |
|--------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Add an activity    | POST        | /activities/update       | https://api.sandbox.orcid.org/v2.0_rc2/[ORCID]/[ACTIVITY-TYPE]            |
| Read an activity   | GET         | /read-limited            | https://api.sandbox.orcid.org/v2.0_rc2/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |
| Update an activity | PUT         | /activities/update       | https://api.sandbox.orcid.org/v2.0_rc2/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |
| Delete an activity | DELETE      | /activities/update       | https://api.sandbox.orcid.org/v2.0_rc2/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |

### Biography details
| Action             | HTTP method | Scope                    | URL                                                                      |
|--------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Add  section       | POST        | /person/update       	  | https://api.sandbox.orcid.org/v2.0_rc2/[ORCID]/[SECTION]            		 |
| Read section       | GET         | /read-limited       	  | https://api.sandbox.orcid.org/v2.0_rc2/[ORCID]/[SECTION]/[PUT-CODE]  	 |
| Update section     | PUT         | /person/update       	  | https://api.sandbox.orcid.org/v2.0_rc2/[ORCID]/[SECTION]/[PUT-CODE]  	 |
| Delete section     | DELETE      | /person/update       	  | https://api.sandbox.orcid.org/v2.0_rc2/[ORCID]/[SECTION]/[PUT-CODE]  	 |



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
| Activities summary | /read-limited            |  /orcid-profile/read-limited                          | ```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/activities'```|
| Address	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/address'```|
| Education          | /read-limited            | /orcid-profile/read-limited /affiliations/read-limited |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/education/14613'```|
| Email     		| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/email'```|
| Employment         | /read-limited            | /orcid-profile/read-limited /affiliations/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/employment/14612'```| 
| External Identifiers	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/external-identifiers'```|
| Funding            | /read-limited            | /orcid-profile/read-limited /funding/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/funding/2629'```|
| Keywords     	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/keywords'```|
| Other names     	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/other-names'```|
| Peer review        | /read-limited            |  NONE  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/peer-review/1579'```|
| Personal details	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/personal-details'```|
| Person	| /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/person'```|
| Researcher URL     | /read-limited            | /orcid-bio/read-limited   						|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/researcher-urls'```|
| Work               | /read-limited            | /orcid-profile/read-limited <br>/orcid-works/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/work/469271'```|

### Add Record Items
|Item    |v2.0 Scope       | v1.2 Scope (deprecated)| Example cURL Statement  |
|---------|----------------|------------------------|-------------------------|
| Address	|/person/update           | /orcid-bio/update   			|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/address.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/address```|
| Education  |/activities/update  |/affiliations/create|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/education' ```|
| Employment |  /activities/update  |/affiliations/create  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/employment' ```| 
| External Identifiers	| /person/update            | /orcid-bio/external-identifiers/create	|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/external_identifier.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/external-identifiers```|
| Funding  |/activities/update  |/funding/create  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/funding'```|
| Keywords     	|/person/update            | /orcid-bio/update   		|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/keyword.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/keywords```|
| Other names  |/person/update|  /orcid-bio/update |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/other-name.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/other-names'```|
| Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/peer-review'```|
| Researcher URL  |/person/update|  /orcid-bio/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/researcher-url.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/researcher-urls'```|
| Work  |/activities/update|  /orcid-works/create  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/work'```|

### Update Record Items
|Item  |v2.0 Scope  |v1.2 Scope (deprecated)| Example cURL Statement  |
|------|----------------|-----------------------|---------------------|
| Address	|/person/update            | /orcid-bio/update   			|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/address.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/address/[PUT-CODE]```|
| Education  |/activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/education/[PUT-CODE]' ```|
| Employment |  /activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/employment/[PUT-CODE]' ```|
| External Identifiers	|/person/update            | /orcid-bio/update 	|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/external_identifier.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/external-identifiers/[PUT-CODE]```|
| Funding  |/activities/update  |/funding/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/funding/[PUT-CODE]'```|
| Keywords     	|/person/update            | /orcid-bio/update   		|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/keyword.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/keywords/[PUT-CODE]```|
| Other names  |/person/update|  /orcid-bio/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/other-name.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/other-names/[PUT-CODE]'```|
| Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/peer-review/[PUT-CODE]'```|
| Researcher URL  |/person/update|  /orcid-bio/update |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/researcher-url.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/researcher-urls/[PUT-CODE]'```|
| Work  |/activities/update|  /orcid-works/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/work/[PUT-CODE]'```|

When updating a record item, include the put code as an attribute in the root element, ex: ```<education:education put-code="14775" xmlns:common="http://www.orcid.org/ns/common" xmlns:education="http://www.orcid.org/ns/education" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.orcid.org/ns/education ../education-2.0_rc2.xsd ">```

### Delete Record Items
|Item  |v2.0 Scope      |v1.2 Scope (deprecated)| Example cURL Statement  |
|------|----------------|-----------------------|-------------------------|
| Address	| /read-limited            | /orcid-bio/update   	|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/address/[PUT-CODE]```|
| Education  |/activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/education/[PUT-CODE]'  ```|
| Employment |  /activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/employment/[PUT-CODE]' ```|
| External Identifiers	| /read-limited    | /orcid-bio/update 	|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/external-identifiers/[PUT-CODE]```|
| Funding  |/activities/update  |/funding/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/funding/[PUT-CODE]'```|
| Keywords     	| /read-limited            | /orcid-bio/update 	|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/keywords/[PUT-CODE]```|
| Other names  |/person/update|  /orcid-bio/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/researcher-urls/[PUT-CODE]'```|
| Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/peer-review/[PUT-CODE]'```|
| Researcher URL  |/person/update|  /orcid-bio/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/researcher-urls/[PUT-CODE]'```|
| Work  |/activities/update|  /orcid-works/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc2/0000-0002-1306-4180/work/[PUT-CODE]'```|


