# ORCID-INTERNAL-API (Internal API)

curl -i -L -k -H 'Accept: application/json' -d 'client_id=<CLIENT_ID>' -d 'client_secret=<CLIENT_SECRET>' -d 'scope=/orcid-internal/person/read' -d 'grant_type=client_credentials' 'http://localhost:8080/orcid-internal-api/oauth/token'


curl -H 'Accept: application/json' -H 'Authorization: Bearer <TOKEN>' http://localhost:8080/orcid-internal-api/<ORCID>/person


#License
See [LICENSE.md](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/LICENSE.md)

