# Write, update and delete research resource items

This tutorial goes over editing information in the research resources section of an ORCID record. The ```research-resource``` activity type is intended to reference and link to things that researchers use for their research which require a specific proposal process or credential to access.

Research resources is available in the 3.0_rc1 and later versions of the API. These calls can be used with Member API credentials on sandbox or the production servers.

## Overview

**Scopes:** ```/activities/update``` and ```/read-limited```

**Method:** [3 step OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect)

**Endpoints:** ```/research-resource``` and ```/research-resources```

**Sample XML files:**
  * [reading the research resource section summary](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_rc1/samples/read_samples/research-resources-3.0_rc1.xml)
  * [reading a research resource](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_rc1/samples/read_samples/research-resource-3.0_rc1.xml)
  * [writing a research resource](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_rc1/samples/write_samples/research-resource-3.0_rc1.xml)

## Permission to edit the record
Editing the research resources section of a record requires a 3 step OAuth token with the ```/activities/update``` scope, the ```/read-limited``` scope should also be requested for reading research resource items. See [Authenticating using OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect) for steps to obtain a token.

## Research resource fields

**Describing the proposal to access the resource**

- **title** _(required)_ The title of the proposal or registration to access the resource

- **translated-title** _(optional)_ The title the proposal appears under in another language, the language of the translated title is recorded as an attribute

- **proposal host** _(required)_ The organization that receives and processes resource proposals or requests. Proposal host may or may not be the same as resource host.

- **external-id** _(required)_ An identifier for the propsal. The identifier type must be selected from the [supported identifier types](https://pub.qa.orcid.org/v2.0/identifiers?locale=en). In the case where there is no persistent unique identifier for the proposal, the source providing the data should generate a locally-sourced unique identifier for the review (e.g., type "organization-defined-type").

- **start date** _(optional)_ The date the access started

- **end date** _(optional)_ The date the access ended or will end

- **proposal url** _(optional)_ A link to the proposal

**Describing the resource**

- **resource-name** _(required)_ The name of the resource

- **resource-type** _(required)_ The type of resource, This field is selected from a list containing the following values: Collection, Equipment, Infrastructure, Service
    - Collection: An object or group of objects used for research purposes; can be tangible or digital. Examples include ocean mission, field campaign, data sets, rare book collections, museum collections, biological specimen collections.

    - Equipment: Hardware used for research purposes. Examples include microscopes, telescopes, computers, glassware, samples, materials.

    - Infrastructure: A facility, building, or other physical space used to perform research. Examples include a neutron spallation source, animal facility, data enclave, archaeological site, telescope array, ship, plane, farm, laboratory.

    - Service: Services used for research purposes. Examples include data analysis, computing services, logistical support, legal services, copyediting, expert or staff advisement.

- **resource host** _(required)_ The the organization(s) that administer or operate the resource, typically a national laboratory, government agency, or research university.

- **external-id** _(required)_ An identifier for the resource.

- **resource url** _(optional)_ A url linking to the resource or information about the resource.


## Read a summary of all research resources on a record

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/research-resources |
| Method    | GET |
| header      | Authorization: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v3.0_rc1/0000-0002-9227-8514/research-resources'
```
Example response
```
HTTP/1.1 200 OK

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<activities:research-resources ... </activities:research-resources>
```

## Read a single research resource

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/research-resource/[put-code] |
| Method    | GET |
| header      | Authorization: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v3.0_rc1/0000-0002-9227-8514/research-resource/1000'
```

Example response
```
HTTP/1.1 200 OK

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<research-resource:research-resource put-code="1000" path="/0000-0002-9227-8514/research-resource/1000" ... </research-resource:research-resource>
```

## Post a new research resource

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/research-resource |
| Method    | POST |
| header      | Authorization: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the work you are posting in json or xml format |

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/file_name.xml' -X POST 'https://api.sandbox.orcid.org/v3.0_rc1/0000-0002-9227-8514/research-resource'
```

Example response

```
HTTP/1.1 201 Created
Location: http://api.sandbox.orcid.org/v3.0_rc1/0000-0002-9227-8514/research-resource/1000
```

## Update a research resource

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/research-resource/[put-code] |
| Method    | PUT |
| header      | Authorization: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the updated work in json or xml format |

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v3.0_rc1/0000-0002-9227-8514/research-resource/1000'
```

Example response

```
HTTP/1.1 200 OK

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<research-resource:research-resource put-code="1000 ... </research-resource:research-resource>
```

## Delete a research resource

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/research-resource/[put-code] |
| Method    | DELETE |
| header      | Authorization: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|

**Example request in curl**

```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v3.0_rc1/0000-0002-9227-8514/research-resource/1000'

```



Example response

```
HTTP/1.1 204 No Content
```
