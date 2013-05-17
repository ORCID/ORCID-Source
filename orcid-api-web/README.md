# ORCID-API-Web (Member API)

## Getting Credentials
Send a request to support@orcid.org for credentials that are destined for either the dev sandbox or the production environment.

For creating a client locally see [OAUTH_DEV.md](OAUTH_DEV.md).

For working with webhooks see [WEBHOOKS.md](WEBHOOKS.md)

## Generate an Access Token
The following sequence of requests emulates a user interacting with the sandbox front end and authorizing a client application. Change the client credentials and callback URL to match your own client application.

Log onto the front end to initialize a session

      curl -i -d 'userId=you@youremail.edu' -d 'password=password' 'http://localhost:8080/orcid-api-web/login/check'
      
      HTTP/1.1 302 Moved Temporarily
      Server: Apache-Coyote/1.1
      Date: Mon, 29 Oct 2012 12:56:42 GMT
      Location: http://sandbox-1.orcid.org/workspace
      Transfer-Encoding: chunked
      Set-Cookie: JSESSIONID=E8DA6D14CC31863D80E9D7F686FA1EE9; Path=/; HttpOnly
       
      <html>...</html>


In the same session, navigate to the authorization form with the requested ORCID Scope.

      curl -i -L -b 'JSESSIONID=E8DA6D14CC31863D80E9D7F686FA1EE9' 'http://localhost:8080/orcid-api-web/oauth/authorize?client_id=0000-0003-4444-4444&response_type=code&scope=/orcid-bio/update&redirect_uri=www.yourdomain.edu/orcid-callback'
      
      HTTP/1.1 200 OK
      Server: Apache-Coyote/1.1
      Pragma: no-cache
      Expires: Thu, 01 Jan 1970 00:00:00 GMT
      Cache-Control: no-cache
      Cache-Control: no-store
      Content-Type: text/html;charset=UTF-8
      Content-Language: en-GB
      Transfer-Encoding: chunked
      Vary: Accept-Encoding
      Date: Mon, 29 Oct 2012 13:14:22 GMT


Still in the same session, submit confirmation

      curl -i -L -b 'JSESSIONID=E8DA6D14CC31863D80E9D7F686FA1EE9' --data "user_oauth_approval=true" 'http://localhost:8080/orcid-api-web/oauth/authorize'

      HTTP/1.1 302 Moved Temporarily
      Server: Apache-Coyote/1.1
      Cache-Control: no-cache
      Cache-Control: no-store
      Date: Mon, 29 Oct 2012 13:26:43 GMT
      Location: http://sandbox-1.orcid.org/oauth/yourdomain.edu/orcid-callback?code=hSFyhr
      Expires: Thu, 01 Jan 1970 00:00:00 GMT
      Pragma: no-cache
      Transfer-Encoding: chunked
      Content-Language: en-GB
      
      
      HTTP/1.1 404 Not Found
      Server: Apache-Coyote/1.1
      Content-Type: text/html;charset=UTF-8
      Content-Length: 1051
      Date: Mon, 29 Oct 2012 14:16:42 GMT


