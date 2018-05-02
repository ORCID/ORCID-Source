# ORCID APIs

ORCID offers two APIs. The Member API which allows writing and reading limited access information and is available to organizations who are members of ORCID, and the Public API which allows reading public access information and is available for anyone to use. Both APIs use the same workflow for requesting access and making calls. 

In addition to the production environment at https://orcid.org, ORCID offers a developers sandbox at https://sandbox.orcid.org/ for testing, you do not need to be an ORCID member to test on the Member API on the sandbox.

## API Backgound

* Restful API
* Supports XML and JSON
* Supports [OAuth 2.0](https://oauth.net/2/)
* Supports [OpenID Connect](http://openid.net/connect/)

## Getting Credentials
To access the API you will use a client ID and secret issued by ORCID.

**Member Credentials**

[Request credentials](https://orcid.org/content/register-client-application) to request a client for the production or sandbox environment.

**Public Credentials**

[Developer Tools](https://sandbox.orcid.org/developer-tools) on your ORCID record can be used to register Public Credentials.

**Locally**

To create a client locally see [OAUTH_DEV.md](OAUTH_DEV.md).

## Endpoints

PRODUCTION
* Authorization requests: https://orcid.org/oauth/authorize
* Token exchange: https://orcid.org/oauth/token
* Public API calls: https://pub.orcid.org/[version]
* Member API calls: https://api.orcid.org/[version]

SANDBOX
* Authorization requests: https://sandbox.orcid.org/oauth/authorize
* Token exchange: https://sandbox.orcid.org/oauth/token
* Public API: https://pub.sandbox.orcid.org/[version]
* Member API: https://api.sandbox.orcid.org/[version]

## Authenticating users and using OAuth / OpenID Connect

Most API integrations use OAuth to get access to specific records they want to read or post information to. This workflow has the record holder grant access via the Registry interface and returns the ORCID iD and a token that can be used to access the record.

### Generate an OAuth access token
Generating an access token requires interacting with the Registry interface as a user would when granting access. You will need to set up a user account to test granting access.

**1. Create an Authorization URL to request access**
	
| Parameter             |Contents               |
|--------------------|--------------------------|
| host 				| https://sandbox.orcid.org/oauth/authorize|
| client\_id 		| *Your client ID*|
| response\_type	| code|
| scope				| [*Your selected scopes*](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.1#scopes)|
| redirect\_uri		| *Your landing page*|

Example Authorization URL

```https://sandbox.orcid.org/oauth/authorize?client_id=APP-674MCQQR985VZZQ2&response_type=code&scope=/activities/update%20/read-limited&redirect_uri=https://developers.google.com/oauthplayground```

[More information](http://members.orcid.org/api/resources/customize)

**2. Visit the URL and grant access**

You will need to log into your ORCID record using the Registry Interface. After granting access you will be taken to the redirect URI with a six digit authorization code appended to the url.

Example redirect_uri with authorization code
```https://developers.google.com/oauthplayground/?code=WkiYjn```

**3. Exchange the authorization code for an access token**

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
**4. Store the ORCID iD, access token, and, optionally, refresh token from the response**

Example response:

```
{"access_token":"f5af9f51-07e6-4332-8f1a-c0c11c1e3728","token_type":"bearer",
"refresh_token":"f725f747-3a65-49f6-a231-3e8944ce464d","expires_in":631138518,
"scope":"/activities/update /read-limited","name":"Sofia Garcia","orcid":"0000-0001-2345-6789"}
```
**5. Use the access token and ORCID iD to read or update the record.**

See the current [XSD documentation](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md#calls) for possible calls.

### Using OpenID Connect

Please refer to [ORCID's OpenID Connect HOWTO Guide](https://github.com/ORCID/ORCID-Source/blob/master/orcid-web/ORCID_AUTH_WITH_OPENID_CONNECT.md)

## Generate a two step (read-public) access token

Read-public access tokens can be used to search the ORCID Registry and read public information on any record, they do not require the record holder to grant access.

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

Example response:
```
{"access_token":"1cecf036-5ced-4d04-8eeb-61fa6e3b32ee","token_type":"bearer","refresh_token":"81hbd686-7aa9-4c52-b8db-51fd8370ccf4","expires_in":631138518,"scope":"/read-public","orcid":null}
```

See the current [XSD documentation](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md#calls) for possible calls using a read-public token.

## API Limits

**v1.2**
* Request a second - 8
* Burst - 40

**v2.0 and above**
* Request a second - 24
* Burst - 40

Burst - Number of request we will allow to be queued before rejecting. Requests in the queue are slowed down to the rate of requests a second. If you exceed the burst, you'll get a 503 response.

Request a second - Number of request that can be made a second.

## Further documentation

* [About the ORCID XSD](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources)

* [Documentation on the latest XSD version](https://github.com/ORCID/ORCID-Source/blob/master/orcid-model/src/main/resources/record_2.1/README.md)

* [Members.orcid.org](https://members.orcid.org/api/) for detailed tutorials and workflows

