# ORCID-INTERNAL-API (Internal API)

The ORCID-INTERNAL-API is intended to be used for internal ORCID apps, so, only specific clients can use it.

### Set up

1. Add the scope '/orcid-internal/person/last_modified' to the client you want to use

``insert into client_scope values('<Client ID>','/orcid-internal/person/last_modified',now() , now() );``

``update client_details set last_modified=now() where client_details_id='<CLIENT_ID>'`` 

### How to use it?

1. Get the token: 

``curl -i -L -k -H 'Accept: application/json' -d 'client_id=<CLIENT_ID>' -d 'client_secret=<CLIENT_SECRET>' -d 'scope=/orcid-internal/person/read' -d 'grant_type=client_credentials' 'http://localhost:8080/orcid-internal-api/oauth/token'``

2. Use the token to get user info

``curl -H 'Accept: application/json' -H 'Authorization: Bearer <TOKEN>' http://localhost:8080/orcid-internal-api/<ORCID>/person``


#License
See [LICENSE.md](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/LICENSE.md)

