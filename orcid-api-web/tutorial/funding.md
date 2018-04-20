# Write, update and delete funding items

This tutorial goes over editing information in the funding section of an ORCID record. The ```funding``` activity type is intended to link to grants, awards, or other types of funding that have been awarded in support of the researcher's work.

These calls can be used with Member API credentials on sandbox or the production servers.

## Overview

**Scopes:** ```/activities/update``` and ```/read-limited```

**Method:** [3 step OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect)

**Endpoints:** ```/funding``` and ```/fundings```

**Sample XML files:**
  * [reading the fundings section summary](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/read_samples/fundings-3.0_dev1.xml)
  * [reading a funding item](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/read_samples/funding-3.0_dev1.xml)
  * [writing a funding item](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/write_samples/funding-3.0_dev1.xml)
  
## Permission to edit the record
Editing the funding section of a record requires a 3 step OAuth token with the ```/activities/update``` scope, the ```/read-limited``` scope should also be requested for reading funding items. See [Authentciating using OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect) for steps to obtain a token.

## Funding fields

**Describing the work**
- **type** _(required)_ The type of funding awarded, This field is selected from a list containing the following values: Award, Contract, Grant, Salary-award
    - Award: Peer-reviewed funding providing direct research costs through competitions. Discussion

    - Contract: Works commissioned by external public agencies or industry building on research expertise and aimed at deliverables. Include research contracts awarded by federal agencies for both direct and indirect costs, and honoraria. Discussion

    - Grant: Peer-reviewed funding providing direct research costs through competitions. Discussion

    - Salary award: A competitive, peer-reviewed award that is paid as salary to the awardee/faculty member.


- **organization-defined-type** _(optional)_ The subtype or locally defined type for the award 

- **title** _(required)_ The title of the funding award

- **subtitle** _(optional)_ A subtitle to the funding award

- **translated-title** _(optional)_ The title the funding appears under in another language, the langauge of the translated title is recorded as an attribute

- **amount** _(optional)_ The value of the award, the currency-code is given as an attribute

- **url** _(optional)_ A url linking to the funding

- **start-date** _(optional)_ The date the funding began

- **end-date** _(optional)_ The date the funding ended or will end

- **exteral-id-type** _(optional)_ The type of identifier. This field must be set to ```grant_number```

- **exteral-id-value** _(optional)_ The identifier itself

- **exteral-id-url** _(optional)_ A url the identifier resolves to

- **exteral-id-relationship** _(optional)_ Select self for identifiers that apply to the funding itself or part-of for identifiers that apply to a collection the funding is part of.

- **contributors** _(optional)_ Information about the individuals who received the funding

- **organization** _(required)_ Information about the organization that awarded the funding

## Read a summary of all funding items on a record

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/fundings |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/fundings'
```
Example response
```
HTTP/1.1 200 OK

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<activities:fundings ... </activities:fundings>
```

## Read a single funding item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/funding/[put-code] |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/funding/4413'
```

Example response
```HTTP/1.1 200 OK

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<funding:funding put-code="2205" path="/0000-0002-9227-8514/funding/4413" ... </funding:funding> 
```

## Post a new funding item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/funding |
| Method    | POST |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the work you are posting in json or xml format | 

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/file_name.xml' -X POST 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/funding'
```

Example response
```
HTTP/1.1 201 Created
Location: http://api.qa.orcid.org/v3.0_dev1/0000-0002-9227-8514/funding/4458
```

## Update a funding item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/funding/[put-code] |
| Method    | PUT |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the updated work in json or xml format | 

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/funding/[PUT-CODE]'
```

Example response
```
HTTP/1.1 200 OK

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<funding:funding put-code="4458" ... </funding:funding>
```

## Delete a funding item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/funding/[put-code] |
| Method    | DELETE |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/funding/[PUT-CODE]'
```

Example response
```HTTP/1.1 204 No Content```
