# ORCID-API-Web (Member API)

## Getting Credentials
[Request credentials](https://qa.orcid.org/content/register-client-application) to request a client for the production or sandbox environment. Sandbox URLs are used in these examples.

To creating a client locally see [OAUTH_DEV.md](OAUTH_DEV.md).

## Generate a three step access token
Generating an access token requires interacting with the Registry interface as a user would when granting access

1. Create an Authorization URL to request access
	
| Parameter             |Contents               |
|--------------------|--------------------------|
| host 				| https://sandbox.orcid.org/oauth/authorize|
| client\_id 		| *Your client ID*|
| response\_type	| code|
| scope				| [*Your selected scopes*](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.0#scopes)|
| redirect\_uri		| *Your landing page*|

Example Authorization URL

```https://sandbox.orcid.org/oauth/authorize?client_id=APP-674MCQQR985VZZQ2&response_type=code&scope=/activities/update%20/read-limited&redirect_uri=https://developers.google.com/oauthplayground```

[More information](http://members.orcid.org/api/resources/customize)

2. Visit the URL and grant access

After granting access you will be taken to the redirect URI with a six digit authorization code.

3. Exchange the authorization code for an access token

| Item               |Parameter               |
|--------------------|--------------------------|
| URL 				| https://sandbox.orcid.org/oauth/token|
| client\_id 		| *Your client ID*|
| client\_secret	| *Your client secret*|
| grant\_type		| authorization\_code|
| code				| *The authorization code*|

Example call in curl

```
curl -i -L -H 'Accept: application/json' --data 'client_id=APP-674MCQQR985VZZQ2&client_secret=d08b711e-9411-788d-a474-46efd3956652&grant_type=authorization_code&code=*WkiYjn*' 'https://sandbox.orcid.org/oauth/token'
```
4. Store the ORCID iD, access token, and, optionally, refresh token from the response

Example response:

```
{"access_token":"f5af9f51-07e6-4332-8f1a-c0c11c1e3728","token_type":"bearer",
"refresh_token":"f725f747-3a65-49f6-a231-3e8944ce464d","expires_in":631138518,
"scope":"/activities/update /read-limited","name":"Sofia Garcia","orcid":"0000-0001-2345-6789"}
```
5. Use the access token and ORCID iD to read or update the record.

See the current [XSD documentation](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources) for possible calls.

## Generate a two step (read-public) access token

Send a request to the ORCID API for a two step token

| Item              |Parameter               |
|-------------------|--------------------------|
| URL 				| https://sandbox.orcid.org/oauth/token|
| client\_id 		| *Your client ID*|
| client\_secret	| *Your client secret*|
| grant\_type		| client\_credentials|
| scope				| /read-public|

Example request in curl

```
curl -i -L -H 'Accept: application/json' -d 'client_id=APP-674MCQQR985VZZQ2' -d 'client_secret=d08b711e-9411-788d-a474-46efd3956652' -d 'scope=/read-public' -d 'grant_type=client_credentials' 'https://sandbox.orcid.org/oauth/token'
```
## Further documentation

* [About the ORCID XSD](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources)

* [Documentation on the latest XSD version](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.0/README.md)

* [Members.orcid.org](https://members.orcid.org/api/) for detailed tutorials and workflows

