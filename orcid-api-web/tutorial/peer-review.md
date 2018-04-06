# Write, update and delete peer-review items

This tutorial goes over editing information in the peer-review section of an ORCID record. The ```peer-review``` activity type is intended to allow for recognition of and exchange of data about peer review service contributed by researchers. 

The ```peer-review``` activity type follows the [CASRAI Peer Review Services data profile](http://dictionary.casrai.org/Peer_Review_Services).

Peer-review items can only be added and updated by clients. Users can delete but not add or edit peer-reviews.

This workflow can be used with Member API credentials on sandbox or the production servers.

## Overview

**Scopes:** ```/activities/update``` and ```/read-limited``` and ```/group-id-record/update``` for peer-review groups.

**Method:** [3 step OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect) for posting peer-review items, [2 step authorization](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#generate-a-two-step-read-public-access-token) for creating peer-review groups

**Endpoints:** ```/peer-reviews``` and ```/peer-review``` for peer-review items, ```/group-id-record/update``` for peer-review groups

**Sample XML:**
  * [reading the peer-review section summary](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/read_samples/peer-reviews-3.0_dev1.xml)
  * [reading a basic peer-review item](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/read_samples/peer-review-3.0_dev1.xml)
  * [reading a detailed peer-review item](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/read_samples/peer-review-full-3.0_dev1.xml)
  * [writing a peer-reivew item with the mininal information](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/write_samples/peer-review-simple-3.0_dev1.xml)
  * [writing a peer-reivew item with the detailed information](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/write_samples/peer-review-full-3.0_dev1.xml)

## Permission to edit the record
Editing the peer-review section of a record requires a 3 step OAuth token with the ```/activities/update``` scope, the ```/read-limited``` scope should also be requested for reading peer-review activities. See [Authentciating using OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect) for steps to obtain a token.

## Create a peer-review group
Peer-review items are grouped on ORCID records based on who or what the reivew was done for this can be an organization, a publication or other. We suggest searching existing peer-review groups before creating new ones to avoid duplicate entries.

### Get a token to create a peer-review group
Tokens to create peer-review groups are issued via the [2 step token process](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#generate-a-two-step-read-public-access-token).

Send a request to the ORCID API for a two step token

| Item              |Parameter               |
|-------------------|--------------------------|
| URL 				| https://sandbox.orcid.org/oauth/token|
| client\_id 		| *Your client ID*|
| client\_secret	| *Your client secret*|
| grant\_type		| client\_credentials|
| scope				| /group-id-record/update|


**Example request in curl**

```
curl -i -L -H 'Accept: application/json' -d 'client_id=APP-674MCQQR985VZZQ2' -d 'client_secret=d08b711e-9411-788d-a474-46efd3956652' -d 'scope=group-id-record/update' -d 'grant_type=client_credentials' 'https://sandbox.orcid.org/oauth/token'
```

Example response:
```
{"access_token":"1cecf036-5ced-4d04-8eeb-61fa6e3b32ee","token_type":"bearer","refresh_token":"81hbd686-7aa9-4c52-b8db-51fd8370ccf4","expires_in":631138518,"scope":"group-id-record/update","orcid":null}
```

### Peer-review group fields

See sample [peer-review-group](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/group-id-3.0_dev1/samples/group-id-3.0_dev1.xml)

- **group-id:name** _(required)_ The name of the group. This will display on the user's ORCID record.
- **group-id:group-id** _(required)_ An identifier for the group formated as identifier type:identifier. Identifier type options are fundref, issn, orcid-generated, publons, ringgold.  
- **group-id:description** _(required)_ A brief description of the group. This will display on the user's ORCID record.
- **group-id:type** _(required)_ The type of object or organization of the group. This will display on the user's ORCID record.

### Create, update, read and delete peer-review groups

| Action             | HTTP method | Scope                    | URL                                                                      |
|--------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Read all peer-review groups   | GET         | /group-id-record/update| https://api.sandbox.orcid.org/v3.0_dev1/group-id-record/?page-size=10&page=1 |
| Read one peer-review group   | GET         | /group-id-record/update| https://api.sandbox.orcid.org/v3.0_dev1/group-id-record/[PUT-CODE] |
| Add new peer-review group    | POST        | /group-id-record/update| https://api.sandbox.orcid.org/v3.0_dev1/group-id-record            |
| Update peer-review group | PUT         | /group-id-record/update| https://api.sandbox.orcid.org/v3.0_dev1/group-id-record/[PUT-CODE] |
| Delete peer-review group | DELETE*      | /group-id-record/update| https://api.sandbox.orcid.org/v3.0_dev1/group-id-record/[PUT-CODE] |
| Search for a peer-review group by name | GET         | /group-id-record/update| https://api.sandbox.orcid.org/v3.0_dev1/?name=Name+you+are+searching+for&page-size=10&page=1 |

*Peer-review groups that are referenced by existing peer-review items can not be deleted.

**Curl example to post a new group**
```
curl -H 'Content-Type: application/vnd.orcid+xml' -H 'Authorization: Bearer 1cecf036-5ced-4d04-8eeb-61fa6e3b32ee' -d '@group.xml' -X POST 'https://api.sandbox.orcid.org/v3.0_dev1/group-id-record'
```

Example response
```
HTTP/1.1 201 Created
...
Location: http://api.sandbox.orcid.org/v3.0_dev1/group-id-record/1348
```

## Add, update, read and delete peer-review items

### Peer-review fields

See the peer-review items in the [sample files](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_3.0_dev1/samples) for the latest schema version.

**Describing the reviewer**

- **reviewer-role** _(required)_ The role played by a person in their contribution to a review. This field is selected from a list containing the following values: _chair, editor, member, organizer, reviewer_

**Describing the review**

- **review-identifiers** _(required)_ Unique identifier(s) of the review. *This identifier refers to the review itself, NOT to the item that was reviewed.* At least one identifier is required. In the case where there is no persistent unique identifier for the review, the source providing the data should generate a locally-sourced unique identifier for the review (e.g., type "organization-defined-type"). This field will be checked when adding new reviews to prevent double counting of review activity.

- **review-url** _(optional)_ A link to a representation of the review on the web. *This URL refers to the review itself, NOT to the item that was reviewed.*

- **review-type** _(required)_ The kind of review applied to the subject type reviewed. This field is selected from a list containing the following values: _evaluation, review_

- **review-completion-date** _(required)_ The date on which the review was completed (formatted to ISO 8601). Allowable values: yyyy; yyyy-mm; yyyy-mm-dd, formatted using the ORCID fuzzy date format. 

- **review-group-id** _(required)_ Identifier for the group that this review should be a part of for aggregation purposes. The Group ID must be pre-registered before use. (see [Group ID Registration]() below for more information.) 

**Describing the subject of the reivew**

- **subject-external-identifier** _(optional)_ The unique ID of the object that was reviewed. *This identifier refers to the SUBJECT of the review, not of the review itself.*

- **subject-container-name** _(optional)_ The name of the journal, conference, grant review panel, or other applicable object of which the review subject was a part.

- **subject-type** _(optional)_ The type of object that the review subject is (for example, a journal article, grant, etc)

- **subject-name** _(optional)_ The name/title of the subject object that was reviewed.

- **subject-url** _(optional)_ The URL of the subject object that was reviewed. *This URL points to the SUBJECT of the review, not to the review itself.*

**Describing the organization the review was done for**

- **convening-organization** _(required)_ Information about the organization convening the review (journal publisher, conference organizer, funding agency, etc.). Whenever possible, this organization is identified by a unique identifier like the Ringgold ID or FundRef ID.


## Read, write, update and delete peer-review items

### Peer Review Calls

| Action             | HTTP method | Scope                    | URL                                                                      |
|--------------------|-------------|--------------------------|--------------------------------------------------------------------------|
| Read all peer-review items   | GET         | /read-limited or /read-public| https://api.sandbox.orcid.org/v3.0_dev1/[ORCID-iD]/peer-reviews |
| Read one peer-review item   | GET         | /read-limited or /read-public| https://api.sandbox.orcid.org/v3.0_dev1/[ORCID-iD]/peer-review/[PUT-CODE] |
| Add peer-review item    | POST        | /activities/update       | https://api.sandbox.orcid.org/v3.0_dev1/[ORCID-iD]/peer-review            |
| Update peer-review item | PUT         | /activities/update       | https://api.sandbox.orcid.org/v3.0_dev1/[ORCID-iD]/peer-review/[PUT-CODE] |
| Delete peer-review item | DELETE      | /activities/update       | https://api.sandbox.orcid.org/v3.0_dev1/[ORCID-iD]/peer-review/[PUT-CODE] |


- **[ORCID-iD]** is the ORCID iD for the record, formatted as XXXX-XXXX-XXXX-XXXX
- **[PUT-CODE]** is the ```put-code``` attribute for the specific ```peer-review``` activity that you wish to read or modify.

### cURL Examples
**Read all peer-reviwe items**

```
curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/peer-reviews'
```

**Read single peer-review item**

```
curl -i -H "Accept: application/orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/peer-review/1374'
```

**Add a peer-review item**

```
curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@peer-review-item.xml' -X POST 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/peer-review'
```

**Update a peer-review item**

```
curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@peer-review-item-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/peer-review/1374'
```

**Delete a peer-review item**

```
curl -i -H 'Content-type: application/orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/peer-review/1374'
```


