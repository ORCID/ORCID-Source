# Read Public information from an ORCID Record

This tutorial shows how to read public information from ORCID records without going through the 3-step OAuth process. If you do not have the list of authenticated ORCID iDs you want to read, we suggest using the OAuth process to collect iDs and read information on the records. See the [tutorial to get authenticate iDs](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/tutorial/get_id.md).

This workflow can be used with Public or Member API credentials on sandbox or the production servers.

## Generate a two step (/read-public) access token

Read-public access tokens are generated with a direct call to the ORCID API, they do not require the record holder to grant access. A single /read-public token can be used multiple times and on multiple records, you should store a single token for reuse rather than generating a new token each time you read a record.

Send a request to the ORCID API for a two step token

| Item              |Parameter               |
|-------------------|--------------------------|
| URL 				| https://sandbox.orcid.org/oauth/token|
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

## Read the ORCID record

Version is the the version of the API you are using, the latest stable release is v2.1. Endpoint is the section of the record you want to read, 'record' returns the entire record.

### Member API

| Option| Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.sandbox.orcid.org/[version]/[ORCID iD]/[endpoint]|
| method    | GET |
| header    | Content-Type: application/orcid+xml OR  Content-Type: application/orcid+json|
| header    | Authorization: Bearer [Your /read-public access token]|

**Curl example:**

```
curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 89f0181c-168b-4d7d-831c-1fdda2d7bbbb' 'https://api.sandbox.orcid.org/v2.1/0000-0001-2345-6789/record' -i
```

### Public API

| Option| Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://pub.sandbox.orcid.org/[version]/[ORCID iD]/[endpoint]|
| method    | GET |
| header    | Content-Type: application/orcid+xml OR  Content-Type: application/orcid+json|
| header    | Authorization: Bearer [Your /read-public access token]|

**Curl example:**

```
curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 89f0181c-168b-4d7d-831c-1fdda2d7bbbb' 'https://pub.sandbox.orcid.org/v2.1/0000-0001-2345-6789/personal-details' -i
```

See the current [XSD documentation](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_3.0_rc1#calls) for possible endpoints.
