# ORCID API v2.0_rc1 Guide

## Current State (Release Candidate Stable)
As of 2015-09-01 changes to v2.0_rc1 will be avoided. Further model changes will be expressed in rc_2.
A Release Candidate (RC) is the built to help ORCID and members check if any critical problems have gone 
undetected into the code during the previous development period. Release candidates are NOT suggested for production use.

## XSDs and current state (all stable)
- [activities-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/activities-2.0_rc1.xsd) 
**stable**, further model changes will be expressed in v2.0_rc2
- [common-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/common_2.0_rc1/common-2.0_rc1.xsd)
**stable**, further model changes will be expressed in v2.0_rc2
- [education-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/education-2.0_rc1.xsd)
**stable**, further model changes will be expressed in v2.0_rc2
- [employment-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/employment-2.0_rc1.xsd)
**stable**, further model changes will be expressed in v2.0_rc2
- [error-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/error-2.0_rc1.xsd)
**stable**, further model changes will be expressed in v2.0_rc2
- [funding-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/funding-2.0_rc1.xsd)
**stable**, further model changes will be expressed in v2.0_rc2
- [peer-review-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/peer-review-2.0_rc1.xsd)
**stable**, further model changes will be expressed in v2.0_rc2
- [work-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/work-2.0_rc1.xsd)
**stable**, further model changes will be expressed in v2.0_rc2

## Changes
A number of changes to the ORCID API have been made to improve on the existing 1.2 release, this section highlights the most notable changes.

###Activities summary:
When reading a list of activities, a summary of each activity is returned. The summary is intended to provide basic information about the item, including title, source and identifiers. The full item can be retrieved by accessing the individual item using the path provided, or by using the identifiers to retrieve metadata from another service.

An example work summary is below, fields to note are:

The parent *work-summary* field, which has attributes for the put-code and path to access the individual work. The *visibility* attribute indicates who can access this item and the *display-index* indicates the order the researcher has ranked this item within its group, higher display indexes appear first. By deafulat all items added via the API will have their display index set to 0, the display index will update when the order is edited by the researcher.

```
<work:work-summary put-code="142937" path="/0000-0001-6737-6852/work/142937" visibility="public" display-index="0">
```

The create, modified, and source fields which provide metadata about the item. The *created date* is when the item was first added to the ORCID record, the *last-modified-date* is when the item was changed, such as being edited or having it's visibility changed. The *source* field records who added the item to the record. Items added via the API will have the *source-client-id* field to record the client iD and associated display name. Items added by the researcher will use the *source-orcid* field to record the researcher's ORCID iD and their name.

```
<common:created-date>2016-06-15T17:38:59.907Z</common:created-date>
	<common:last-modified-date>2016-06-15T17:38:59.907Z</common:last-modified-date>
	<common:source>
		<common:source-client-id>
			<common:uri>http://qa.orcid.org/client/APP-5G54N5YFOKGV5Z0X</common:uri>
			<common:path>APP-5G54N5YFOKGV5Z0X</common:path>
			<common:host>orcid.org</common:host>
		</common:source-client-id>
		<common:source-name>ORCID, Inc</common:source-name>
	</common:source>
```

The body of the summary records the basic information about the item including *title*, *type*, *date*, and *external-identifiers* associated with the item.

```
	<work:title>
		<common:title>ORCID: a system to uniquely identify researchers</common:title>
	</work:title>
	<work:external-identifiers>
		<work:work-external-identifier>
			<common:external-identifier-url>dx.doi.org/10.1087/20120404</common:external-identifier-url>
			<common:relationship>self</common:relationship>
			<work:external-identifier-type>doi</work:external-identifier-type>
			<work:external-identifier-id>10.1087/20120404</work:external-identifier-id>
   	 </work:work-external-identifier>
	</work:external-identifiers>
	<work:type>journal-article</work:type>
	<common:publication-date>
		<common:year>2012</common:year>
        <common:month>10</common:month>
       	 <common:day>01</common:day>
    </common:publication-date>
</work:work-summary>
```


###Activities group:

Funding, Works and Peer-review items are grouped together based on a common external identifier. In the schema, the group is the parent of the activity summary and contains a last modified date for the group and the identifier(s) used to create the group. An example work group:

```
<activities:group>
	<common:last-modified-date>2016-06-14T22:44:12.705Z</common:last-modified-date>
	<common:external-ids>
		<common:external-id>
			<common:external-id-type>doi</common:external-id-type>
			<common:external-id-value>10.1087/20120404</common:external-id-value>
			<common:external-id-relationship>self</common:external-id-relationship>
		</common:external-id>
	</common:external-ids>
	<work:work-summary>...</work:work-summary>
</activities:group>
```

