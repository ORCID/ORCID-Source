## What is Token Delegation

Token Delegation allows an ORCID member to transfer permissions granted to their client to another member client. This allows the second client to take an action, such as posting to the user's ORCID record on behalf of the original client.

Token delegation can be used with Member API credentials on sandbox or the production servers using [version 3.0_rc2](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_3.0_rc2) or greater.

## How does it work?

**Via OAuth or Implicit flow**
An ORCID user grants permission to the original client following the standard [3 step OAuth process](https://github.com/ORCID/ORCID-Source/tree/master/orcid-api-web#authenticating-users-and-using-oauth--openid-connect) with the openid scope, as well as any other scopes the client wants. When the authorization code is exchanged, in addition to the access token an id_token is returned
This id_token is then securely passed to the second client. The second client exchanges the id_token for a new access token that they can use to read and update the record.

**From an existing access token**
The original client uses the existing access token to request an id_token. This id_token is then securily passed to the second client. The second client exchanges the id_token for a new access token which they can use to read and update the record.


## Example OAuth flow using curl

Note: this example uses the OAuth 'authorization code' flow. For the implicit/token flow please see the notes at the end of the document.

1. The original client (APP-CY6IU882C8WLCEVB) Sends the user to an authorization url with the openid scope and the other scopes they are requesting access to (/activities/update and /read/limited in this example)

```
    https://sandbox.orcid.org/oauth/authorize?client_id=[client-id]&response_type=code&scope=openid%20/activities/update%20/read-limited&redirect_uri=[redirect]
```

An authorization code is returned at the redirect_uri

2. The original client exchanges the authorization code at the oauth/token endpoint

```
    curl -i -L -H "Accept: application/json" --data "client_id=APP-CY6IU882C8WLCEVB&client_secret=[client-secret]&grant_type=authorization_code&code=[CODE]" "https://sandbox.orcid.org/oauth/token"
```

3. The token response contains an access token and an id_token:

```
    {"access_token":"bb5c8a64-8e52-4e6a-8316-ed273914fd29","token_type":"bearer","refresh_token":"aac1673e-8fc8-4bad-8341-7963128729e6","expires_in":631138518,"scope":"/read-limited openid /activities/update","name":"Sofia Maria Hernandez Garcia","orcid":"0000-0002-9227-8514","id_token":"eyJraWQiOiJxYS1vcmNpZC1vcmctcjlhZmw3cWY2aGNnN2c5bmdzenU1bnQ3Z3pmMGVhNmkiLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoiQzRzMXlWdVRUTmxPWHBjb3pRM2J2ZyIsImF1ZCI6IkFQUC1DWTZJVTg4MkM4V0xDRVZCIiwic3ViIjoiMDAwMC0wMDAyLTc5MDAtNTM0MyIsImF1dGhfdGltZSI6MTU0ODI3NzA3MSwiaXNzIjoiaHR0cHM6XC9cL3FhLm9yY2lkLm9yZyIsIm5hbWUiOiJTb2ZpYSBNYXJpYSBIZXJuYW5kZXogR2FyY2lhIiwiZXhwIjoyMTc5NDE1NjYzLCJnaXZlbl9uYW1lIjoiU29maWEgTWFyaWEiLCJpYXQiOjE1NDgyNzcxNDQsImZhbWlseV9uYW1lIjoiR2FyY2lhIiwianRpIjoiODgzMmZmYTktMDY5Ny00NzlkLThkMDgtMDI1ZGQ0ZDA2OWNiIn0.uKNxG3uD0xpC-rqqfYxPy7k2jlxRVY-rjXvNDLKYvxanfhxGAFE3K_45hPclA-Gly_qOCkTtTricPI31i934BbyhCHJbwMfikLSPQrVr1kn1ch4At-FPmSOhRVBmk31HwofFrxNSTHkwHgXVy0WY06OxTk4H58I_wUB0Kv1LgSFakpwpFSad6vFiLgzNuE5FWGXr8I-sCpZk8fG9wthaXq87zu1rTWF3q_pyEt33idUVAoIdtWdnYUUEfus-1bylEMaKaE2-y13i40otPnSEcRNL-8kLkD28b5LIzzpOJmaCH1Xra_5QkFatRJn6GQUOagZYvTDD8gtNK7gK2uy-yw"}
```

4. The original client shares the id_token with the second client who will be adding information

5. The second client (APP-HAYMC6PX5GI1YSM6) sends a request with the id_token to the oauth/token endpoint

```
    curl -i -L -H "Accept: application/json" --data "client_id=APP-HAYMC6PX5GI1YSM6&client_secret=[2nd client secret]&grant_type=urn:ietf:params:oauth:grant-type:token-exchange&subject_token=eyJraWQiOiJxYS1vcmNpZC1vcmctcjlhZmw3cWY2aGNnN2c5bmdzenU1bnQ3Z3pmMGVhNmkiLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoiQzRzMXlWdVRUTmxPWHBjb3pRM2J2ZyIsImF1ZCI6IkFQUC1DWTZJVTg4MkM4V0xDRVZCIiwic3ViIjoiMDAwMC0wMDAyLTc5MDAtNTM0MyIsImF1dGhfdGltZSI6MTU0ODI3NzA3MSwiaXNzIjoiaHR0cHM6XC9cL3FhLm9yY2lkLm9yZyIsIm5hbWUiOiJTb2ZpYSBNYXJpYSBIZXJuYW5kZXogR2FyY2lhIiwiZXhwIjoyMTc5NDE1NjYzLCJnaXZlbl9uYW1lIjoiU29maWEgTWFyaWEiLCJpYXQiOjE1NDgyNzcxNDQsImZhbWlseV9uYW1lIjoiR2FyY2lhIiwianRpIjoiODgzMmZmYTktMDY5Ny00NzlkLThkMDgtMDI1ZGQ0ZDA2OWNiIn0.uKNxG3uD0xpC-rqqfYxPy7k2jlxRVY-rjXvNDLKYvxanfhxGAFE3K_45hPclA-Gly_qOCkTtTricPI31i934BbyhCHJbwMfikLSPQrVr1kn1ch4At-FPmSOhRVBmk31HwofFrxNSTHkwHgXVy0WY06OxTk4H58I_wUB0Kv1LgSFakpwpFSad6vFiLgzNuE5FWGXr8I-sCpZk8fG9wthaXq87zu1rTWF3q_pyEt33idUVAoIdtWdnYUUEfus-1bylEMaKaE2-y13i40otPnSEcRNL-8kLkD28b5LIzzpOJmaCH1Xra_5QkFatRJn6GQUOagZYvTDD8gtNK7gK2uy-yw&subject_token_type=urn:ietf:params:oauth:token-type:id_token&requested_token_type=urn:ietf:params:oauth:token-type:access_token" "https://sandbox.orcid.org/oauth/token"
```

6. A new access token is generated for the second client

```
    {"access_token":"cefe19d2-3dcc-4331-b2d8-112b73329277","token_type":"bearer","expires_in":3599,"scope":"/read-limited openid /activities/update","issued_token_type":"urn:ietf:params:oauth:token-type:access_token","name":"Sofia Maria Hernandez Garcia","orcid":"0000-0002-9227-8514"}
```

7. The second client uses the access token to read and post activities to the record

8. The added items appear on the user's ORCID record, the second client is listed as the source and the original client is listed as the assertion origin source.

```
                    <common:source>
                        <common:source-client-id>
                            <common:uri>https://sandbox.orcid.org/client/APP-HAYMC6PX5GI1YSM6</common:uri>
                            <common:path>APP-HAYMC6PX5GI1YSM6</common:path>
                            <common:host>sandbox.orcid.org</common:host>
                        </common:source-client-id>
                        <common:source-name>Second client</common:source-name>
                        <common:assertion-origin-client-id>
                            <common:uri>https://sandbox.orcid.org/client/APP-CY6IU882C8WLCEVB</common:uri>
                            <common:path>APP-CY6IU882C8WLCEVB</common:path>
                            <common:host>sandbox.orcid.org</common:host>
                        </common:assertion-origin-client-id>
                        <common:assertion-origin-name>Original Client</common:assertion-origin-name>
                    </common:source>
```


## Existing access token flow using curl

Clients who have already received permission from the user can generate an id_token to share from an existing access token


1. The origianl client (APP-CY6IU882C8WLCEVB) uses an existing token that was previously issued to that client to generate an open_id token

```
    curl -i -L -H "Accept: application/json" --data "client_id=APP-CY6IU882C8WLCEVB&client_secret=[client-secret]&subject_token=[access token]&grant_type=urn:ietf:params:oauth:grant-type:token-exchange&subject_token_type=urn:ietf:params:oauth:token-type:access_token&requested_token_type=urn:ietf:params:oauth:token-type:id_token" "https://sandbox.orcid.org/oauth/token"
```

2. The original client shares the id_token with the second client who will be adding information

3. The second client (APP-HAYMC6PX5GI1YSM6) sends a request with the id_token to the oauth/token endpoint

```
    curl -i -L -H "Accept: application/json" --data "client_id=APP-HAYMC6PX5GI1YSM6&client_secret=[2nd client secret]&grant_type=urn:ietf:params:oauth:grant-type:token-exchange&subject_token=eyJraWQiOiJxYS1vcmNpZC1vcmctcjlhZmw3cWY2aGNnN2c5bmdzenU1bnQ3Z3pmMGVhNmkiLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoiQzRzMXlWdVRUTmxPWHBjb3pRM2J2ZyIsImF1ZCI6IkFQUC1DWTZJVTg4MkM4V0xDRVZCIiwic3ViIjoiMDAwMC0wMDAyLTc5MDAtNTM0MyIsImF1dGhfdGltZSI6MTU0ODI3NzA3MSwiaXNzIjoiaHR0cHM6XC9cL3FhLm9yY2lkLm9yZyIsIm5hbWUiOiJTb2ZpYSBNYXJpYSBIZXJuYW5kZXogR2FyY2lhIiwiZXhwIjoyMTc5NDE1NjYzLCJnaXZlbl9uYW1lIjoiU29maWEgTWFyaWEiLCJpYXQiOjE1NDgyNzcxNDQsImZhbWlseV9uYW1lIjoiR2FyY2lhIiwianRpIjoiODgzMmZmYTktMDY5Ny00NzlkLThkMDgtMDI1ZGQ0ZDA2OWNiIn0.uKNxG3uD0xpC-rqqfYxPy7k2jlxRVY-rjXvNDLKYvxanfhxGAFE3K_45hPclA-Gly_qOCkTtTricPI31i934BbyhCHJbwMfikLSPQrVr1kn1ch4At-FPmSOhRVBmk31HwofFrxNSTHkwHgXVy0WY06OxTk4H58I_wUB0Kv1LgSFakpwpFSad6vFiLgzNuE5FWGXr8I-sCpZk8fG9wthaXq87zu1rTWF3q_pyEt33idUVAoIdtWdnYUUEfus-1bylEMaKaE2-y13i40otPnSEcRNL-8kLkD28b5LIzzpOJmaCH1Xra_5QkFatRJn6GQUOagZYvTDD8gtNK7gK2uy-yw&subject_token_type=urn:ietf:params:oauth:token-type:id_token&requested_token_type=urn:ietf:params:oauth:token-type:access_token" "https://sandbox.orcid.org/oauth/token"
```

4. A new access token is generated for the second client

```
    {"access_token":"cefe19d2-3dcc-4331-b2d8-112b73329277","token_type":"bearer","expires_in":3599,"scope":"/read-limited openid /activities/update","issued_token_type":"urn:ietf:params:oauth:token-type:access_token","name":"Sofia Maria Hernandez Garcia","orcid":"0000-0002-9227-8514"}
```

5. The second client uses the new access token to read and post activities to the record

6. The added item appears on the user's ORCID record, the second client is listed as the source and the original client is listed as the assertion origin source.

```
                    <common:source>
                        <common:source-client-id>
                            <common:uri>https://sandbox.orcid.org/client/APP-HAYMC6PX5GI1YSM6</common:uri>
                            <common:path>APP-HAYMC6PX5GI1YSM6</common:path>
                            <common:host>sandbox.orcid.org</common:host>
                        </common:source-client-id>
                        <common:source-name>Second client</common:source-name>
                        <common:assertion-origin-client-id>
                            <common:uri>https://sandbox.orcid.org/client/APP-CY6IU882C8WLCEVB</common:uri>
                            <common:path>APP-CY6IU882C8WLCEVB</common:path>
                            <common:host>sandbox.orcid.org</common:host>
                        </common:assertion-origin-client-id>
                        <common:assertion-origin-name>Original Client</common:assertion-origin-name>
                    </common:source>
```

## API Calls

**Call to get an id_token with an existing access token**

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://[host]/oauth/token |
| Method    | POST |
| data      | client_id=[Your client iD] |
| data      | client_secret=[Your client secret] |
| data      | client_id=[Your client iD] |
| data      | subject_token=[Original access token] |
| data      | grant_type=urn:ietf:params:oauth:grant-type:token-exchange|
| data      | subject_token_type=urn:ietf:params:oauth:token-type:access_token|
| data      | requested_token_type=urn:ietf:params:oauth:token-type:id_token|
| header      | Accept: application/json|

**Call to exchange an id_token for an access token**

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://[host]/oauth/token |
| Method    | POST |
| data      | client_id=[Your client iD] |
| data      | client_secret=[Your client secret] |
| data      | client_id=[Your client iD] |
| data      | subject_token=[Your id_token] |
| data      | grant_type=urn:ietf:params:oauth:grant-type:token-exchange|
| data      | subject_token_type=urn:ietf:params:oauth:token-type:id_token|
| data      | requested_token_type=urn:ietf:params:oauth:token-type:access_token|
| header      | Accept: application/json|

## **Notes** 

* On behalf of must be enabled for clients using it- contact https://orcid.org/help/contact-us if you are interested in using this workflow.
* Access Tokens issued from id_tokens include all scopes that were granted to the original client by that user, not just scopes granted when the openid scope was requested.
* The OAuth flow can be completed use the [implicit flow](https://github.com/ORCID/ORCID-Source/blob/master/orcid-web/ORCID_AUTH_WITH_OPENID_CONNECT.md#implicit-flow) instead of OAuth, if implicit is used the token returned to the original client will only have the /read-public scope, but tokens generated by the second client from the id_token will have all scopes that the user has granted permission for.
* Assertion origin source is only returned in version 3.0+ of the API
* Access tokens generated via token delegation expire after one hour, however, id_tokens do not expire unless revoked and can be used to generate new access tokens.
