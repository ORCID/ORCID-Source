# Get Authenticate iDs

This tutorial shows how to collect a user's authenticated ORCID iD using OAuth, the OAuth token can then be used to read the public record. Organizations who only need to collect authenticated iDs may want to consider the [implicit workflow](https://github.com/ORCID/ORCID-Source/blob/master/orcid-web/ORCID_AUTH_WITH_OPENID_CONNECT.md#implicit-flow).

This example worflow uses the `/authenticate` scope, it can also be completed use `openid` scope for organizations that want to use [OpenID Connect workflow](https://github.com/ORCID/ORCID-Source/blob/master/orcid-web/ORCID_AUTH_WITH_OPENID_CONNECT.md).

This workflow can be used with Public or Member API credentials on sandbox or the production servers.

## Build the authorization URL

| Parameter | Value        |
|--------------------|--------------------------|
| Base URL 				| https://sandbox.orcid.org/oauth/authorize|
| Client\_id 		| *Your client ID* |
| Response_type       | code |
| Scope				| /authenticate |
| Redirect URI				| *Your redirect uri* |

**Example authorization url:**

```
https://sandbox.orcid.org/oauth/authorize?client_id=APP-RU42Z8TDSYBG7T2S&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground
```

## Grant authorization

Go to the authorization URL in your browser. If you have a sandbox ORCID account log into it then grant access. If you do not have and account, register for a new sandbox ORCID account and grant access.

Users must grant authorization for you to get their authenticated ORCID iD and it must be completed in a browser window- this step can not be automated.

## Get the authorization code

After granting access, you will be sent to your redirect URI. Appended to the end of the URI will be a 6-character authorization code.

Example redirect URI with authorization code:

```
https://developers.google.com/oauthplayground?code=eUeiz2
```

## Exchange the authorization code

The authorization code can be exchanged for an access token and the user's ORCID iD.

| Option| Value        |
|--------------------|--------------------------|
| Base URL 				| https:<span>//sandbox.orcid.org/oauth/token|
| Method    | POST |
| Header    | accept:application/json |
| Data      | client\_id=[Your client ID]<br>client\_secret=[Your client secret]<br>grant_type=authorization_code<br>code=[Your authorization code] |

**Curl example:**

```curl -i -L -H "Accept: application/json" --data "client_id=APP-RU42Z8TDSYBG7T2S&client_secret=749daee6-c5ec-466a-b86b-b58453e3a01c&grant_type=authorization_code&code=eUeiz2" "https://sandbox.orcid.org/oauth/token"```

The response will include an access_token and refresh_token and the scopes and expiration time of those tokens as well as the user's ORCID iD and the name recorded on the ORCID record if it is public.

Example response:

```
HTTP/1.1 200 OK
  ...
  {"access_token":"89f0181c-168b-4d7d-831c-1fdda2d7bbbb","token_type":"bearer",
  "refresh_token":"69e883f6-d84e-4ae6-87f5-ef0044e3e9a7","expires_in":631138518,
  "scope":"/authenticate","orcid":"0000-0001-2345-6789","name":"Sofia Garcia "}
  ```

You will need to store at least the ORCID iD and access token in your local system.

## Read the ORCID record

You can read public information on the ORCID record using the access token.

Version is the the version of the API you are using, the latest stable release is v2.1.

Endpoint is the section of the record you want to read, 'record' returns the entire record. [List of 2.1 endpoints](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_2.1#read-sections).

### Member API

| Option| Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://api.sandbox.orcid.org/[version]/[ORCID iD]/[endpoint]|
| Method    | GET |
| Header    | Content-Type: application/vnd.orcid+xml OR  Content-Type: application/orcid+json|
| Header    | Authorization: Bearer [Your access token]|

**Curl example:**

```
curl -H 'Content-Type: application/vnd.orcid+xml' -H 'Authorization: Bearer 89f0181c-168b-4d7d-831c-1fdda2d7bbbb' 'https://api.sandbox.orcid.org/v2.1/0000-0001-2345-6789/record' -i
```

### Public API

| Option| Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://pub.sandbox.orcid.org/[version]/[ORCID iD]/[endpoint]|
| Method    | GET |
| Header    | Content-Type: application/vnd.orcid+xml OR  Content-Type: application/orcid+json|
| Header    | Authorization: Bearer [Your access token]|

**Curl example:**

```
curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer 89f0181c-168b-4d7d-831c-1fdda2d7bbbb' 'https://pub.sandbox.orcid.org/v2.1/0000-0001-2345-6789/works' -i
```
