# Search for public information on ORCID records

This tutorial shows how to use the API to search for records in the ORCID Registry using the Solr query syntax. Search results only include the ORCID iD of each search hit. For information on retrieving data from a specific ORCID iD see our tutorial on [Reading Public information from an ORCID Record](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/tutorial/read_public.md).

This workflow can be used with Public or Member API credentials on sandbox or the production servers.

Depending on your use you should consider these alternatives to the ORCID API search:

  * To get researchers' ORCID iDs, use OAuth to ensure there are no false matches. See the tutorial on [Getting Authenticate iDs](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/tutorial/get_id.md).
  * If you are parsing data from a large section of the registry you may want to use [ORCID's public data file](https://orcid.org/content/download-file) -- a snapshot of all public data. Premium ORCID members may want to use the [ORCID sync](https://github.com/ORCID/public-data-sync/blob/master/README.md) process to access a regularly updated data file.
  * For specific instructions on finding your institution’s researchers, see [tips on finding ORCID record-holders at your institution](https://members.orcid.org/api/resources/find-myresearchers).

## A note about using search with diacritics 

The *given-and-family-names* field will parse any letters or characters with diacritics and return records with names both with and without diacritics in them.

For example the following will all return records containing both 'Kårlsbeârd' and 'Karlsbeard':

"https://pub.sandbox.orcid.org/v3.0/search/?q=given-and-family-names:K%C3%A5rlsbe%C3%A2rd"
"https://pub.sandbox.orcid.org/v3.0/search/?q=given-and-family-names:Kårlsbeârd"
"https://pub.sandbox.orcid.org/v3.0/search/?q=given-and-family-names:Karlsbeard"

Only the *given-and-family-names* field will match characters with and without disacritics, other fields will match only on the character searched.

For example both of the following searches will only return records matching 'Kårlsbeârd' not 'Karlsbeard':

"https://pub.sandbox.orcid.org/v3.0/search/?q=K%C3%A5rlsbe%C3%A2rd"
"https://pub.sandbox.orcid.org/v3.0/search/?q=Kårlsbeârd"



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
| header    | Accept: application/vnd.orcid+xml OR  Accept: application/vnd.orcid+json|
| header    | Authorization: Bearer [Your access token]|

**Curl example:**

```
curl -H 'Accept: application/orcid+xml' -H 'Authorization: Bearer 89f0181c-168b-4d7d-831c-1fdda2d7bbbb' 'https://api.sandbox.orcid.org/v3.0/search/?q=orcid' -i
```

### Public API

| Option| Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://pub.sandbox.orcid.org/[version]/search/?=[query]|
| method    | GET |
| header    | Accept: application/orcid+xml OR  Accept: application/orcid+json|
| header    | Authorization: Bearer [Your access token]|

**Curl example:**

```
curl -H 'Accept: application/orcid+xml' -H 'Authorization: Bearer 89f0181c-168b-4d7d-831c-1fdda2d7bbbb' 'https://pub.sandbox.orcid.org/v3.0/search/?q=orcid' -i
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

 1000 results are returned by default, up to 1000 results can be returned with one query using paging -see examples.

 Like all API calls, search queries are restricted by the [API limits](https://github.com/ORCID/ORCID-Source/tree/master/orcid-api-web#api-limits).

 ## Using ORCID search with CSV

 | Item              |Parameter               |
|-------------------|--------------------------|
| URL 				| https://pub.sandbox.orcid.org/v3.0/csv-search/[version]/search/?q=[query]&fl[csv fields]
| Header		|'Accept: text/csv' |
|Method| GET|
| Endpoint 	|csv-search |
|Allowed Fields      | orcid, email, given-names, family-name,given-and-family-names, current-institution-affiliation-name,past-institution-affiliation-name, credit-name, other-names|

You can search ORCID with the API and return your search as a CSV as an alternative to JSON and XML. This can be achieved by changing the header, the endpoint and then specify the fields you want in the response using the fl (field list) parameter. The search part of the query remains the same.

For example; say you want to search for records associated with the ringgold `385488` and return the results as a CSV containing the fields `orcid, given-names, family-name, current-institution-affiliation-name`. You would need to change the header to `Accept:text/csv` and add your search query as normal `?q=ringgold-org-id:385488` and then select the fields you want to return in your CSV by choosing from the above list of allowed fields as follows: `orcid, given-names, family-name, current-institution-affiliation-name`.

The above query would look like

```https://pub.sandbox.orcid.org/v3.0/csv-search/?q=ringgold-org-id:385488&fl=orcid,given-names,family-name,current-institution-affiliation-name'```


The first part of the response to the above query would look something like this:

```0000-0003-1481-3160,Paula,Demain,ORCID
0000-0002-6600-9556,ma_test,16sept2016,"ORCID,ORCID,ORCID,CrossRef"
0000-0003-1876-0369,Anjli,Narwani,
0000-0002-9177-4161,Utib,Utib,
0000-0002-7191-5289,Pedro,López,"ORCID,Universidad Autónoma de San Luis Potosí"
```

 ## Indexed fields

 The entire ORCID record is indexed and can be searched using basic keyword searching. Search can also be limited to the specific fields listed below:

**Biographical data**

* given-names

* family-name

* given-and-family-names

* credit-name

* other-names

* email

* keyword

* external-id-reference

* external-id-type-and-value

* biography

**Affiliations data**

* affiliation-org-name

* ringgold-org-id

* grid-org-id

* ror-org-id

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

**Peer review**

* peer-review-type

* peer-review-role

* peer-review-group-id

**Record data**

* orcid

* profile-submission-date

* profile-last-modified-date

&ast; For a full list of external identifiers see the [identifiers list](https://pub.sandbox.orcid.org/v3.0/identifiers?locale=en). Some identifiers may require "-self" or "-part-of"  to return results.

## Expanded search

In addition to the basic search, the expanded search end point `expanded-search` is available. This is available in **3.0 API only**. Using this endpoint returns the following information: 
`orcid-id`
`given-names`
`family-names`
`credit-name`
`other-name`
`email`
`institution-name` when using the standard search syntax. Results can be returned in either XML or JSON. 
### Public API expanded search 
| Option| Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://pub.sandbox.orcid.org/v3.0/expanded-search/?=[query]|
| method    | GET |
| header    | Accept: application/orcid+xml OR  Accept: application/orcid+json|
| header    | Authorization: Bearer [Your access token]|


**Curl example:**

```
curl -H 'Accept: application/orcid+xml' -H 'Authorization: Bearer 89f0181c-168b-4d7d-831c-1fdda2d7bbbb' 'https://pub.sandbox.orcid.org/v3.0/expanded-search/?q=blackburn' -i
```

Example response

```
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<expanded-search:expanded-search num-found="4" xmlns:expanded-search="http://www.orcid.org/ns/expanded-search">
    <expanded-search:expanded-result>
        <expanded-search:orcid-id>0000-0002-7024-3038</expanded-search:orcid-id>
        <expanded-search:given-names>Rob</expanded-search:given-names>
        <expanded-search:family-names>Blackburn</expanded-search:family-names>
        <expanded-search:credit-name>Kårlsbeârd The Gnöme</expanded-search:credit-name>
        <expanded-search:email>rob21may@mailinator.com</expanded-search:email>
        <expanded-search:institution-name>ORCID</expanded-search:institution-name>
        <expanded-search:institution-name>common:name</expanded-search:institution-name>
        <expanded-search:institution-name>Museum Siam Discovery Museum</expanded-search:institution-name>
        <expanded-search:institution-name>Highland Fine Cheeses (United Kingdom)</expanded-search:institution-name>
        <expanded-search:institution-name>Glamorgan Cake Institute</expanded-search:institution-name>
        <expanded-search:institution-name>Stilton Manufacturing</expanded-search:institution-name>
    </expanded-search:expanded-result>
...
 ```

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

Same search but with expanded search (3.0 API only)

Syntax: Lucene

Paging: First 10 rows only

URL: ```https://pub.sandbox.orcid.org/v3.0/expanded-search/?q=family-name:Einstein+AND+keyword:Relativity&start=0&rows=10```
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

The same search but with expanded search (3.0 API only):

Paging: Default

URL: ```https://pub.sandbox.orcid.org/v3.0/expanded-search/?q=doi-self:%2210.1087/20120404%22```


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

URL: ```https://pub.sandbox.orcid.org/v3.0/search/?q=profile-last-modified-date:%5B2018-01-01T00:00:00Z%20TO%20NOW%5D&start=0&rows=10```

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

Description: Search for records that have a personal identifier "ResearcherID:A-1111-2011":

Paging: Default

URL ```https://pub.sandbox.orcid.org/v3.0/search/?q=external-id-type-and-value:ResearcherID=%22A-1111-2011%22```

### Example 18

Description: Search for records with peer-reviews that are associated with the peer-review group "issn:1741-4857":

Paging: Default

URL ```https://pub.sandbox.orcid.org/v3.0/search/?q=peer-review-group-id:%22issn\:1741-4857%22```

### Example 19 

Description: Search for records with Ringold "385488" and return as CSV with the fields "given-names, family-name, current-institution-affiliation-name":

Paging: Default

URL ```https://pub.sandbox.orcid.org/v3.0/csv-search/?q=ringgold-org-id:385488&fl=orcid,given-names,family-name,current-institution-affiliation-name```


### Example 20 
Description: Search for records affiliated with the organization with the exact name “Boston University” or "BU" and return results as CSV with the fields "orcid, given-names, family-name'

Paging: Default

URL ```https://pub.sandbox.orcid.org/v3.0/csv-search/?q=affiliation-org-name:(%22Boston%20University%22+OR+BU)&fl=orcid,given-names,family-name```
