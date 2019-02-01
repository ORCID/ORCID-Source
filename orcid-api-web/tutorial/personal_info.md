# Write, update and delete personal information

This tutorial goes over accessing and editing information in the personal information sections of an ORCID record, this includes name, credit name, biography, other-names, country, keywords, websites, personal identifiers and email addresses.

These calls can be used with Member API credentials on sandbox or the production servers.

## Overview

**Scopes:** ```/person/update``` and ```/read-limited```

**Method:** [3 step OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect)

**Endpoints:**
* For reading the entire person section: ```/person```
* For reading name, credit name and biography: ```/personal-details```
* For other names ```/other-names```
* For country: ```/address```
* For keywords: ```/keywords```
* For websites: ```/research-urls```
* For [personal identifiers](personal_identifiers.md): ```/external-identifiers```
* For reading email addresses: ```/email```

**Sample XML files:**
  * [Examples for reading personal information sections in 2.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/read_samples)
  * [Examples for writing personal information sections in 2.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/write_sample)
  * [Examples for reading personal information in 3.0](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_3.0_rc1/samples/read_samples)
  * [Examples writing personal information sections in 3.0](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_3.0_rc1/samples/write_samples)

## Permission to edit the record

Only the other names, country, keywords, website and personal identifier sections of the record can be written to. The name, credit name, biography, and email fields can be read using the API but can only be edited by the researcher.

Editing the person section of a record requires a 3 step OAuth token with the ```/person/update``` scope, the ```/read-limited``` scope should also be requested for reading person items. See [Authentciating using OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect) for steps to obtain a token.

## Personal inforamtion fields

- **given-name** _(user edit only)_ The given name of the researcher, or the name they most commonly go by. This is field is required for researchers

- **family-name** _(user edit only)_ The family name or surname of the researcher

- **credit-name** _(user edit only)_ How the researcher prefers their name to be displayed when credited

- **biography** _(user edit only)_ Personal information about the researcher and their career

- **other-names** _(optional)_ Additional names the researcher is or has been known by

- **address:country** _(optional)_ Countries where the researcher works or has worked

- **keywords** _(optional)_ Words or phrases which describe the researcher's research activities and areas of interest

- **researcher-url:url-name** _(optional)_ A text description of a website about the researcher

- **researcher-url:url** _(optional)_ The URL to a website about the researcher

- **external-identifiers** _(optional)_ Identifiers with links to a reference to the researcher in another sytem. See the [Personal identifiers tutorial](personal_identifiers.md).

- **emails** _(user edit only)_ Email addresses of the researcher. _Note that most researchers choose to have their email addresses set to private, which means that they cannot be read via the API_

## Read all person information

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/person |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/person'
```
Example response
```
HTTP/1.1 200 OK

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<person:person ... </person:person>
```

## Read a personal information section

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/[person section] |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/keywords'
```
Example response
```
HTTP/1.1 200 OK

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<keyword:keywords ... </keyword:keywords
```

## Read a single personal information item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/[person section]/[put-code] |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/keywords/4504'
```

## Post a personal information item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/[person section] |
| Method    | POST |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the work you are posting in json or xml format |

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/file_name.xml' -X POST 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/researcher-urls'
```

Example response
```
HTTP/1.1 201 Created
Location: http://api.qa.orcid.org/2.1/0000-0002-9227-8514/researcher-urls/41387
```

## Update a personal information item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/[person section]/[put-code] |
| Method    | PUT |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the updated work in json or xml format |

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/file_name_updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/researcher-urls/41387'
```

Example response
```
HTTP/1.1 200 OK
<researcher-url:researcher-url put-code="8802" ...</researcher-url:researcher-url>
```

## Delete a personal information item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.[host]/[version]/[ORCID iD]/[affiliation section]/[put-code] |
| Method    | DELETE |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/researcher-urls/41387'
```

Example response
```HTTP/1.1 204 No Content```
