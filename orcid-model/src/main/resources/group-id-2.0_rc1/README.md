# GROUP-ID-RECORD API

## Deprecated guide, please see [Write, update and delete peer-review and group-id items](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/tutorial/peer-review.md) instead.

The group-id-record API allows a client to view, add, update, delete the group-id records.
A client can access the records with the following 2 scopes :

/group-id-record/read : GET

/group-id-record/update : GET, POST, PUT, DELETE


In order to get access to these scopes, one should manually map the scopes with the client in the table client_scope using the following example query :

``insert into client_scope values(<Client ID>,'/group-id-record/read',now(),now() );``

##### Generate the input XML >>>

The maximum length of the following fields should not exceed :

Name : 1000 chars
Groupid : 1000 chars
Decription : 1000 chars


##### To use the API >>>

1) Get the access_token :

``curl -i -L -H 'Accept: application/json' -d 'client\_id=APP-OAGL07C5YB6GP2L0' -d 'client\_secret=ead57be0-cf91-47f7-a673-a154cbab7d3f' -d 'scope=/group-id-record/update' -d 'grant\_type=client_credentials' 'http://localhost:8080/orcid-web/oauth/token'``



``{"access\_token":"**a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0**","token\_type":"bearer","expires\_in":631138518,"scope":"/group-id-record/update","orcid":null}``

2) Use the token to perform the GET, POST, PUT, DELETE.

**Create Group-id-record**

Creates a new record

``curl -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0' -d '@/Documents/groupid2.0.xml' -X POST 'http://localhost:8080/orcid-api-web/v2.0_rc1/group-id-record'``

**View Group-id-record**

Returns a record with put-code 1000

``curl -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0' -X GET 'http://localhost:8080/orcid-api-web/v2.0_rc1/group-id-record/1000'``

**Update Group-id-record**

Updates and replaces the updated values for the record with put-code 1000

``curl -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0' -d '@/Documents/groupid2.0.xml' -X PUT 'http://localhost:8080/orcid-api-web/v2.0_rc1/group-id-record/1000'``

**Delete Group-id-record**

Deletes the record with put-code 1000

``curl -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0' -X DELETE 'http://localhost:8080/orcid-api-web/v2.0_rc1/group-id-record/1000'``

**View Group-id-records-Paging**

Returns a list of group-id-records which belong to the page number 1, when the given page-size is 5. Also returns other fields('total', 'page', 'page-size') along with the group-id-record list.

``curl -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer a9bae4e9-fdf4-4f18-beaa-08e7629a5ec0' -X GET 'http://localhost:8080/orcid-api-web/v2.0_rc1/group-id-record?page-size=5&page=1'``
