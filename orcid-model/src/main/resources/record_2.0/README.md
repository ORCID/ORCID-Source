# ORCID API v2.0 Guide

## Current State (Release Stable)
v2.0 is the current release of the ORCID API and should be used for all development.

## XSDs and current state (all stable)
- [activities-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/activities-2.0.xsd) 
**stable**
- [address-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/address-2.0.xsd)
**stable**
- [bulk-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/bulk-2.0.xsd)
**stable**
- [common-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/common_2.0/common-2.0.xsd)
**stable**
- [education-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/education-2.0.xsd)
**stable**
- [email-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/email-2.0.xsd)
**stable**
- [employment-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/employment-2.0.xsd)
**not stable**
- [error-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/error-2.0.xsd)
**stable**
- [external-identifier-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/external-identifier-2.0.xsd)
**stable**
- [funding-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/funding-2.0.xsd)
**stable**
- [keyword-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/keyword-2.0.xsd)
**stable**
- [other-names-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/other-name-2.0.xsd)
**stable**
- [peer-review-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/peer-review-2.0.xsd)
**stable**
- [person-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/person-2.0.xsd)
**stable**
- [personal-details-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/personal-details-2.0.xsd)
**stable**
- [researcher-url-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/researcher-url-2.0.xsd)
**stable**
- [search-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/search-2.0.xsd)
**stable**
- [work-2.0.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/work-2.0.xsd)
**stable**

## Changes from Release candidate 2.0_rc3:
### Search
- Addition of search endpoints

### Peer-review
- Change external-id-type 'PEER-REVIEW' to 'peer-review'
- Addition of 'peer-review service' as a group type for peer-reviews
- Peer-review groups look-up by name option

### Email addresses
- Include primary and verified status when reading an email address

## Changes from Version 1.2
A number of changes to the ORCID API have been made to improve on the existing 1.2 release, this section highlights the most notable changes.

### Multiple XSD
The ORCID Messages Schema has been broken down in to multiple files. Namespaces are now used and common elements are reused in multiple sections.

### Per item API
With version 2.0 the ORCID API information is now added, updated, and read as individual items, as compared to entire sections of a record as was the case with 1.2. Existing items are read, updated or deleted using their put-code, and new items must be posted individually (with the exception of works which can be added up to 100 at a time). To read an entire section of a record the activities summary can be accessed.

### Activities summary:
When reading the works, funding, and peer-review sections, a summary of each activity is returned. The summary is intended to provide basic information about the item, including title, type, date, organization, source and identifiers. The full item can be retrieved by accessing the individual item using the put-code provided.

### Activities group:

Funding, Works and Peer-review items are grouped together based on a common external identifier. In the schema, the group is the parent of the items and contains a last modified date for the group and the identifier(s) used to create the group.

### New attributes for items
- *put-code* attribute on the parent field of an item to uniquely identify that item within the ORCID Registry
- *display-index* indicates the order the researcher has ranked this item within its group or section, higher display indexes appear first. (By deafulat all items added via the API will have their display index set to 0, the display index will update when the order is edited by the researcher.)

### Additional metadata about items on the record 
 - *created date* when the item was first added to the ORCID record
 - *last-modified-date* when the item was changed, such as being edited or having its visibility changed. 
 - *source*  who added the item to the record. Items added via the API will have the *source-client-id* field to record the client iD, items added by the researcher will use the *source-orcid* field to record the researcher's ORCID iD.

### Additional external identifier fields
- *external-id-url* indicates how the identifier will resolve
- *external-id-relationship* indicates the relationship between the item and the identifier

### Peer-review
A new activities section, peer-review, captures the formal review activity of researchers.

### Non-editable fields
The 2.0 API can not be used to edit the fields give-name, family-name or biography. 

### Repeatable address field
With 2.0 the address field can be repeated


## Sample files:

