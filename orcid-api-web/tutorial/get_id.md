# Get Authenticate iDs

This tutorial shows how to collect a user's authenticated ORCID iD using OAuth, examples are provided in curl.

This workflow can be used with Public or Member API credentials on sandbox or the production servers.

## Build the authorization URL

| Parameter | Value        |
|--------------------|--------------------------|
| Base URL 				| https://sandbox.orcid.org/oauth/token|
| Client\_id 		| *Your client ID* |
| Client\_secret	| *Your client secret* |
| Response       | code |
| Grant\_type		| client\_credentials |
| Scope				| /authenticate |

**Example authorization url:**

```
https://sandbox.orcid.org/oauth/authorize?client_id=[Your client ID]&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground
```
   
## Grant authorization

Go to the authorization URL in your browser. If you have a sandbox ORCID account log into it then grant access. If you do not have and account, register for a new sandbox ORCID account and grant access.

## Get the authorization code

After granting access, you will be sent to your redirect URI. Appended to the end of the URI will be a 6-character authorization code.

Example redirect URI with authorization code:

``` https://developers.google.com/oauthplayground?code=eUeiz2```

## Exchange the authorization code

The authorization code can be exchanged for an access token and the user's ORCID iD.

| Option| Value        |
|--------------------|--------------------------|
| Base URL 				| https:<span>//sandbox.orcid.org/oauth/token|
| Method    | POST |
| Header    | accept:application/json |
| Data      | client\_id=[Your client ID]<br>client\_secret=[Your client secret]<br>grant_type=authorization_code<br>code=[Your authorization code] |

**Curl example:**

```curl -i -L -H "Accept: application/json" --data "client_id=[Your client ID]&client_secret=[Your client secret]&grant_type=authorization_code&code=eUeiz2" "https://sandbox.orcid.org/oauth/token"```

The response will include an access_token and refresh_token and the scopes and expiration time of those tokens as well as the user's ORCID iD and the name recorded on their ORCID record if it is public.

Example response:

```HTTP/1.1 200 OK
  ...
  {"access_token":"89f0181c-168b-4d7d-831c-1fdda2d7bbbb","token_type":"bearer",
  "refresh_token":"69e883f6-d84e-4ae6-87f5-ef0044e3e9a7","expires_in":631138518,
  "scope":"/authenticate","orcid":"0000-0001-2345-6789","name":"Sofia Garcia "}
  ```
  
Store the ORCID iD in your system
  
## Read the ORCID record
  
You can read public information on the ORCID record using the access token. 
  
Version is the the version of the API you are using, the latest stable release is v2.1.
Endpoint is the section of the record you want to read 'record' returns the entire record. [List of 2.1 endpoints](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.1#read-sections).

| Option| Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://sandbox.orcid.org/[version]/[ORCID iD]/[endpoint]|
| method    | GET |
| header    | Content-Type: application/orcid+xml OR  Content-Type: application/orcid+json|
| header    | Authorization: Bearer [Your access token]|

**Curl example:**

```
curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 89f0181c-168b-4d7d-831c-1fdda2d7bbbb' 'https://api.sandbox.orcid.org/v2.1/0000-0001-2345-6789/record' -i
```

  
