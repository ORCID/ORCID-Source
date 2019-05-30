# GROUP-ID-RECORD API TUTORIAL
## DEPRECATED please visit [Write, update and delete peer-review and group-id items](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/tutorial/peer-review.md) instead

## Overview

The group-id-record API allows a client to view, add, update, delete the group-id records.

Posting a peer-review requires a valid group-id, we suggest searching existing peer-review groups before creating new ones to avoid duplicate entries.

** Scopes **

A client can access the records with the following 2 scopes :

```/group-id-record/read``` : GET

```/group-id-record/update``` : GET, POST, PUT, DELETE


##### To use the API >>>

1) Get the access_token :

``curl -i -L -H 'Accept: application/json' -d 'client_id=APP-OAGL07C5YB6GP2L0' -d 'client_secret=ead57be0-cf91-47f7-a673-a154cbab7d3f' -d 'scope=/group-id-record/update' -d 'grant_type=client_credentials' 'https://sandbox.orcid.org/oauth/token'``



``{"access_token":"**a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0**","token_type":"bearer","expires_in":631138518,"scope":"/group-id-record/update","orcid":null}``

2) Use the token to perform the GET, POST, PUT, DELETE.

## Search Group-id-records by Paging


Returns a list of group-id-records which belong to the page number 1, when the given page-size is 5. Also returns other fields('total', 'page', 'page-size') along with the group-id-record list.  page-size defaults to 100, page defaults to 1.


**Example curl call**

``curl -i -L -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0' -X GET https:/~/api.sandbox.orcid.org/v2.0/group-id-record?page-size=5&page=1'``

## Search Group-id-records by Name

Returns a list of group-id-records with name "my-name".


**Example curl call**

``curl -i -L -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0' -X GET https:/~/api.sandbox.orcid.org/v2.0/group-id-record?name=my-name'``


## Create Group-id-record

Creates a new record

``curl -i -L -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0' -d '@/Documents/groupid2.0.xml' -X POST 'https://api.sandbox.orcid.org/v2.0/group-id-record'``

## View Group-id-record

Returns a record with put-code 1000

``curl -i -L -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0' -X GET https:/~/api.sandbox.orcid.org/v2.0/group-id-record/1000'``

## Update Group-id-record

Updates and replaces the updated values for the record with put-code 1000

``curl -i -L -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0' -d '@/Documents/groupid2.0.xml' -X PUT 'https://api.sandbox.orcid.org/v2.0/group-id-record/1000'``

## Delete Group-id-record

Deletes the record with put-code 1000

``curl -i -L -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0' -X DELETE 'https://api.sandbox.orcid.org/v2.0/group-id-record/1000'``
