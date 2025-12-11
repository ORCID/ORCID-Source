## **What is OpenID connect?** 

OpenID Connect 1.0 is a simple identity layer on top of the OAuth 2.0 protocol.  It supplements existing OAuth authentication flows and provides information about users to clients in a well described manner.  

## **Why OpenID connect?** 

OpenID connect is a standardised way of implementing OAuth and sharing information about authenticated users.  It will now be possible to configure services to use ORCID "out of the box" alongside other standards compliant OpenID connect providers.  OpenID connect also provides sharable ID tokens, which are signed objects that can prove a user authenticated using ORCID at a specific time.  These tokens can be used by user interface elements to maintain user sessions.

## **What does ORCID support?**

ORCID supports the Basic OpenID Provider conformance profile, which is an extension of the OAuth authorization code flow.   ORCID also supports the implicit token flow for the "/authenticate" and "openid" scopes. In addition, for the Member API only,  ORCID provides data about the authentication method set up and used by ORCID users.


This means that ORCID:

*   Embeds signed id tokens within token responses for authorization codes generated with the 'openid' scope
*   Supports the implicit flow when using 'token' or 'id_token token' response_types and the 'openid' scope. 
*   Supports the 'prompt' and 'nonce' parameters for authorisation requests that include the 'openid' scope.
*   Supports Openid Connect discovery and userinfo endpoints
*   The payload from the id_token now contains a new data field called “amr” in which the value is “mfa” for users who have enabled two-factor authentication on their ORCID account, and “pwd” for users who haven’t. ( This data is not returned by the Public API only the Member API)

## **How does it work?**

Initiating an OpenID Connect authentication works the same way as a regular OAuth authentication.  All that is required is that the client request the 'openid' scope If you are using the /authenticate scope replace it with openid, as authenticate and openid have the same authorization only one or the other should be used. If you are using any other scopes, add openid to the list of scopes requested. When the openid scope is included, the Registry will return an id_token inside the token response and grant the client permission to access the user info endpoint for that user.

Note that the 'openid' scope does not start with a '/' like the other ORCID API scopes.  This is because the 'openid' scope is not defined by ORCID, but instead defined by the OpenID Connect specification.

## **Authorization code flow**

For example, this browser request will return a code as usual:

```
    https://orcid.org/oauth/authorize?client_id=[client-id]&response_type=code&scope=openid&redirect_uri=[redirect]
```

The code is exchanged for a token:

```
    curl -i -L -H "Accept: application/json" --data "client_id=[client-id]&client_secret=[client-secret]&grant_type=authorization_code&code=[YOUR_CODE]&redirect_uri=[redirect]" "https://orcid.org/oauth/token"
```

And the token response now contains an id_token property:

```
    {"access_token":"aa4629f3-b0a2-4edd-b77a-398d7afe3c90","token_type":"bearer","refresh_token":"5a712062-0068-47b9-922e-f26a2c5cb250","expires_in":631138518,"scope":"openid","name":"Name","orcid":"0000-0002-2601-8132","id_token":"eyJraWQiOiJPcGVuSURUZXN0S2V5MSIsImFsZyI6IlJTMjU2In0.eyJhdWQiOiI0NDQ0LTQ0NDQtNDQ0NC00NDQ1Iiwic3ViIjoiMDAwMC0wMDAyLTI2MDEtODEzMiIsImF1dGhfdGltZSI6MTQ5NTcwNzI1NywiaXNzIjoiaHR0cHM6XC9cL29yY2lkLm9yZyIsImV4cCI6MTQ5NTcwNzg3MywiaWF0IjoxNDk1NzA3MjczLCJub25jZSI6Im4xIiwianRpIjoiZWFhNGQ1NjMtYmU2My00N2VlLTg5NzYtZGM3Y2MzYjdiZTYxIn0.Pt1wfwo7CHjYBKKgsCQiG4l3tHiDqIJ9t2PMSdBh568FK2gtPvVuuZHS-6rDWY2dhjaFELOYUVAPRHGsh65ERZ5drBurL-GKtUrkapT1W1n83Neq9Ls1QMshG91YEI9feo4CL0Ar9FQnC-ngHnSS37Ld3etkVO2meotWsAjmRK7eW18qJY3zAvxlN9TFjOQry9UGQMhHd0IGobvZG8rOfrXUJddXm7wcyK1RpgTdeZFhAfwJb1s1WQR1MUrDSIrP2BnLuAqPLPIoSRnvKP7vvZy3GkCRypaHPfEqZFq1iTZTNddlKCzrwb6bOF5HwuhE2c8CwiekAT7ku8s253fweQ"}
```

