
#ORCID API v2.0_rc1 Peer Review Guide
Starting in v2.0_rc1, the ORCID API now supports a new activity type: ```peer-review```. 

The ```peer-review``` activity type is intended to allow for recognition of and exchange of data about peer review service contributed by researchers. 

The ```peer-review``` activity type follows the [CASRAI Peer Review Services data profile](http://dictionary.casrai.org/Peer_Review_Services), which was developed by the [Peer Review Services Working Group (PRS-WG)](http://casrai.org/standards/subject-groups/peer-review-services), led by [ORCID](http://orcid.org) and [F1000](http://f1000.com/).

##Peer Review XML
XML for the ```peer-review``` activity follows the [peer-review-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/peer-review-2.0_rc1.xsd) and consists of the following sections:

- ```<peer-review:role>```
The role played by a person in their contribution to a review. 

- ```<peer-review:organization>``` 
Information about the organization (journal publisher, conference organizer, funding agency, etc) convening the review

- ```<peer-review:external-identifiers>```
The unique locally generated identifier of the review. **IMPORTANT**: This identifier refers to the review itself, **NOT** to the manuscript, application, etc that was reviewed.

- ```<peer-review:url>```
A link to a representation of the review on the web. **IMPORTANT**: This URL refers to the review itself, **NOT** to the manuscript, application, etc that was reviewed.

- ```<peer-review:type>```
The kind of review applied to the subject type reviewed. 

- ```<peer-review:completion-date>```
The day of the month on which the review was completed (formatted to ISO 8601). 

- ```<peer-review:subject>```
Information about the item (journal article, conference paper, funding application, etc) that was reviewed.

For an example, see [peer-review-2.0_rc1.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/samples/peer-review-2.0_rc1.xml )

***Note:*** *Sample files contain system-generated elements/attributes that are returned when reading items from ORCID. The following items should not be included when posting items to ORCID:*

- *put-code (exception: include put-code when updating items using the PUT method)*
- *source*
- *created-date*
- *last-modified-date*


##Peer Review API Reference
```peer-review``` is available only in ORCID API v2.0_rcX, which uses a slightly different data structure from previous API versions. 

In v2.0_rcX, activities are read, added, and modified on an individual basis (rather than as a list), using a ```put-code```, which is a system-generated identifier used within the ORCID database.

The ```put-code``` for a specific item can be obtained by reading a summary of a user's ORCID record.

Other notable differences between v2.0_rcX previous versions include:

- Only 2 scopes are used: ```/activities/read-limited``` and ```/activities/update```
- An explicit ```DELETE``` method is used to remove record items

###Read Activities Summary
| Action                   | HTTP method | Scope                    | URL                                                      |
|-------------------------|-------------|--------------------------|----------------------------------------------------------|
| Read activities summary | GET         | /activities/read-limited | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/activities |

###Read/Modifiy Peer Review Activities
| Action             | HTTP method | Scope                    | URL                                                                      |
|--------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Add peer-review item    | POST        | /activities/update       | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review            |
| Read peer-review item   | GET         | /activities/read-limited | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review/[PUT-CODE] |
| Update peer-review item | PUT         | /activities/update       | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review/[PUT-CODE] |
| Delete peer-review item | DELETE      | /activities/update       | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review/[PUT-CODE] |


- **[ORCID]** is the ORCID iD for the record, formatted as XXXX-XXXX-XXXX-XXXX
- **[PUT-CODE]** is the ```put-code``` attribute for the specific ```peer-review``` activity that you wish to read or modify.

###Example cURL Statements
####Read Activities Summary
```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/activities'```
####Add Peer-Review Activity
```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item.xml' -X POST 'http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review'```
####Read Peer-Review Activity
```curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review/[PUT-CODE]'```
####Update Peer-Review Activity
```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/peer-review-item-updated.xml' -X PUT 'http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review/[PUT-CODE]'```
####Delete Peer-Review Activity
```curl -i -H 'Content-type: application/orcid+xml’ -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review/[PUT-CODE]'```




