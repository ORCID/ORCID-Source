# Search for public information on ORCID records

This tutorial shows how to use the API to search for records in the ORCID Registry using the Solr query syntax. Search results only include the ORCID iD of each search hit. For information on retrieving data from a specific ORCID iD see our tutorial on [Reading Public information from an ORCID Record](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/tutorial/read_public.md).

This workflow can be used with Public or Member API credentials on sandbox or the production servers.

Depending on your use you should consider these alternatives to the ORCID API search:

  * To get researchers' ORCID iDs, use OAuth to ensure there are no false matches. See the tutorial on [Getting Authenticate iDs](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/tutorial/get_id.md).
  * If you are parsing data from a large section of the registry you may want to use [ORCID's public data file](https://orcid.org/content/download-file) -- a snapshot of all public data. Premium ORCID members may want to use the [ORCID sync](https://github.com/ORCID/public-data-sync/blob/master/README.md) process to access a regularly updated data file.
  * For specific instructions on finding your institution’s researchers, see [tips on finding ORCID record-holders at your institution](https://members.orcid.org/api/resources/find-myresearchers).

## A note about using search with diacritics 

If you search using the given names fields then our search will parse any letters or characters with diacritics and return all the words both with and without diacritics in them.

For example the following will all return records containing both 'Kårlsbeârd' and 'Karlsbeard':

"https://pub.qa.orcid.org/v3.0/search/?q=given-and-family-names:K%C3%A5rlsbe%C3%A2rd"
"https://pub.qa.orcid.org/v3.0/search/?q=given-and-family-names:Kårlsbeârd"
"https://pub.qa.orcid.org/v3.0/search/?q=given-and-family-names:Karlsbeard"

If you do the same search without specifically searching in the family-names field your search will only return words that exactly what you typed in. 

For example both of the following searches will only return records matching 'Kårlsbeârd' not 'Karlsbeard':

"https://pub.qa.orcid.org/v3.0/search/?q=K%C3%A5rlsbe%C3%A2rd"
"https://pub.qa.orcid.org/v3.0/search/?q=Kårlsbeârd"



## Generate a two step (/read-public) access token

Read-public access tokens are generated with a direct call to the ORCID API, they do not require the record holder to grant access.

Send a request to the ORCID API for a two step token

| Item              |Parameter               |
|-------------------|--------------------------|
| URL 				| https<i></i>://sandbox.orcid.org/oauth/token|
| client\_id 		| *Your client ID*|
| client\_secret	| *Your client secret*|
| grant\_type		| client\_credentials|
| scope				| /read-public|

**Curl Example**

```
curl -i -d 'client_id=APP-674MCQQR985VZZQ2' -d 'client_secret=d08b711e-9411-788d-a474-46efd3956652' -d 'scope=/read-public' -d 'grant_type=client_credentials' 'https://sandbox.orcid.org/oauth/token'
```

Example response:
```
{"access_token":"1cecf036-5ced-4d04-8eeb-61fa6e3b32ee","token_type":"bearer","refresh_token":"81hbd686-7aa9-4c52-b8db-51fd8370ccf4","expires_in":631138518,"scope":"/read-public","orcid":null}
```

## Search for ORCID records

Version is the the version of the API you are using, the latest stable release is v3.0.
Query is the terms you are searching for.

### Member API

| Option| Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.sandbox.orcid.org/[version]/search/?=[query]|
| method    | GET |
| header    | Content-Type: application/vnd.orcid+xml OR  Content-Type: application/vnd.orcid+json|
| header    | Authorization: Bearer [Your access token]|

**Curl example:**

```
curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 89f0181c-168b-4d7d-831c-1fdda2d7bbbb' 'https://api.sandbox.orcid.org/v3.0/search/?q=orcid' -i
```

### Public API

| Option| Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://pub.sandbox.orcid.org/[version]/search/?=[query]|
| method    | GET |
| header    | Content-Type: application/orcid+xml OR  Content-Type: application/orcid+json|
| header    | Authorization: Bearer [Your access token]|

**Curl example:**

```
curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 89f0181c-168b-4d7d-831c-1fdda2d7bbbb' 'https://pub.sandbox.orcid.org/v3.0/search/?q=orcid' -i
```

Example response

```
<?xml version="1.0"  encoding="UTF-8" standalone="yes"?>
  <search:search num-found="385" xmlns:search="http://www.orcid.org/ns/search"  xmlns:common="http://www.orcid.org/ns/common">
  <search:result>
  <common:orcid-identifier>
    <common:uri>https://sandbox.orcid.org/0000-0001-2345-6789</common:uri>
    <common:path>/0000-0001-2345-6789</common:path>
    <common:host>sandbox.orcid.org</common:host>
  </common:orcid-identifier>
  </search:result>
  [...]
  </search:search>
 ```

 ## Search technical information

 The default behaviour is a free-form, unrestricted search across the entire record. A basic search for "newman" will therefore turn up researchers with the published name Newman, given name Newman, or associated with a work with "newman" in the title, etc.

 The API supports Boolean searches using multiple keywords, exact phrases, and other Boolean search features. Keywords must be combined using brackets and “AND” or “OR” in uppercase.

 [All query syntaxes available in SOLR 8.0 are supported](https://lucene.apache.org/solr/guide/8_0/), including Lucene with Solr extensions (default), DisMax, and Extended Dismax.

 The number of matching records is returned in the num-found field in the results.

 100 results are returned by default, up to 200 results can be returned with one query using paging -see examples.

 Like all API calls, search queries are restricted by the [API limits](https://github.com/ORCID/ORCID-Source/tree/master/orcid-api-web#api-limits).

 ## Indexed fields

 The entire ORCID record is indexed and can be searched using basic keyword searching. Search can also be limited to the specific fields listed below:

**Biographical data**

* given-names

* family-name

* credit-name

* other-names

* email

* keyword

* external-id-reference

* biography

**Affiliations data**

* affiliation-org-name

* ringgold-org-id

* grid-org-id

**Funding data**

* funding-titles

* fundref-org-id

* grant-numbers

**Works**

* title

* digital-object-ids

* doi-self

* [external identifier type]&ast;-self

* [external identifier type]&ast;-part-of

* external-id-type-and-value: like ResearcherID=A-1007-2016

**Peer review**

* peer-review-type

* peer-review-role

* peer-review-group-id

**Record data**

* orcid

* profile-submission-date

* profile-last-modified-date

&ast; For a full list of external identifier see the [identifiers list](https://pub.qa.orcid.org/v3.0/identifiers?locale=en). Some identifiers may require "-self" or "-part-of"  to return results.

## Example search queries

### Example 1

Description: Search the full text for the word “English”

Syntax: Lucene

Paging: First 10 rows only

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=text:English&start=0&rows=10```

### Example 2

Description: Search for records with the family name “Sanchez”

Syntax: Lucene

Paging: Rows 5-10 only

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=family-name:Sanchez&start=4&rows=6```

### Example 3

Description: Search for contributors associated with the work at PubMed ID 2485-7732

Syntax: Lucene

Paging: All records

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=pmid:24857732```

### Example 4

Description: Search for records with the family name “Einstein” and the keyword “Relativity”. Only records containing both the family name and the keyword will be returned.

Syntax: Lucene

Paging: First 10 rows only

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=family-name:Einstein+AND+keyword:Relativity&start=0&rows=10```

### Example 5

Description: Search for records with the Family name Taylor and the given-name Michael.

Syntax: Lucene

Paging: All results

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=family-name:Taylor+AND+given-names:Michael```

### Example 6

Description: Search given names and family names of all ORCID records for “Raymond” but boost the family name. Records with given names containing “Raymond” and family name containing “Raymond” will be returned, but those with family name will appear at the top of the list and will have a higher relevancy score.

Syntax: Extended DisMax

Paging: First 10 rows only

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?defType=edismax&q=Raymond&qf=given-names^1.0%20family-name^2.0&start=0&rows=10```

### Example 7

Description:

This is the same search as the one above except that the two records with ORCID ID https://sandbox.orcid.org/0000-0002-0879-455X and https://sandbox.orcid.org/0000-0001-6238-4490 will be excluded from the results.

Syntax: Extended DisMax

Paging: First 10 rows only

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?defType=edismax&q=Raymond+-orcid:(0000-0002-0879-455X+0000-0001-6238-4490)&qf=given-names^1.0+family-name^2.0&start=0&rows=10```

### Example 8

Description: Search for records with the exact DOI 10.1087/20120404 set to self

Paging: Default

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=doi-self:%2210.1087/20120404%22```

### Example 9

Description: Search for records with a DOI that includes 10.1087 set either to self or part-of

Paging: First 200 rows

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=digital-object-ids:10.1087&start=0&rows=200```

### Example 10

Description: Search for records with a PubMed Identifier 27281629 set to self

Paging: Default

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=pmid-self:27281629```

### Example 11

Description: Search for records with an ISBN Identifier including 1234 set to either self or part-of

Paging: Default

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=isbn:1234```

### Example 12

Description: Search for all records with an email address with an @orcid.org domain

Paging: Default  
Note: Most ORCID records have the email address marked as private, and private information will not be returned in the search results.

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=email:*@orcid.org```

### Example 13

Description: Search for records modified between January 1, 2018 and today

Paging: First 10 results

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=profile-last-modified-date:%5B2018-01-01T00:00:00Z%20TO%20NOW%5D&start=1&rows=10```

### Example 14

Description: Search for records affiliated with the organization with the exact name “Boston University” or "BU"

Paging: Default

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=affiliation-org-name:(%22Boston%20University%22+OR+BU)```

### Example 15

Description: Search for records affiliated with the Ringgold ID 1438 (University of California Berkeley)

Paging: Default

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=ringgold-org-id:1438```

### Example 16

Description: Search for records affiliated with the GRID ID grid.5509.9 (University of Tampere)

Paging: Default

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=grid-org-id:grid.5509.9```

### Example 17 

Description: You can search for records using external IDs, these can be any of the Identifiers in this list https://pub.orcid.org/v3.0/identifiers. The following search is for records affiliated with Research ID 1007-2016

URL ```https://pub.sandbox.orcid.org/v3.0/search/?q=ResearcherID=A-1007-2016```


### Example 18

Description: Search for peer-review group IDs. You can search group IDs with SOLR search but you do not need to put the ID type in the search. For example the following search looks for 1996-3068 which is a valid ISSN. 

URL ```'https://pub.sandbox.orcid.org/v3.0/search/?q=peer-review-group-id=1996-3068'```

Paging: first 25 results



