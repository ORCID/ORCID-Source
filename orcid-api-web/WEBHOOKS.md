# Webhooks (Member API)

## Get an access token for webhooks

Make sure you get our Authorization to register webhooks.

Get an access token using the /webhook scope.

      curl -i -L -H 'Accept: application/json' -d 'client_id=0000-0002-5315-1318' -d 'client_secret=fd1a5e75-713a-4ca3-b9c3-a23164e6a263' -d 'scope=/webhook' -d 'grant_type=client_credentials' 'http://localhost:8080/orcid-api-web/oauth/token

Example access token response:

      HTTP/1.1 200 OK
      Server: Apache-Coyote/1.1
      Cache-Control: no-store
      Pragma: no-cache
      Content-Type: application/json;charset=UTF-8
      Transfer-Encoding: chunked
      Date: Fri, 05 Apr 2013 13:05:01 GMT
      
      {"access_token":"5eb23750-1e19-47a3-b6f6-26635c34e8ee","token_type":"bearer","refresh_token":"c7d3d5fd-e4c0-4825-89f2-7cfb4a1cf01e","expires_in":631138518,"scope":"/webhook"}

## Register a webhook

URL-encode the URL that you want ORCID to call when the user's record is updated.

For example the following URL:

      http://nowhere2.com/0000-0002-7253-3645/updated

becomes

      http%3A%2F%2Fnowhere2.com%2F0000-0002-7253-3645%2Fupdated

Now build the full URL for the ORCID API call:

      http://localhost:8080/orcid-api-web/{ORCID}/webhook/{URL-ENCODED-WEBHOOK-URL}

For example, using the webhook URL above, and the ORCID 0000-0002-7253-3645:

      http://localhost:8080/orcid-api-web/0000-0002-7253-3645/webhook/http%3A%2F%2Fnowhere2.com%2F0000-0002-7253-3645%2Fupdated

Use your webhook access token to register your webhook against the user's ORCID record.

      curl -v -H 'Accept: application/json' -H 'Authorization: Bearer 5eb23750-1e19-47a3-b6f6-26635c34e8ee' -X PUT 'http://localhost:8080/orcid-api-web/0000-0002-7253-3645/webhook/http%3A%2F%2Fnowhere2.com%2F0000-0002-7253-3645%2Fupdated'

You need to use an HTTP PUT request, but you should not include anything in the body of the request.

The response should be a 201 Created.

      HTTP/1.1 201 Created
      Server: Apache-Coyote/1.1
      Location: http://localhost:8080/orcid-api-web/0000-0001-5775-9892/webhook/http%3A%2F%2Fnowhere2.com%2F0000-0002-7253-3645%2Fupdated
      Content-Type: application/json;charset=UTF-8
      Content-Length: 0
      Date: Fri, 05 Apr 2013 12:43:00 GMT

If the callback already existed, then the response will be 204 No Content.

## Unregister a webhook

The URL for unregistering a webhook is the same as for registering. However, you need to use the HTTP DELETE method.

      curl -v -H 'Accept: application/json' -H 'Authorization: Bearer 5eb23750-1e19-47a3-b6f6-26635c34e8ee' -X DELETE 'http://localhost:8080/orcid-api-web/0000-0002-7253-3645/webhook/http%3A%2F%2Fnowhere2.com%2F0000-0002-7253-3645%2Fupdated'

The response should be 204 No Content.

      HTTP/1.1 204 No Content
      Server: Apache-Coyote/1.1
      Date: Fri, 05 Apr 2013 12:49:17 GMT

## Receiving the webhook call

Now that you have registered your webhook URL, you will get a callback whenever the user's ORCID record is updated.

ORCID will do the following HTTP call.

      curl -v -X POST http://nowhere2.com/0000-0002-7253-3645/updated

The request uses the HTTP POST method, but the body of the request is empty.

Your server should respond with standard HTTP response codes. So, if the call was successful you should return 204 No Content.

      HTTP/1.1 204 No Content

Any 2xx response code means that the call was successful.

If you return a code that is not a 2xx, then we will retry the call later.

The callback will not be immediate. You may get the callback some time after the record was changed.