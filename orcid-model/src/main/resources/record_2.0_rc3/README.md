# ORCID API v2.0_rc3 Guide

## Current State (Release Candidate Stable)
As of 2016-11-01 changes to v2.0_rc3 will be avoided. Further model changes will be expressed in rc_4. A Release Candidate (RC) is the built to help ORCID and members check if any critical problems have gone undetected into the code during the previous development period. Release candidates are NOT suggested for production use.

## XSDs and current state (all stable)
- [activities-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/activities-2.0_rc3.xsd) 
**stable**
- [address-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/address-2.0_rc3.xsd)
**stable**
- [bulk-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/bulk-2.0_rc3.xsd)
**stable**
- [common-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/common_2.0_rc3/common-2.0_rc3.xsd)
**stable**
- [education-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/education-2.0_rc3.xsd)
**stable**
- [email-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/email-2.0_rc3.xsd)
**stable**
- [employment-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/employment-2.0_rc3.xsd)
**stable**
- [error-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/error-2.0_rc3.xsd)
**stable**
- [external-identifier-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/external-identifier-2.0_rc3.xsd)
**stable**
- [funding-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/funding-2.0_rc3.xsd)
**stable**
- [history-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/history-2.0_rc3.xsd)
**stable**
- [keyword-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/keyword-2.0_rc3.xsd)
**stable**
- [other-names-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/other-name-2.0_rc3.xsd)
**stable**
- [peer-review-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/peer-review-2.0_rc3.xsd)
**stable**
- [person-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/person-2.0_rc3.xsd)
**stable**
- [person-external-identifier-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/person-external-identifier-2.0_rc3.xsd)
**stable**
- [personal-details-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/personal-details-2.0_rc3.xsd)
**stable**
- [researcher-url-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/researcher-url-2.0_rc3.xsd)
**stable**
- [work-2.0_rc3.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/work-2.0_rc3.xsd)
**stable**

##Changes:
###Post bulk works

- Addition of endpoint /works for bulk posting works
- Update to schema of bulk element in works
- Update to schema of error section for returning errors with bulk added works

###Read activities sections
- Additional endpoints for reading a single activity section
	- /educations
	- /employments
	- /fundings
	- /peer-reviews
	- /works
	
### Work:citation value
- Update the field for text citations from <work:citation> to <work:citation-value> (this removes confusion with the double use of the <work:citation> tag)

## Sample files:

- [activities-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/activities-2.0_rc3.xml)
- [address-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/address-2.0_rc3.xml)
- [bulk-work-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/bulk-work-2.0_rc3.xml)
- [bulk-work-2.0_rc3.json] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/bulk-work-2.0_rc3.json)
- [biography-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/biography-2.0_rc3.xml)
- [credit-name-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/credit-name-2.0_rc3.xml)
- [education-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/education-2.0_rc3.xml)
- [email-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/email-2.0_rc3.xml)
- [emails-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/emails-2.0_rc3.xml)
- [employment-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/employment-2.0_rc3.xml)
- [error-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/error-2.0_rc3.xml)
- [external-identifier-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/external-identifier-2.0_rc3.xml)
- [external-identifiers-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/external-identifiers-2.0_rc3.xml)
- [funding-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/funding-2.0_rc3.xml)
- [keyword-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/keyword-2.0_rc3.xml)
- [keywords-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/keywords-2.0_rc3.xml)
- [name-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/name-2.0_rc3.xml)
- [other-name-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/other-name-2.0_rc3.xml)
- [other-names-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/other-names-2.0_rc3.xml)
- [peer-review-2.0_rc3.xml ] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/peer-review-2.0_rc3.xml )
- [person-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/person-2.0_rc3.xml)
- [personal-details-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/personal-details-2.0_rc3.xml)
- [researcher-url-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/researcher-url-2.0_rc3.xml)
- [researcher-urls-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/researcher-urls-2.0_rc3.xml)
- [work-2.0_rc3.xml] (https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc3/samples/work-2.0_rc3.xml)

**Note:** These files contain system-generated elements/attributes that are returned when reading items from ORCID, but should not be included when posting items to ORCID
- put-code (exception: include put-code when updating items using the PUT method)
- source
- created-date
- last-modified-date