###Per item API
With version 2.0_rc1 the ORCID API now requires that information be added, updated and read as individual items, as compared to entire sections of a record as was the case with 1.2. Existing items are read, updated or deleted using their put-code, and new items must be posted individually. To read an entire section of a record the activities summary can be accessed.

###Peer-review

A new activities section, peer-review, captures the formal review activity of researchers. Information on how to add peer-review items is at http://members.orcid.org/api/peer-review-getting-started

###Multiple XSD
The 2.0_rc1 schema has been broken into individual pieces for each section of the ORCID record. As part of this namespaces are now included with the XML record. [Full list of XSDs](#xsds-and-current-state-none-stable)

## Sample XML files:

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


## REST API Reference

### Swagger

The swagger interfaces to the API available at:
- [member](https://api.orcid.org/v2.0_rc1/)
- [public](https://pub.orcid.org/v2.0_rc1/)

### Activities summary
| Item                    | HTTP method | Scope                    | URL                                                      |
|-------------------------|-------------|--------------------------|----------------------------------------------------------|
| Read activities summary | GET         | /read-limited | https://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/activities |

### Individual activities
| Action             | HTTP method | Scope                    | URL                                                                      |
|--------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Add an activity    | POST        | /activities/update       | https://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/[ACTIVITY-TYPE]            |
| Read an activity   | GET         | /read-limited | https://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |
| Update an activity | PUT         | /activities/update       | https://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |
| Delete an activity | DELETE      | /activities/update       | https://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |

[ORCID] is the ORCID iD for the record.

[ACTIVITY-TYPE] can be one of the following:
- education
- employment
- work
- funding
- peer-review

## Examples
### Read Record Items
| Item               | v2.0 Scope               | v1.2 Scope (deprecated)                               | Example cURL Statement                                         |
|--------------------|--------------------------|-------------------------------------------------------|----------------------------------------------------------------|
| Activities summary | /read-limited            |  /orcid-profile/read-limited                          | ```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/activities'```|
| Education          | /read-limited            | /orcid-profile/read-limited /affiliations/read-limited |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/education/14613'```|
| Employment         | /read-limited            | /orcid-profile/read-limited /affiliations/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/employment/14612'```| 
| Funding            | /read-limited            | /orcid-profile/read-limited /funding/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/funding/2629'```|
| Peer review        | /read-limited            |  NONE  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/peer-review/1579'```|
| Work               | /read-limited            | /orcid-profile/read-limited <br>/orcid-works/read-limited  |```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/work/469271'```|

### Add Record Items
|Item    |v2.0 Scope       | v1.2 Scope (deprecated)| Example cURL Statement  |
|---------|----------------|------------------------|-------------------------|
|Education  |/activities/update  |/affiliations/create|```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/education' ```|
|Employment |  /activities/update  |/affiliations/create  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/employment' ```| 
|Funding  |/activities/update  |/funding/create  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/funding'```|
|Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/peer-review'```|
|Work  |/activities/update|  /orcid-works/create  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/work'```|

### Update Record Items
|Item  |v2.0 Scope  |v1.2 Scope (deprecated)| Example cURL Statement  |
|------|----------------|-----------------------|---------------------|
|Education  |/activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/education/[PUT-CODE]' ```|
|Employment |  /activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/employment/[PUT-CODE]' ```|
|Funding  |/activities/update  |/funding/update  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/funding/[PUT-CODE]'```|
|Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/peer-review/[PUT-CODE]'```|
|Work  |/activities/update|  /orcid-works/update  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/work/[PUT-CODE]'```|

When updating a record item, include the put code as an attribute in the root element, ex: ```
<education:education put-code="14775" xmlns:common="http://www.orcid.org/ns/common" xmlns:education="http://www.orcid.org/ns/education" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.orcid.org/ns/education ../education-2.0_rc1.xsd "> ```

### Delete Record Items
|Item  |v2.0 Scope      |v1.2 Scope (deprecated)| Example cURL Statement  |
|------|----------------|-----------------------|-------------------------|
|Education  |/activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/education/[PUT-CODE]'  ```|
|Employment |  /activities/update  |/affiliations/update|```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/employment/[PUT-CODE]' ```|
|Funding  |/activities/update  |/funding/update  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/funding/[PUT-CODE]'```|
|Peer review  |/activities/update|  NONE  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/peer-review/[PUT-CODE]'```|
|Work  |/activities/update|  /orcid-works/update  |```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/work/[PUT-CODE]'```|