- [read activities-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/activities-2.0.xml)
- [read address-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/address-2.0.xml)
- [write address-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_sample/address-2.0.xml)
- [write bulk-work xml-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_samples/bulk-work-2.0.xml)
- [write bulk-work-2.0.json](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_samples/bulk-work-2.0.json)
- [read biography-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/biography-2.0.xml)
- [read education-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/education-2.0.xml)
- [write education-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_sample/education-2.0.xml)
- [read educations-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/educations-2.0.xml)
- [read email-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/email-2.0.xml)
- [read emails-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/emails-2.0.xml)
- [read employment-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/employment-2.0.xml)
- [write employment-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_sample/employment-2.0.xml)
- [read employments-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/employments-2.0.xml)
- [error-message-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/error-2.0.xml)
- [read external-identifier-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/external-identifier-2.0.xml)
- [write external-identifier-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_sample/external-identifier-2.0.xml)
- [read external-identifiers-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/external-identifiers-2.0.xml)
- [read funding-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/funding-2.0.xml)
- [write funding-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_sample/funding-2.0.xml)
- [read fundings-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/fundings-2.0.xml)
- [read keyword-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/keyword-2.0.xml)
- [write keyword-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_sample/keyword-2.0.xml)
- [keywords-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/keywords-2.0.xml)
- [read other-name-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/other-name-2.0.xml)
- [write other-name-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/other-name-2.0.xml)
- [read other-names-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/other-names-2.0.xml)
- [read peer-review-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/peer-review-2.0.xml)
- [write full peer-review-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_sample/peer-review-full-2.0.xml)
- [write simple peer-review-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_sample/peer-review-simple-2.0.xml)
- [read peer-reviews-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/peer-reviews-2.0.xml)
- [read person-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/person-2.0.xml)
- [read personal-details-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/personal-details-2.0.xml)
- [read researcher-url-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/researcher-url-2.0.xml)
- [write researcher-url-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_sample/researcher-url-2.0.xml)
- [read researcher-urls-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/researcher-urls-2.0.xml)
- [search-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/search-2.0.xml)
- [read work-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/work-2.0.xml)
- [write full work-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_sample/work-full-2.0.xml)
- [write simple work-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/write_sample/work-simple-2.0.xml)
- [read works-2.0.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/works-2.0.xml)

## REST API Reference

### Swagger

