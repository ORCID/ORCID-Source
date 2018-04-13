# Write, update and delete works

This tutorial goes over editing information in the works section of an ORCID record. The ```work``` activity type is intended to link to the research outputs of the ORCID record holder. 

This workflow can be used with Member API credentials on sandbox or the production servers.

## Overview

**Scopes:** ```/activities/update``` and ```/read-limited```

**Method:** [3 step OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect)

**Endpoints:** ```/work``` and ```/works```

**Sample XML files:**
  * [reading the works section summary](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/read_samples/works-3.0_dev1.xml)
  * [reading a basic work](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/read_samples/work-3.0_dev1.xml)
  * [reading a detailed work item](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/read_samples/work-full-3.0_dev1.xml)
  * [writing a work item with the mininal information](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/write_samples/work-simple-3.0_dev1.xml)
  * [writing a work with the detailed information](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/write_samples/work-full-3.0_dev1.xml)
  * [writing mulitple works](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/write_samples/bulk-work-3.0_dev1.xml)
  * [writing multiple works in json](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_3.0_dev1/samples/write_samples/bulk-work-3.0_dev1.json)

## Permission to edit the record
Editing the work section of a record requires a 3 step OAuth token with the ```/activities/update``` scope, the ```/read-limited``` scope should also be requested for reading works. See [Authentciating using OAuth](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/README.md#authenticating-users-and-using-oauth--openid-connect) for steps to obtain a token.

## Work fields

**Describing the work**
- **title** _(required)_ The title of the work

- **subtitle** _(optional)_ A subtitle to the work

- **translated-title** _(optional)_ The title the work appears under in another language, the langauge of the translated title is recorded as an attribute

- **journal-title** _(optional)_ The name of a larger collection the work belongs to, such a a journal for journal articles or a book for book chapters

- **short-description** _(optional)_ A brief description or abstract of the work

- **citation-type** _(optional)_ The format the citation is provided in. This field is selected from a list containing the following values: APA, BIBTEX, CHICAGO, HARVARD, IEEE, MLA, RIS, UNSPECIFIED, VANCOUVER

- **citation-value** _(optional)_ The contents of the citation

- **work-type** _(required)_ The type object the work is.  This field is selected from a list containing the following values: artistic-performance, book-chapter, book-review, book, conference-abstract, conference-paper, conference-poster, data-set, dictionary-entry, disclosure, dissertation, edited-book, encyclopedia-entry, invention, journal-article, journal-issue, lecture-speech, license, magazine-article, manual, newsletter-article, newspaper-article, online-resource, other, patent, registered-copyright, report, research-technique, research-tool, spin-off-company, standards-and-policy, supervised-student-publication, technical-standard, test, translation, trademark, website, working-paper

- **publication_date** _(optional)_ The date the work was completed

- **exteral-id-type** _(required)_ The type of identifier. This field is selected from a list at https://pub.qa.orcid.org/v2.0/identifiers?locale=en

- **exteral-id-value** _(required)_ The identifier itself

- **exteral-id-url** _(optional)_ A url the identifier resolves to

- **exteral-id-relationship** _(required)_ Select self for identifiers that apply to the work itself or part-of for identifiers that apply to a collection the work is part of.

- **work-url** _(optional)_ A url linking to the work

- **work-contributors** _(optional)_ Information about the individuals who created the work

- **language-code** _(optional)_ The language used to describe the work in the previous fields

- **country** _(optional)_ A country the work was published in or otherwise assocaited with

## Read a summary of all works on a record

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/works |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/works'
```


## Read a single work

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/work/[put-code] |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|


**Example request in curl**

```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/work/733536'
```


## Read multiple works (up to 50)

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/works/[put-code-1],[put-code-2],[put-code-3] |
| Method    | GET |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Accept: application/vnd.orcid+json or /vnd.orcid+xml|

**Example request in curl**
```
curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/works/733535,733536'
```

## Post one new work

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/work |
| Method    | POST |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the work you are posting in json or xml format | 

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work.xml' -X POST 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/work'
```

## Post multiple works (up to 100)

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/works |
| Method    | POST |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the work you are posting in json or xml format | 

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/works.xml' -X POST 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/works'
```


## Update a single work

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/work/[put-code] |
| Method    | PUT |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|
| data        | the updated work in json or xml format | 

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -d '@[FILE-PATH]/work-updated.xml' -X PUT 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/work/[PUT-CODE]'
```


## Delete a single work

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://api.[host]/[version]/[ORCID iD]/work/[put-code] |
| Method    | DELETE |
| header      | Authorication: Bearer [Your authorization code] |
| header      | Content-Type: application/vnd.orcid+json or /vnd.orcid+xml|

**Example request in curl**
```
curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer dd91868d-d29a-475e-9acb-bd3fdf2f43f4' -X DELETE 'https://api.sandbox.orcid.org/v3.0_dev1/0000-0002-9227-8514/work/[PUT-CODE]'
```