## REST API Reference

### Swagger

The swagger interfaces to the API available at:
- [member](https://api.orcid.org/v2.0_rc3/)
- [public](https://pub.orcid.org/v2.0_rc3/)

### Scopes
| Scope           | Request method | Obtain Access Token Through	| Expires In | API  |
|-----------------|-------------|--------------------------|----------------------------------|-------------|
| /authenticate     | No API call. Client retrieves access token only.| 3-legged OAuth| Single authentication | Public API and Member API   |
| /activities/update     |POST, PUT, DELETE| 3-legged OAuth| When expired or revoked by user | Member API   |
| /person/update     |POST, PUT, DELETE| 3-legged OAuth| When expired or revoked by user | Member API   |
| /read-limited     |GET| 3-legged OAuth| When expired or revoked by user | Member API   |
| /read-public     |GET| Client credentials| When revoked by ORCID | Public API and Member API  |
| /webhooks     |PUT, DELETE| Client credentials| When revoked by ORCID | Premium Member API  |

### Record summary
| Item                    | HTTP method | Scope                    | URL                                                      |
|-------------------------|-------------|--------------------------|----------------------------------------------------------|
| Read record summary     | GET         | /read-limited or /read-public| https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/record    |

### Activities summary
| Item                    | HTTP method | Scope                    | URL                                                      |
|-------------------------|-------------|--------------------------|----------------------------------------------------------|
| Read activities summary | GET         |  /read-limited or /read-public| https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/activities|
| Read activities sections  | GET         | /read-limited or /read-public| https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/[ACTIVITY-TYPE]s          |
| Add multiple works		     | POST        | /activities/update       | https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/works		             |

### Biography summary
| Item                    | HTTP method | Scope                    | URL                                                      |
|-------------------------|-------------|--------------------------|----------------------------------------------------------|
| Read biography summary  | GET         | /read-limited or /read-public| https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/person    |
| Read biography sections | GET         |  /read-limited or /read-public| https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/person    |

### Activities items
| Action             | HTTP method | Scope                    | URL                                                                      |
|-------------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Add an activity    | POST        | /activities/update       | https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/[ACTIVITY-TYPE]            |
| Read an activity   | GET         | /read-limited or /read-public| https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |
| Update an activity | PUT         | /activities/update       | https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |
| Delete an activity | DELETE      | /activities/update       | https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/[ACTIVITY-TYPE]/[PUT-CODE] |

### Biography items
| Action             | HTTP method | Scope                    | URL                                                                      |
|-------------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Add  an item       | POST        | /person/update       	  | https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/[SECTION]            	 |
| Read an item       | GET         | /read-limited or /read-public| https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/[SECTION]/[PUT-CODE]  	 |
| Update an item     | PUT         | /person/update       	  | https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/[SECTION]/[PUT-CODE]  	 |
| Delete an item     | DELETE      | /person/update       	  | https://api.sandbox.orcid.org/v2.0_rc3/[ORCID]/[SECTION]/[PUT-CODE]  	 |



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
- personal-details *

**Note:** email and personal-details sections are only for reading purposes

## Examples

### Read sections

| Item               |Scope               | Example cURL Statement                                         |
|--------------------|--------------------------|----------------------------------------------------------------|
| Activities summary | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/activities'```|
| Addresses			 | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/address'```|
| Education items    | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/educations'```|
| Emails     		 | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/email'```|
| Employment items   | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/employments'```| 
| External Identifiers	| /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/external-identifiers'```|
| Funding summary    | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/fundings'```|
| Keywords     		 | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/keywords'```|
| Other names     	 | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/other-names'```|
| Peer review summary| /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/peer-reviews'```|
| Personal details	 | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/personal-details'```|
| Person			 | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/person'```|
| Researcher URLs    | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/researcher-urls'```|
| Works              | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/works'```|


### Read Record Items
| Item               |Scope               | Example cURL Statement                                         |
|--------------------|--------------------------|----------------------------------------------------------------|
| Address			 | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/address/4556'```|
| Education          | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/education/22423'```|
| Employment         | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/employment/22411'```| 
| External Identifier| /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/external-identifiers/3193'```|
| Funding            | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/funding/4413'```|
| Keywords     		 | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/keywords/4504'```|
| Other names     	 | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/other-names/15812'```|
| Peer review        | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/peer-review/1374'```|
| Researcher URL     | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/researcher-urls/41387'```|
| Work               | /read-limited or /read-public|```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/work/733536'```|

### Add Record Items
|Item    |Scope       | Example cURL Statement  |
|---------|----------------|-------------------------|
| Address	|/person/update           |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/address.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/address```|
| Education  |/activities/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/education' ```|
| Employment |  /activities/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/employment' ```| 
| External Identifiers	| /person/update            |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/external_identifier.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/external-identifiers```|
| Funding  |/activities/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/funding'```|
| Keywords     	|/person/update            |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/keyword.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/keywords```|
| Other names  |/person/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/other-name.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/other-names'```|
| Peer review  |/activities/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/peer-review'```|
| Researcher URL  |/person/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/researcher-url.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/researcher-urls'```|
| Work  |/activities/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/work'```|
| Bulk works  |/activities/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/works.xml' -X POST 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/works'```|

### Update Record Items
|Item  |Scope  | Example cURL Statement  |
|------|----------------|---------------------|
| Address	|/person/update       |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/address.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/address/[PUT-CODE]```|
| Education  |/activities/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/education/[PUT-CODE]' ```|
| Employment |  /activities/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/employment/[PUT-CODE]' ```|
| External Identifiers	|/person/update            |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/external_identifier.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/external-identifiers/[PUT-CODE]```|
| Funding  |/activities/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/funding/[PUT-CODE]'```|
| Keywords     	|/person/update            |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/keyword.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/keywords/[PUT-CODE]```|
| Other names  |/person/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/other-name.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/other-names/[PUT-CODE]'```|
| Peer review  |/activities/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/peer-review/[PUT-CODE]'```|
| Researcher URL  |/person/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/researcher-url.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/researcher-urls/[PUT-CODE]'```|
| Work  |/activities/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/work/[PUT-CODE]'```|

When updating a record item, include the put code as an attribute in the root element, ex: ```<education:education put-code="14775" xmlns:common="http://www.orcid.org/ns/common" xmlns:education="http://www.orcid.org/ns/education" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.orcid.org/ns/education ../education-2.0_rc3.xsd ">```

### Delete Record Items
|Item  | Scope      | Example cURL Statement  |
|------|----------------|-------------------------|
| Address	| /person/update            |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/address/[PUT-CODE]```|
| Education  | /activities/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/education/[PUT-CODE]'  ```|
| Employment |  /activities/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/employment/[PUT-CODE]' ```|
| External Identifiers	| /person/update    |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/external-identifiers/[PUT-CODE]```|
| Funding  | /activities/update  |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/funding/[PUT-CODE]'```|
| Keywords     	| /person/update            |```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/keywords/[PUT-CODE]```|
| Other names  | /person/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/researcher-urls/[PUT-CODE]'```|
| Peer review  | /activities/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/peer-review/[PUT-CODE]'```|
| Researcher URL  | /person/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/researcher-urls/[PUT-CODE]'```|
| Work  |/activities/update|```curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0_rc3/0000-0002-9227-8514/work/[PUT-CODE]'```|

### Webhooks
|Item  | Scope      | Example cURL Statement  |
|------|----------------|-------------------------|
| Register a webhook	| /webhook|```curl -v -H 'Authorization: Bearer 64cea88d-b600-48aa-af4c-690b7fb56bc1' -X PUT 'http://api.sandbox.orcid.org/0000-0002-9227-8514/webhook/http%3A%2F%2Fencoded.url%2F0000-0002-9227-8514'```|
| Delete a webhook  	| /webhook |```curl -v -H 'Authorization: Bearer 64cea88d-b600-48aa-af4c-690b7fb56bc1' -X DELETE 'http://api.sandbox.orcid.org/0000-0002-9227-8514/webhook/http%3A%2F%2Fencoded.url%2F0000-0002-9227-8514'  ```|


