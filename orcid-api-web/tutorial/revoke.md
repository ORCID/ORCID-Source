# Revoke an access token

This tutorial covers how to use the API to revoke an access token that has been issued to your client. You can revoke tokens created via either the two-step or three-step OAuth processes. When revoking an access token the associated refresh token is also removed, the refresh token can also be used in the steps below to revoke an access token. If you have multiple tokens, all tokens with the same scope for a given ORCID iD will be revoked when you revoke any one token, tokens with a different set of scopes or issued for another iD will not be affected. 

Users can also revoke access tokens at any time by [removing trusted organization permissions](https://support.orcid.org/knowledgebase/articles/131598-trusted-organizations#03) from their personal account settings. 

We suggest revoking access tokens in the following conditions:

* If the token was issued to a third-party supplier after the termination of a relationship;
* If the users disconnects their ORCID iD from your system;
* To allow users to revoke tokens from within your system.

We recommend using the [refresh tokens workflow](/orcid-api-web/tutorial/refresh_tokens.md) to limit the scope or duration of an existing access token or update a token if it has been compromised.

This workflow can be used with Public or Member API credentials on sandbox or the production servers.

## Call to revoke an access token

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https<i></i>://[host]/oauth/revoke|
| Method | POST |
| client\_id 		| *Your client ID* |
| client\_secret 		| *Your client secret* |
| token       | *The access or refresh token to be revoked* |

**Example calls in curl:**

Original token response:

```
{"access_token":"4ec62207-1d93-4396-9c24-8294893a791d","token_type":"bearer","refresh_token":"55ad9d2f-c392-4e1d-b55b-2fa74a964817","expires_in":631138518,"scope":"/read-limited /activities/update /person/update","name":"Sofia Garcia","orcid":"0000-0001-2345-6789"}
```

Example call:

```
curl -i -L -H "Accept: application/json" --data "client_id=APP-NPXKK6HFN6TJ4YYI&client_secret=060c36f2-cce2-4f74-bde0-a17d8bb30a97&token=4ec62207-1d93-4396-9c24-8294893a791d" "https://sandbox.orcid.org/oauth/revoke"
```

Example response:

```
HTTP/1.1 200 OK
```
