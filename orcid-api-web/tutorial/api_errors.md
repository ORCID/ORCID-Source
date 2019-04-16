# API Errors and their meanings

This guide is to help diagnose any trouble you may be having with API calls. Below you will find a list of common error codes, their possible meanings, how to troubleshoot them and examples where useful.


## Tips for debugging your cURL

* Check the tokens
* check the file path
* check the end point (is it singular or plural?)


## Common problems

* XML formatted wrongly
* No put code in xml
* Wrong put code in xml
* Reusing old tokens that have been revoked or when a new token has been generated
* File path to XML is wrong
* Incorrect API used
* Wrong or incorrect file path
* Wrong or mispelled endpoint




 For additional help please contact support@orcid.org.

 ## List of Error codes and solutions

|Error Code |	Message	|Possible Solution|Example|
|-----------|---------|-----------------|-------|
|301	|Moved Permanently|	This ORCID iD has been deprecated into another, see the location returned for the updated ORCID iD if you are not automatically forwarded| **Example of location returned in error message:** `<developer-message>301 Moved Permanently: This account is deprecated. Please refer to account: https://qa.orcid.org/0000-0000-0000-0000. ORCID https://qa.orcid.org/0000-1111-0000-0000</developer-message>`
|302|Found|Check that you are making a call to the api url not the web interface. The URL should start http://api.sandbox.orcid.org|**Incorrect call notice how the URL has no 'api'**  curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer ************************' 'https:/orcid.org/v3.0/0000-0002-4575-651X/works'|
|400| The client application sent a bad request to ORCID. Full validation error: argument type mismatch|An example of this error occurring is when a post is made for bulk works using the work endpoint.  ... -X POST 'https://api.sandbox.orcid.org/v3.0_rc1/0000-0002-4575-651X/work' instead of -X POST 'https://api.sandbox.orcid.org/v3.0_rc1/0000-0002-4575-651X/works'
|400|	Bad Request	Check XML headers|--|--|
|400|Bad Request Please specify the API Version you want to PUT to|Although this says check API version this can happen if your ORCID's are inconsistent in the call and XML. Check both.|--|
|400|	Bad Request /  Value * is not facet-valid with respect to enumeration	|Check that the version specified in the XML header and URL match|--|
|400|	Content is not allowed in prolog	|Ensure that you are pointing to the correct file|**Example missing '@' at the start of the file path** curl -i -H 'Content-type: application/vnd.orcid+xml' -H 'Authorization: Bearer  ********************' -d 'Users/rob/code/XML/address-2.1.xml' -X POST 'https://api.qa.orcid.org/v2.1/0000-0002-3631-3071/address'|
|400|	Premature end of file	|Check the URL to which you are posting, the formatting of your XML and your file path to the XML as you can get this error for all of these issues|--|
|400|	Exception: unexpected element	|Ensure that your XML is valid|--|
|400|	Invalid incoming message:	|Check your XML, make sure required fields are completed.|--|
|400 |Bad Request: There is an issue with your data or the API endpoint. with org.xml.sax.SAXParseException; lineNumber: foo; columnNumber: bar; Content is not allowed in prolog.] (Content is not allowed in prolog| Check your file path. This error message can actually mean that the api can't find your file. Have you missed the '@' or added a rogue space perhaps?|
|400|The client application sent a bad request to ORCID. Full validation error: argument type mismatch|This can be because of a scope typo. Check whether you are using singular or plural (education or educations for example)|--|
|400 |Bad Request: The put code in the URL was [number] whereas the one in the body was null.| Check that your XML has a put code and that it is correct. |Your XML should look like the following in the second line of your XML `<?xml version="1.0" encoding="UTF-8"?><external-identifier:external-identifier put-code="4910"``|
|401|	Unauthorized|	Check the syntax of your call, particularly around the client-id|--|
|401|	Access token expired|	Your access token has expired or been revoked, you will need to ask for access again.|--|
|401|	Client not found|	Ensure that your client iD is correct|--|
|401|	Bad client credentials|	Ensure that your client secret is correct|--|
|401|	Invalid access token|1.	Ensure that the access token used for the call is complete, matched to the ORCID iD and scope of the call, and is not expired, or that the user has not revoked access 2. Check that you are calling the correct API for the client |Example of incorrect API  (api.orcid.org not api.sandbox.orcid.org for example.)|
|403|Forbidden / Illegal state...	|Check the ORCID iDs of any contributors being added with the work|--|
|403|	HTML error message	|Check the syntax of your call, an extra space or missing character may be preventing the system from recognizing this as an API request.|--|
|403|	Security problem : Insufficient or wrong scope|	Check that the access token has permission for the action you are taking and that you are using the correct end-point and method.|--|
|403|	Security problem: You do not have the required permissions.|	Ensure that you have been granted permission to access the requested ORCID iD|--|
|403|	Access Denied	|Check the URL of the request|--|
|403|Forbidden: You are not the source of the work, so you are not allowed to update it|This is because you are trying to modify something that your API client did not put there in the first place ( if the user added an address for example you wouldn't be able to update it)|--|
|404|	ORCID ... not found	|Check that the ORCID iD is correct and is claimed. Records that were created in the last 10 days and are not yet claimed will return this error|--|
|405|	Method Not Allowed	|Ensure that the scope of your authorization token matches the call you are making, and that you are posting to the Member API base URL (i.e., api.sandbox.orcid.org)|--|
|406|	Not Acceptable|	Check the header you are using. It should be either 'Accept: application/xml' or 'Accept: application/json' It also must match the content type header if provided.|--|
|409|	Access Denied; Account locked : The given account ... is locked|	This record was flagged as violating ORCID's Terms of Use and has been hidden from public view.|--|
|409|Conflict: You have already added this activity (matched by external identifiers.)|This one is quite explicit, the activity you are trying to add is already on the record|--|
|413|	Request Entity Too Large|Bulk work posts are limited to 100 items check your XML does not have too many items|--|
|415|	Unsupported Media Type|	Check your call you may be missing a header or another command that is causing the file to be misinterpreted.|--|
|500|	Invalid authorization code	|Ensure that your authorization code is accurate and not expired|--|
|500|	An authorization code must be supplied|	Ensure that your authorization code is included in the call|--|
|500|	Internal Server Error|	Ensure that that your XML is valid and that any ORCID records you reference in the file are valid|--|
|500|	Redirect URI mismatch.	|Check that the redirect_uri in the request for the authorization code matches the redirect_uri used when exchanging the authorization code for an access token|--|
|500|	Invalid authorization code	|Check that the authorization code has not already been exchanged for an access token, authorization codes can only be used once|--|
|500|	Invalid scope: /webhook	|Your credentials are not authorized to create webhooks. Webhooks are available only to premium members, if you are a premium member contact support@orcid.org to correct this problem
|500|	org.hibernate.exception.DataException: could not execute statement	|Something that you are posting doesn't comply with field restrictions, check that fields don't exceed character limits, urls are properly formatted, etc.|--|
|***|Unable to find org.orcid.persistence.jpa.entities.ProfileEntity with id	﻿|Ensure that you have correct and consistent ORCID iDs throughout the XML, including in sub-elements, such as works source|--|
|***|Could not resolve host: Bearer; nodename nor servname provided, or not known|Check the syntax of your request|--|
|***|NonUniqueObjectException: a different object with the same identifier value was already associated with the session|The file you are posting has already been added to this record.|--|
|***|url: (3) [globbing] bad range in column 32|Check that you have filled in all the relevant information, this error came from a missing ORCID|--|
