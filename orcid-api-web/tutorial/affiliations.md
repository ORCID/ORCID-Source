# Write, update and delete affiliation items

This tutorial goes over editing information in the affiliations section of an ORCID record. Versions of the ORCID API before 3.0 include the Education and Employment affiliation sections. Versions 3.0 and greater include the following sections: Education, Employment, Distinction, Invited position, Membership, Qualification and Service.

These calls can be used with Member API credentials on sandbox or the production servers.

## Affiliation sections

**Distinction**: An honorary or other award, distinction, or prize in recognition of your achievements, e.g. trophy, medal, honorary degree.

**Education**: Participation in an academic higher education program to receive an undergraduate, graduate, or other degree, may be in progress or unfinished.

**Employment**: formal employment relationship with an organization, e.g. staff, intern, researcher, contractor. Employment can be paid or unpaid.

**Invited position**: An invited non-employment affiliation, e.g. honorary fellow, guest researcher, emeritus professor.

**Membership**: Membership in a society or association, not including honorary memberships and fellowships.

**Qualification**: Participation in a professional or vocational accreditation, certification, or training program, may be in progress or unfinished.

**Service**: A significant donation of time, money, or other resource, e.g. volunteer society officer, elected board position, extension work.

## Overview

**Scopes:** ```/activities/update``` and ```/read-limited```

**Method:** [3 step OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect)

**Endpoints:** 
* For education items: ```/education``` and ```/educations```
* For employment items: ```/employment``` and ```/employments```
* For distinction items (3.0+ only): ```/distinction``` and ```/distinctions```
* For invited position items (3.0+ only): ```/invited-position``` and ```/invited-positions```
* For membership items (3.0+ only): ```/membership``` and ```/membership```
* For qualification items (3.0+ only): ```/qualification``` and ```/qualification```
* For service items (3.0+ only): ```/service``` and ```/service```

**Sample XML files:**
  * [reading the education section summary in 2.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/read_samples/educations-2.1.xml)
  * [reading an education item in 2.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/read_samples/education-full-2.1.xml)
  * [writing an education item in 2.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/write_sample/education-2.1.xml)
  * [reading the employment section summary in 2.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/read_samples/employments-2.1.xml)
  * [reading an employment item in 2.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/read_samples/employment-full-2.1.xml)
  * [writing an employment item in 2.1](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/samples/write_sample/employment-2.1.xml)
  * [Examples for reading affiliations sections in 3.0](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_3.0_rc1/samples/read_samples)
  * [Examples writing affiliation sections in 3.0](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_3.0_rc1/samples/write_samples)
 
## Permission to edit the record
Editing the affiliations sections of a record requires a 3 step OAuth token with the ```/activities/update``` scope, the ```/read-limited``` scope should also be requested for reading items. See [Authentciating using OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect) for steps to obtain a token.

## Affiliations fields

- **department-name** _(optional)_ The department or subdivision of the organization the affiliation is assocaited with 

- **role-title** _(optional)_ The title given for the position, or the degree earned

- **start-date** _(optional)_ The date the affiliation began

- **end-date** _(optional)_ The date the affiliation ended or will end

- **organization** _(required)_ Information about the organization the affiliation was with, a Ringgold, Grid or Fundref organization identifier is requires in 3.0+

- **url** _(optional in 3.0+ only)_ A URL to a resource about the affiliation 

- **external-ids** _(optional in 3.0+ only)_ An identifier for the affiliation itself


## Read a summary of an affiliation section

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/[affiliation section] |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/educations'
```
Example response
```
HTTP/1.1 200 OK

<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<activities:educations ... </activities:educations>
```

## Read a single affiliation item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/[affiliation section]/[put-code] |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/education/22423'
```

## Post a new affiliation item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/[affiliation section] |
| Method    | POST |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the work you are posting in json or xml format | 

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/file_name.xml' -X POST 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/education'
```

Example response
```
HTTP/1.1 201 Created
Location: http://api.qa.orcid.org/2.1/0000-0002-9227-8514/education/54563
```

## Update an affiliation item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/[affiliation section]/[put-code] |
| Method    | PUT |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the updated work in json or xml format | 

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/file_name_updated.xml' -X PUT 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/education/54563'
```

## Delete an affiliation item

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/[affiliation section]/[put-code] |
| Method    | DELETE |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v2.1/0000-0002-9227-8514/education/54563'
```

Example response
```HTTP/1.1 204 No Content```