The swagger interfaces to the API available at:
- [member](https://api.orcid.org/v2.0/)
- [public](https://pub.orcid.org/v2.0/)

### Scopes
| Scope           | Request method | Obtain Access Token Through	| Expires In | API  |
|-----------------|-------------|--------------------------|----------------------------------|-------------|
| /authenticate     | No API call. Client retrieves access token only.| 3-legged OAuth| Single authentication | Public API and Member API   |
| /activities/update     |POST, PUT, DELETE| 3-legged OAuth| When expired or revoked by user | Member API   |
| /person/update     |POST, PUT, DELETE| 3-legged OAuth| When expired or revoked by user | Member API   |
| /read-limited     |GET| 3-legged OAuth| When expired or revoked by user | Member API   |
| /read-public     |GET| Client credentials| When revoked by ORCID | Public API and Member API  |
| /webhook     |PUT, DELETE| Client credentials| When revoked by ORCID | Premium Member API  |


### Calls
| Action                   | HTTP method | Scope                    | URL                                                      |
|-------------------------|-------------|--------------------------|----------------------------------------------------------|
| Read the entire record | GET       | /read-limited or /read-public | https://[HOST]/v2.0/[ORCID]/record |
| Read an activities section | GET | /read-limited or /read-public | https://[HOST]/v2.0/[ORCID]/[SECTION]s |
| Read a person section | GET | /read-limited or /read-public | https://[HOST]/v2.0/[ORCID]/[SECTION] |
| Read a single item   | GET         | /read-limited or /read-public | https://[HOST]/v2.0/[ORCID]/[SECTION]/[PUT-CODE] |
| Add a person item    | POST        | /person/update       		 | https://[HOST]/v2.0/[ORCID]/[SECTION]            |
| Update a person item | PUT         | /person/update  				 | https://[HOST]/v2.0/[ORCID]/[SECTION]/[PUT-CODE] |
| Delete a person item | DELETE      | /person/update				 | https://[HOST]/v2.0/[ORCID]/[SECTION]/[PUT-CODE] |
| Add an activity    | POST          | /activities/update       | https://[HOST]/v2.0/[ORCID]/[SECTION]            |
| Update an activity | PUT           | /activities/update       | https://[HOST]/v2.0/[ORCID]/[SECTION]/[PUT-CODE] |
| Delete an activity | DELETE        | /activities/update       | https://[HOST]/v2.0/[ORCID]/[SECTION]/[PUT-CODE] |
| Add multiple works| POST           | /read-limited or /read-public | https://[HOST]/v2.0/[ORCID]/works		             |
| Read multiple works| GET          | /activities/update       | https://[HOST]/v2.0/[ORCID]/works/[PUT-CODE1],[PUT-CODE2]  |
| Search records     | GET         | /read-public      	      | https://[HOST]/v2.0/search?q=[SOLR-QUERY]*          	 |

[HOST] is the ORCID environment you are using
- api.sandbox.orcid.org for the Member API on the ORCID Sandbox
- pub.sandbox.orcid.org for the Public API on the ORCID Sandbox (/read-public scope only)
- api.orcid.org for the Member API on the production ORCID Registry
- pub.orcid.org for the Public API on the production ORCID Registry (/read-public scope only)

[ORCID] is the ORCID iD for the record.

[SECTION] can be one of the following:
- activities
- address
- biography *
- education
- email *
- employment
- external-identifiers
- funding
- keywords
- other-names
- researcher-urls
- peer-review
- person *
- personal-details *
- work

**Notes:** 
- biography, email, person and personal-details sections are read only
- when reading multiple works a maximum of 50 works can be accessed with a single call, when posting multiple works a maximum of 100 works can be posted with a single call.
- When searching by default a maximum of 100 results will be returned. The `rows` parameter can be used to increase the number or results, but only up to 200. The `start` parameter (integer pointing to the zero-based position of the first result to be returned) can be used to page through larger results sets. 
- [Help with SOLR searching](https://cwiki.apache.org/confluence/display/solr/The+Standard+Query+Parser)

## Examples

### Read sections

| Item               |Scope               | Example cURL Statement                                         |
|--------------------|--------------------------|----------------------------------------------------------------|
| Entire record | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/record'```|
| Activities summary | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/activities'```|
| Addresses			 | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/address'```|
| Education items    | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/educations'```|
| Emails     		 | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/email'```|
| Employment items   | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/employments'```| 
| External identifiers	| /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/external-identifiers'```|
| Funding summary    | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/fundings'```|
| Keywords     		 | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/keywords'```|
| Other names     	 | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/other-names'```|
| Peer review summary| /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/peer-reviews'```|
| Personal details	 | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/personal-details'```|
| Person			 | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/person'```|
| Researcher URLs    | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/researcher-urls'```|
| Works summary             | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/works'```|


### Read Record Items
| Item               |Scope               | Example cURL Statement                                         |
|--------------------|--------------------------|----------------------------------------------------------------|
| Address			 | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/address/4556'```|
| Biography			 | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/biography'```|
| Education          | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/education/22423'```|
| Employment         | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/employment/22411'```| 
| External identifier| /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/external-identifiers/3193'```|
| Funding            | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/funding/4413'```|
| Keywords     		 | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/keywords/4504'```|
| Other names     	 | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/other-names/15812'```|
| Peer review        | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/peer-review/1374'```|
| Researcher URL     | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/researcher-urls/41387'```|
| Work               | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/work/733536'```|
| Multiple Works     | /read-limited or /read-public|```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/works/733535,733536'```|

When reading multiple works the contents of each work is returned or an error code is returned if there is an issue with a single work.


### Add Record Items
|Item    |Scope       | Example cURL Statement  |
|---------|----------------|-------------------------|
| Address	|/person/update           |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/address.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/address```|
| Education  |/activities/update  |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/education' ```|
| Employment |  /activities/update  |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/employment' ```| 
| External identifiers	| /person/update            |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/external_identifier.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/external-identifiers```|
| Funding  |/activities/update  |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/funding'```|
| Keywords     	|/person/update            |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/keyword.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/keywords```|
| Other names  |/person/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/other-name.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/other-names'```|
| Peer review  |/activities/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/peer-review'```|
| Researcher URL  |/person/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/researcher-url.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/researcher-urls'```|
| Work  |/activities/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/work'```|
| Multiple works |/activities/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/works.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/works'```|

When posting multiple works the contents of each work is returned or an error code is returned if there is an issue with a single work, see [bulk work error sample](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/samples/read_samples/bulk-work-error.xml).

### Update Record Items
|Item  |Scope  | Example cURL Statement  |
|------|----------------|---------------------|
| Address	|/person/update       |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/address.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/address/[PUT-CODE]```|
| Education  |/activities/update  |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/education-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/education/[PUT-CODE]' ```|
| Employment |  /activities/update  |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/employment-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/employment/[PUT-CODE]' ```|
| External identifiers	|/person/update            |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/external_identifier.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/external-identifiers/[PUT-CODE]```|
| Funding  |/activities/update  |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/funding-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/funding/[PUT-CODE]'```|
| Keywords     	|/person/update            |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/keyword.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/keywords/[PUT-CODE]```|
| Other names  |/person/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/other-name.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/other-names/[PUT-CODE]'```|
| Peer review  |/activities/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/peer-review/[PUT-CODE]'```|
| Researcher URL  |/person/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/researcher-url.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/researcher-urls/[PUT-CODE]'```|
| Work  |/activities/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/work/[PUT-CODE]'```|

When updating a record item, include the put code as an attribute in the root element, ex: ```<education:education put-code="14775" xmlns:common="http://www.orcid.org/ns/common" xmlns:education="http://www.orcid.org/ns/education" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.orcid.org/ns/education ../education-2.0.xsd ">```

### Delete Record Items
|Item  | Scope      | Example cURL Statement  |
|------|----------------|-------------------------|
| Address	| /person/update            |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/address/[PUT-CODE]```|
| Education  | /activities/update  |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/education/[PUT-CODE]'  ```|
| Employment |  /activities/update  |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/employment/[PUT-CODE]' ```|
| External identifiers	| /person/update    |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/external-identifiers/[PUT-CODE]```|
| Funding  | /activities/update  |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/funding/[PUT-CODE]'```|
| Keywords     	| /person/update            |```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/keywords/[PUT-CODE]```|
| Other names  | /person/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/other-names/[PUT-CODE]'```|
| Peer review  | /activities/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/peer-review/[PUT-CODE]'```|
| Researcher URL  | /person/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/researcher-urls/[PUT-CODE]'```|
| Work  |/activities/update|```curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.0/0000-0002-9227-8514/work/[PUT-CODE]'```|

### Webhooks
|Item  | Scope      | Example cURL Statement  |
|------|----------------|-------------------------|
| Register a webhook	| /webhook|```curl -v -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X PUT 'http://api.sandbox.orcid.org/0000-0002-9227-8514/webhook/http%3A%2F%2Fencoded.url%2F0000-0002-9227-8514'```|
| Delete a webhook  	| /webhook |```curl -v -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'http://api.sandbox.orcid.org/0000-0002-9227-8514/webhook/http%3A%2F%2Fencoded.url%2F0000-0002-9227-8514'  ```|

### Search
| Item               | Scope               |  Example cURL Statement |
|--------------------|--------------------------|-------------------------------------------------------|
| Search records     | /read-public             | ```curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.0/search?q=Simpson'```|