The user info page can be requested in the same way as a regular API request:

```
    curl -i -L -H "Accept: application/json" -H "Authorization: Bearer aa4629f3-b0a2-4edd-b77a-398d7afe3c90" 'https://orcid.org/oauth/userinfo'
```

And will respond with a json document like this:

```
    {"sub":"0000-0002-2601-8132","name":"Credit Name","family_name":"Jones","given_name":"Tom"}
```

The public key required to check the signature can be found at [https://orcid.org/oauth/jwks](https://orcid.org/oauth/jwks) and looks something like this:

```
    {"kty":"RSA","e":"AQAB","use":"sig","kid":"OpenIDTestKey1","alg":"RS256","n":"qCtxWP2HppC8PBEXUh6b5RPECAzQS01khDwbxCSndO-YtS1MYpNlmtUgdtoAEoIP9TFMqXOsltKmGFioy0CeWLi53M-iX-Ygjd3zSQAbr0BU0-86somdbIlFxuvGA8v6AC7MNlICTwbGExCufL_hivrzF1XVqi5zIovM1LA8k2bP4BKMEjNwhGBGJ0E9KcQYv65foZr9K0C6YYJDFE6YqsHP_czvbI1ij7MfDvN5cwmHRGMGOyzDCmT_SmjoZAZ4vSXbl2wI5txIj70RLLSK4oahktb-09c0lDVYpCno7LqsLR8E3DuTUniYwYMHlXeBor_G7sJw2a}
```

## **Implicit flow**

The implicit flow is designed so that clients do not need to use their secret key to initiate ORCID sign in.  Security is enforced by restricting clients to their registered redirect_urls.  This lower level of security means that ORCID only supports the /authenticate and openid scopes when using the implicit flow.  Tokens are also short lived, with a 10 minute lifespan.  This flow is recommended for client side applications which do not have access to a back end server, for example phone applications or single page javascript web-apps.

For example, this request:

```
    https://qa.orcid.org/oauth/authorize?response_type=token&redirect_uri=http%3A%2F%2Flocalhost%3A&client_id=APP-6LKIJ3I5B1C4YIQP&scope=openid&nonce=whatever
```

Returns a response with an access token and id_token:

```
    http://localhost/#access_token=24c11342-f5da-4cf9-94a4-f8a72a30da00&token_type=bearer&expires_in=599&tokenVersion=1&persistent=false&id_token=eyJraWQiOiJxYS1vcmNpZC1vcmctcjlhZmw3cWY2aGNnN2c5bmdzenU1bnQ3Z3pmMGVhNmkiLCJhbGciOiJSUzI1NiJ9.eyJhdF9oYXNoIjoiMW52bXZBbVdwaVd0Z3ZKZW1DQmVYUSIsImF1ZCI6IkFQUC02TEtJSjNJNUIxQzRZSVFQIiwic3ViIjoiMDAwMC0wMDAyLTUwNjItMjIwOSIsImF1dGhfdGltZSI6MTUwNTk4Nzg2MiwiaXNzIjoiaHR0cHM6XC9cL29yY2lkLm9yZyIsIm5hbWUiOiJNciBDcmVkaXQgTmFtZSIsImV4cCI6MTUwNTk4ODQ2MywiZ2l2ZW5fbmFtZSI6IlRvbSIsImlhdCI6MTUwNTk4Nzg2Mywibm9uY2UiOiJ3aGF0ZXZlciIsImZhbWlseV9uYW1lIjoiRGVtIiwianRpIjoiY2U0YzlmNWUtNTBkNC00ZjhiLTliYzItMmViMTI0ZDVkNmNhIn0.hhhts2-4-ibjXPW6wEsFRaNqV_A-vTz2JFloYn7mS1jzQt3xuHiSaSIiXg3rpnt1RojF_yhcvE9Xe4SOtYimxxVycpjcm8yT_-7lUSrc46UCt9qW6gV7L7KQyKDjNl23wVwIifpRD2JSnx6WbuC0GhAxB5-2ynj6EbeEEcYjAy2tNwG-wcVlnfJLyddYDe8AI_RFhq7HrY4OByA91hiYvHzZ8VzoRW1s4CTCFurA7DoyQfCbeSxdfBuDQbjAzXuZB5-jD1k3WnjqVHrof1LHEPTFV4GQV-pDRmkUwspsPYxsJyKpKWSG_ONk57E_Ba--RqEcE1ZNNDUYHXAtiRnM3w
```

The  id token can be used in the same way as described above.

## **What is an id_token?** 

The id_token value is a JSON web token ([JWT](https://jwt.io/)) that has been signed and base64 encoded. The string is in three parts, separated by a period.  The first section is the header and contains information on how the JWT was signed.  The middle section contains useful information such as the subject (in our case, the ORCID ID), audience, issuer, issue time, expiry time and authentication time and, if requested using the member API, information about the users authentication method. It will also include a nonce if this was in the original request.  The final section is the signature.  There are many client libraries that will take the jwks key we provide and validate the token.  The middle part of the id_token shown above looks like this when decoded:

```
    {"aud":"4444-4444-4444-4445","sub":"0000-0002-2601-8132","auth_time":1601920037,"amr":"pwd","iss":"https:\/\/orcid.org","exp":1602006492,"iat":1601920092,"nonce":"n1","jti":"eaa4d563-be63-47ee-8976-dc7cc3b7be61"}
```

## **Query parameters** 

ORCID now supports the following behaviour during authorization requests that include the openid scope:

*   **prompt=none**:  If the user is not already logged in, return the browser to the redirect url with an error as a query string parameter:
    *   **error=login_required**  : the user does not have an ORCID session
    *   **error=interaction_required** : the user has not granted required permissions
*   **prompt=login** :  If the user is already logged in, force them to reauthenticate
*   **nonce=String** :  This nonce will be returned in the id_token.

## **Other endpoints** 

ORCID now supports several ancillary OpenID connect endpoints:

*   **Discovery endpoint** : This contains details about the server and can be used by some implementations to auto-configure authentication.
[https://orcid.org/.well-known/openid-configuration](https://orcid.org/.well-known/openid-configuration)
*   **JWTS endpoint** : This exposes our public signing key.  There is an example shown above.
[https://orcid.org/oauth/jwks](https://orcid.org/oauth/jwks)
*   **User info endpoint** : This exposes information about the logged in user.  It requires authentication and is protected by the 'openid' scope.  There is an example shown above.**
**[https://orcid.org/oauth/userinfo](https://orcid.org/oauth/userinfo)

## **Example implementations** 

[Js-orcid-jwt](https://github.com/ORCID/orcid-spring-oauth-examples/tree/master/js-orcid-jwt)

One page javascript application that implements implicit ORCID login.  Only 45 lines long!

[Really-simple-orcid-oauth](https://github.com/ORCID/orcid-spring-oauth-examples/tree/master/really-simple-orcid-oauth)

Configures Java Spring to work with ORCID OpenID connect with only 36 lines of code.  Nothing else required!

[Simple-orcid-jwt](https://github.com/ORCID/orcid-spring-oauth-examples/tree/master/boot-orcid-jwt)

This app uses ORCID JWT tokens for security. It is written using Spring Boot and Java.  This is done in a stateless manner, no server side sessions are created.

[Boot-orcid-openid](https://github.com/ORCID/orcid-spring-oauth-examples/tree/master/boot-orcid-openid)

This example uses the ORCID JWT tokens to create a local user session.  It is also written using Spring Boot and Java, but relies on less 'magic' and enables finer grain security patterns.

## **Further reading**

[OpenID Connect core specification](http://openid.net/specs/openid-connect-core-1_0.html)

[OpenID Connect conformance profiles](http://openid.net/wordpress-content/uploads/2016/12/OpenID-Connect-Conformance-Profiles.pdf)

[Using OpenID connect with chrome extensions](https://developer.chrome.com/apps/app_identity#non)
