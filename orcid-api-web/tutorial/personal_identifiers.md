# Write, update and personal identifiers

This tutorial goes over editing information in the other IDs section of the person information on an ORCID record. Personal identifiers are intended to record unique, persistent identifiers that represent the researcher and link to an external system. Examples of personal identifiers include: ISNIs, Scopus Author IDs, Researchers IDs, Loop profiles, and other local identifiers that resolve to a resource about the researcher.

These calls can be used with Member API credentials on sandbox or the production servers.

## Overview

**Scopes:** ```/person/update``` and ```/read-limited```

**Method:** [3 step OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect)

**Endpoints:** ```/external-identifier``` and ```/external-identifiers```

**Sample XML files:**
  * [reading the personal identifiers section in 2.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/read_samples/external-identifiers-2.1.xml)
  * [reading a personal identifier item in 2.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/read_samples/external-identifier-2.1.xml)
  * [writing a personal identifier item in 2.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/write_sample/external-identifier-2.1.xml)
  * [reading the personal identifiers section in 3.0](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/read_samples/external-identifiers-3.0_dev1.xml)
  * [reading a personal identifier item in 3.0](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/read_samples/external-identifier-3.0_dev1.xml)
  * [writing a personal identifier item in 3.0](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/write_sample/external-identifier-3.0_dev1.xml)
  
## Permission to edit the record
Editing the personal identifiers section of a record requires a 3 step OAuth token with the ```/activities/update``` scope, the ```/read-limited``` scope should also be requested for reading funding items. See [Authentciating using OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect) for steps to obtain a token.

## Personal identifier fields

- **exteral-id-type** _(required)_ The type of identifier

- **exteral-id-value** _(required)_ The identifier itself

- **exteral-id-url** _(required)_ A url the identifier resolves to

- **exteral-id-relationship** _(required)_ Personal identifiers must have this field set to 'self'

## Read a summary of all personal identifiers on a record

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/external-identifiers |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.1/external-identifiers'
```

## Read a single personal identifier item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/external-identifiers/[put-code] |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/external-identifiers/3193'
```

## Post a new personal identifier item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/external-identifiers |
| Method    | POST |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the work you are posting in json or xml format | 

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/file_name.xml' -X POST 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/external-identifiers'
```

Example response
```
HTTP/1.1 201 Created
Location: http://api.qa.orcid.org/v2.1/0000-0002-9227-8514/external-identifiers/3194
```

## Update a personal identifier

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/external-identifiers/[put-code] |
| Method    | PUT |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the updated work in json or xml format | 

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/external-identifiers/3193'
```

## Delete a personal identifier

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/external-identifiers/[put-code] |
| Method    | DELETE |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/external-identifiers/3193'
```

Example response
```HTTP/1.1 204 No Content```
