# Refresh tokens

This tutorial covers how to exchange a refresh token for a new access token. We suggest using refresh tokens to revoke and replace access tokens that have been compromised or to give a third party limited access and/or access for a limited time. For removing your access to a record entirely, we recommend the [revoke token process](https://github.com/ORCID/ORCID-Source/blob/master/orcid-api-web/tutorial/revoke.md).

This workflow can be used with Public or Member API credentials on sandbox or the production servers.

## Get a refresh token

Refresh tokens are included when generating an access token via either [2 step](https://github.com/ORCID/ORCID-Source/tree/master/orcid-api-web#generate-a-two-step-read-public-access-token) or [3 step OAuth](https://github.com/ORCID/ORCID-Source/tree/master/orcid-api-web#generate-an-oauth-access-token) authorization.

Integrations are encouraged to store refresh tokens returned with access tokens for possible future use.

Example access token request response with access and refresh token:
```
{"access_token":"67884cd3-f4ca-4195-9d38-b9d5ccc16d5f","token_type":"bearer","refresh_token":"4470d1ff-c817-45c1-86d1-b9062669c7cb","expires_in":631138518,"scope":"/read-limited /activities/update /person/update","name":"Sofia Maria Hernandez Garcia","orcid":"0000-0002-7900-5343"}
```

## Exchange the refresh token for an access token

| Parameter | Value        |
|--------------------|--------------------------|
| URL 				| https://sandbox.orcid.org/oauth/token|
| client_id 		| Your client ID |
| client_secret 		| Your client secret |
| refresh_token       | Your refresh token |
| grant_type       | refresh_token |
| revoke_old				| optional, set to "true" to revoke existing access token |
| scope       | optional, specify a subset of scopes |
| expires_in				| optional, specify the life of the new token in seconds |

**Example calls in curl:**

Create a new token with the same scopes and expiration and revoke the old token

```
curl -d 'refresh_token=4470d1ff-c817-45c1-86d1-b9062669c7cb' -d 'grant_type=refresh_token' -d 'client_id=APP-5GG5N5YFOKGV5N0X' -d 'client_secret=ce4db92f-d535-4014-a250-b5cdc27c0984' -d 'revoke_old=true' https://api.sandbox.orcid.org/oauth/token
```

Create a new token with a subset of scopes, expiring in one day and do not revoke the old token

```
curl -d 'refresh_token=4470d1ff-c817-45c1-86d1-b9062669c7cb' -d 'grant_type=refresh_token' -d 'client_id=APP-5GG5N5YFOKGV5N0X' -d 'client_secret=ce4db92f-d535-4014-a250-b5cdc27c0984' -d 'scope=/read-limited' -d 'expires_in=86400' https://api.sandbox.orcid.org/oauth/token
```