Generate an access token via the use-once authorization code

      curl -i -L -H 'Accept: application/json' --data 'client_id=0000-0003-4444-4441&client_secret=5f63d654-0f38-4ha5-b066-fd589ffd0df7&grant_type=authorization_code&code=hSFyhr&redirect_uri=yourdomain.edu/orcid-callback' 'http://localhost:8080/orcid-api-web/oauth/token'
      
      HTTP/1.1 200 OK
      Server: Apache-Coyote/1.1
      Cache-Control: no-store
      Pragma: no-cache
      Content-Type: application/json;charset=UTF-8
      Transfer-Encoding: chunked
      Date: Mon, 29 Oct 2012 14:17:38 GMT
      
      {"access_token":"355ec54b-9099-4e49-96c1-ef9c451c9e85","token_type":"bearer","refresh_token":"afa27bc1-17c5-4ca5-afec-446d69b1041f","expires_in":631138518,"scope":"/orcid-bio/read-limited","orcid":"0000-0003-1495-7121"


## Get record
below needs to be verified

      curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer fd1f0da1-aa1a-4f8b-8736-6d011463d6db' http://localhost:8080/orcid-api-web/0000-0003-1495-7121


## Update Works

      curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer fd1f0da1-aa1a-4f8b-8736-6d011463d6db' 'http://localhost:8080/orcid-api-web/0000-0003-1495-7121/orcid-works' -X POST --data '@/path/to/works.xml' http://localhost:8080/orcid-api-web/0000-0003-1495-7121/orcid-works

## Create record (If you have access to do so) 
Make sure you get our Authorization to Create Users

      curl -i -L -H 'Accept: application/json' -d 'client_id=0000-0002-5315-1318' -d 'client_secret=fd1a5e75-713a-4ca3-b9c3-a23164e6a263' -d 'scope=/orcid-profile/create' -d 'grant_type=client_credentials' 'http://localhost:8080/orcid-api-web/oauth/token'

Sample Create XML (note: email used must not already exist)

      <!--?xml version="1.0" encoding="UTF-8" standalone="yes"?-->
      <orcid-message xmlns="http://www.orcid.org/ns/orcid">
            <message-version>1.0.13</message-version>
      	<orcid-profile type="user">
      		<orcid-bio>
      			<personal-details>
      				<given-names>Laura</given-names>
      				<family-name>Paglione</family-name>
      				<other-names visibility="limited">
      					<other-name>Very few can see</other-name>
      					<other-name>Call me limited-access</other-name>
      					<other-name>Hidden from the Public</other-name>
      					<other-name>Peek a Boo</other-name>
      				</other-names>
      			</personal-details>
      			<researcher-urls>
      				<researcher-url>
      					<url-name>Harvard Catalyst Profile Page</url-name>
      					<url>http://connects.catalyst.harvard.edu/profiles/profile/person/32213</url>
      				</researcher-url>
      			</researcher-urls>
      			<contact-details>
      				<email>l.paglione+createemailtest+rob4@orcid.org</email>
      			</contact-details>
      			<affiliations>
      				<affiliation visibility="public">
      					<affiliation-name>PUBLIC: Harvard Medical School</affiliation-name>
      					<affiliation-type>current-primary-institution</affiliation-type>
      					<department-name>IT</department-name>
      					<role-title>Chief Technology Officer</role-title>
      				</affiliation>
      			</affiliations>
      		</orcid-bio>		
      		<orcid-activities>
      			<orcid-works>
      				<orcid-work>
      					<work-title><title>The co-morbidity burden of children and young adults with autism spectrum disorders.</title></work-title>
      					<work-type>journal-article</work-type>
      					<publication-date>
      						<year>2012</year>
      						<month>04</month>
      						<day>12</day>
      					</publication-date>
      					<work-external-identifiers>
      						<work-external-identifier>
      							<work-external-identifier-type>doi</work-external-identifier-type>
      							<work-external-identifier-id>10.1371/journal.pone.0033224</work-external-identifier-id>
      						</work-external-identifier>
      						<work-external-identifier>
      							<work-external-identifier-type>pmc</work-external-identifier-type>
      							<work-external-identifier-id>PMC3325235</work-external-identifier-id>
      						</work-external-identifier>
      					</work-external-identifiers>
      					<url>http://www.ncbi.nlm.nih.gov/pubmed/22511918</url>
      				</orcid-work>
      			</orcid-works>
      		</orcid-activities>
      	</orcid-profile>
      </orcid-message>



Sample Curl Statement Create Post

      curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer fd1f0da1-aa1a-4f8b-8736-6d011463d6db' -X POST --data '@/path/create.xml' http://localhost:8080/orcid-api-web/orcid-profile



#License
See [LICENSE.md](https://github.com/ORCID/ORCID-Work-in-Progress/blob/master/LICENSE.md)

