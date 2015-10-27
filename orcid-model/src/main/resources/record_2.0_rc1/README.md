#ORCID API v2.0_rc1 Guide

##Sample XML files:

- [activities-2.0_rc1.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/samples/activities-2.0_rc1.xml)
- [education-2.0_rc1.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/samples/education-2.0_rc1.xml)
- [employment-2.0_rc1.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/samples/employment-2.0_rc1.xml)
- [error-2.0_rc1.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/samples/error-2.0_rc1.xml)
- [funding-2.0_rc1.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/samples/funding-2.0_rc1.xml)
- [peer-review-2.0_rc1.xml ] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/samples/peer-review-2.0_rc1.xml )
- [work-2.0_rc1.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/samples/work-2.0_rc1.xml)

**Note:** These files contain system-generated elements/attributes that are returned when reading items from ORCID, but should not be included when posting items to ORCID
- put-code (exception: include put-code when updating items using the PUT method)
- source
- created-date
- last-modified-date

##XSDs:
- [activities-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/activities-2.0_rc1.xsd)
- [common-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/common_2.0_rc1/common-2.0_rc1.xsd)
- [education-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/education-2.0_rc1.xsd)
- [employment-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/employment-2.0_rc1.xsd)
- [error-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/error-2.0_rc1.xsd)
- [funding-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/funding-2.0_rc1.xsd)
- [peer-review-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/peer-review-2.0_rc1.xsd)
- [work-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/work-2.0_rc1.xsd)

##REST API Reference
###Activities summary
| Item                    | HTTP method | Scope                    | URL                                                      |
|-------------------------|-------------|--------------------------|----------------------------------------------------------|
| Read activities summary | GET         | /activities/read-limited | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/activities |

###Individual activities
| Action             | HTTP method | Scope                    | URL                                                                      |
|--------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Add an activity    | POST        | /activities/update       | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/[ACTIVITY-TYPE]            |
| Read an activity   | GET         | /activities/read-limited | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |
| Update an activity | PUT         | /activities/update       | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |
| Delete an activity | DELETE      | /activities/update       | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |

[ORCID] is the ORCID iD for the record.

[ACTIVITY-TYPE] can be one of the following:
- education
- employment
- work
- funding
- peer-review

##Examples
###Read Record Items
| Item               | v2.0 Scope               | v1.2 Scope (deprecated)                               | Example cURL Statement                                         |
|--------------------|--------------------------|-------------------------------------------------------|----------------------------------------------------------------|
| Activities summary | /activities/read-limited |  /orcid-profile/read-limited                          | ```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/activities'```|
| Education          | /activities/read-limited | /orcid-profile/read-limited /affiliations/read-limited |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/education/14613'```|
| Employment         | /activities/read-limited | /orcid-profile/read-limited /affiliations/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/employment/14612'```| 
| Funding            | /activities/read-limited | /orcid-profile/read-limited /funding/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/funding/2629'```|
| Peer review        | /activities/read-limited |  NONE  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/peer-review/1579'```|
| Work               | /activities/read-limited | /orcid-profile/read-limited <br>/orcid-works/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/work/469271'```|

###Add Record Items
|Item    |v2.0 Scope       | v1.2 Scope (deprecated)| Example cURL Statement  |
|---------|----------------|------------------------|-------------------------|
|Education  |/activities/update  |/affiliations/create|```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item.xml' -X POST 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/education' ```|
|Employment |  /activities/update  |/affiliations/create  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item.xml' -X POST 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/employment' ```| 
|Funding  |/activities/update  |/funding/create  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item.xml' -X POST 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/funding'```|
|Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item.xml' -X POST 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/peer-review'```|
|Work  |/activities/update|  /orcid-works/create  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work.xml' -X POST 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/work'```|

###Update Record Items
|Item  |v2.0 Scope  |v1.2 Scope (deprecated)| Example cURL Statement  |
|------|----------------|-----------------------|---------------------|
|Education  |/activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item-updated.xml' -X PUT 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/education/[PUT-CODE]' ```|
|Employment |  /activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item-updated.xml' -X PUT 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/employment/[PUT-CODE]' ```|
|Funding  |/activities/update  |/funding/update  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item-updated.xml' -X PUT 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/funding/[PUT-CODE]'```|
|Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item-updated.xml' -X PUT 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/peer-review/[PUT-CODE]'```|
|Work  |/activities/update|  /orcid-works/update  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work-updated.xml' -X PUT 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/work/[PUT-CODE]'```|

When updating a record item, include the put code as an attribute in the root element, ex:
<education:education put-code="14775" xmlns:common="http://www.orcid.org/ns/common" xmlns:education="http://www.orcid.org/ns/education" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.orcid.org/ns/education ../education-2.0_rc1.xsd ">

###Delete Record Items
|Item  |v2.0 Scope      |v1.2 Scope (deprecated)| Example cURL Statement  |
|------|----------------|-----------------------|-------------------------|
|Education  |/activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/education/[PUT-CODE]'  ```|
|Employment |  /activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/employment/[PUT-CODE]' ```|
|Funding  |/activities/update  |/funding/update  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/funding/[PUT-CODE]'```|
|Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/peer-review/[PUT-CODE]'```|
|Work  |/activities/update|  /orcid-works/update  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/work/[PUT-CODE]'```|
