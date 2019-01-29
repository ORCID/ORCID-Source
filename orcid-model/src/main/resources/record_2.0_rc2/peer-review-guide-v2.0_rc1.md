
#ORCID API v2.0_rc1 Peer Review Guide


### This guide is deprecated please refer to latest guide at [Here](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/tutorial/peer-review.md).


Starting in v2.0_rc1, the ORCID API now supports a new activity type: ```peer-review```.

The ```peer-review``` activity type is intended to allow for recognition of and exchange of data about peer review service contributed by researchers.

The ```peer-review``` activity type follows the [CASRAI Peer Review Services data profile](http://dictionary.casrai.org/Peer_Review_Services), which was developed by the [Peer Review Services Working Group (PRS-WG)](http://casrai.org/standards/subject-groups/peer-review-services), led by [ORCID](http://orcid.org) and [F1000](http://f1000.com/). More details about ORCID's implementation of this recommendation, and the Early Adopter program for Peer Review can be found on the [Peer Review Early Adopter page](http://orcid.org/content/peer-review-early-adopter-program).

##Peer Review XML
XML for the ```peer-review``` activity follows the [peer-review-2.0_rc1.xsd](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/peer-review-2.0_rc1.xsd) and consists of the following sections:

###The fields

**DESCRIBING THE REVIEWER**

- **reviewer-role** _(required)_ The role played by a person in their contribution to a review. This field is selected from a list containing the following values: _chair, editor, member, organizer, reviewer_

**DESCRIBING THE REVIEW**

- **reivew-identifiers** _(required)_ Unique identifier(s) of the review. *This identifier refers to the review itself, NOT to the item that was reviewed.* At least one identifier is required. In the case where there is no persistent unique identifier for the review, the source providing the data should generate a locally-sourced unique identifier for the review (e.g., type "organization-defined-type"). This field will be checked when adding new reviews to prevent double counting of review activity.

- **review-url** _(optional)_ A link to a representation of the review on the web. *This URL refers to the review itself, NOT to the item that was reviewed.*

- **review-type** _(required)_ The kind of review applied to the subject type reviewed. This field is selected from a list containing the following values: _evaluation, review_


- **review-completion-date** _(required)_ The date on which the review was completed (formatted to ISO 8601). Allowable values: yyyy; yyyy-mm; yyyy-mm-dd, formatted using the ORCID fuzzy date format.

- **review-group-id** _(required)_ Identifier for the group that this review should be a part of for aggregation purposes. The Group ID must be pre-registered before use. (see [Group ID Registration]() below for more information.)

**DESCRIBING THE SUBJECT OF THE REVIEW**

- **subject-external-identifier** _(optional)_ The unique ID of the object that was reviewed. *This identifier refers to the SUBJECT of the review, not of the review itself.*

- **subject-container-name** _(optional)_ The name of the journal, conference, grant review panel, or other applicable object of which the review subject was a part.

- **subject-type** _(optional)_ The type of object that the review subject is (for example, a journal article, grant, etc)

- **subject-name** _(optional)_ The name/title of the subject object that was reviewed.

- **subject-url** _(optional)_ The URL of the subject object that was reviewed. *This URL points to the SUBJECT of the review, not to the review itself.*

**DESCRIBING THE ORGANIZATION THAT THE REVIEW WAS DONE FOR**

- **convening-organization** _(required)_ Information about the organization convening the review (journal publisher, conference organizer, funding agency, etc) . Whenever possible, this organization is identified by a unique identifier like the Ringgold ID or FundRef ID.

###Example file

For an example XML file, see [peer-review-2.0_rc1.xml](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0_rc1/samples/peer-review-2.0_rc1.xml )

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
| Read activities summary | GET         | /activities/read-limited | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID-iD]/activities |

###Read/Modifiy Peer Review Activities
| Action             | HTTP method | Scope                    | URL                                                                      |
|--------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Add peer-review item    | POST        | /activities/update       | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID-iD]/peer-review            |
| Read peer-review item   | GET         | /activities/read-limited | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID-iD]/peer-review/[PUT-CODE] |
| Update peer-review item | PUT         | /activities/update       | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID-iD]/peer-review/[PUT-CODE] |
| Delete peer-review item | DELETE      | /activities/update       | http://api.sandbox.orcid.org/v2.0_rc1/[ORCID-iD]/peer-review/[PUT-CODE] |


- **[ORCID-iD]** is the ORCID iD for the record, formatted as XXXX-XXXX-XXXX-XXXX
- **[PUT-CODE]** is the ```put-code``` attribute for the specific ```peer-review``` activity that you wish to read or modify.

###Example cURL Statements
####Read Activities Summary

```shell
curl -i -H "Accept: application/orcid+xml" \
	-H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' \
	'http://api.sandbox.orcid.org/v2.0_rc1/0000-0002-1306-4180/activities'
```

####Add Peer-Review Activity

```shell
curl -i -H 'Content-type: application/orcid+xml’ \
	-H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' \
	-d '@[FILE-PATH]/peer-review-item.xml' \
	-X POST 'http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review'
```

####Read Peer-Review Activity

```
curl -i -H "Accept: application/orcid+xml" \
	-H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' \
	'http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review/[PUT-CODE]'
```

####Update Peer-Review Activity

```shell
curl -i -H 'Content-type: application/orcid+xml’ \
	-H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' \
	-d '@[FILE-PATH]/peer-review-item-updated.xml' \
	-X PUT 'http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review/[PUT-CODE]'
```

####Delete Peer-Review Activity

```shell
curl -i -H 'Content-type: application/orcid+xml’ \
	-H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' \
	-X DELETE 'http://api.sandbox.orcid.org/v2.0_rc1/[ORCID]/peer-review/[PUT-CODE]'
```
