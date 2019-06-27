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
* Wrong or misspelled endpoint




 For additional help please contact https://orcid.org/help/contact-us.

 ## List of Error codes and solutions

|Error Code |	Message	|Possible Solution|Example|
|-----------|---------|-----------------|-------|
|301	|Moved Permanently|	This ORCID iD has been deprecated into another, see the location returned for the updated ORCID iD if you are not automatically forwarded| **Example of location returned in error message:** `<developer-message>301 Moved Permanently: This account is deprecated. Please refer to account: https://qa.orcid.org/0000-0000-0000-0000. ORCID https://qa.orcid.org/0000-1111-0000-0000</developer-message>`
|302|No error message|Check that you are making a call to the api url not the web interface. The URL should start http://api.sandbox.orcid.org|**Incorrect call notice how the URL has no 'api'**  curl -i -H "Accept: application/vnd.orcid+xml" -H 'Authorization: Bearer ************************' 'https:/orcid.org/v3.0/0000-0002-4575-651X/works'|
|400| The client application sent a bad request to ORCID. Full validation error: argument type mismatch|An example of this error occurring is when a post is made for bulk works using the work endpoint.  ... -X POST 'https://api.sandbox.orcid.org/v3.0_rc1/0000-0002-4575-651X/work' instead of -X POST 'https://api.sandbox.orcid.org/v3.0_rc1/0000-0002-4575-651X/works'
|400|	Premature end of file	|Check the URL to which you are posting, the formatting of your XML and your file path to the XML as you can get this error for all of these issues|--|
|400 |Bad Request: There is an issue with your data or the API endpoint. with org.xml.sax.SAXParseException; lineNumber: foo; columnNumber: bar; Content is not allowed in prolog.] (Content is not allowed in prolog| Check your file path. This error message can actually mean that the api can't find your file. Have you missed the '@' or added a rogue space perhaps? Also check that there is valid XML in the request body(missing fields or blank file)|
|400|The client application sent a bad request to ORCID. Full validation error: argument type mismatch|This can be because of a scope typo. Check whether you are using singular or plural (education or educations for example)|--|
|400 |Bad Request: The put code in the URL was [number] whereas the one in the body was null.| Check that your XML has a put code and that it is correct. |Your XML should look like the following in the second line of your XML `<?xml version="1.0" encoding="UTF-8"?><external-identifier:external-identifier put-code="4910"``|
|401|	Bad client credentials|	Ensure that your client secret is correct|--|
|401|	Invalid access token|1.	Ensure that the access token used for the call is complete, matched to the ORCID iD and scope of the call, and is not expired, or that the user has not revoked access 2. Check that you are calling the correct API for the client |Example of incorrect API  (api.orcid.org not api.sandbox.orcid.org for example.)|
|403|	Access Denied	|Check the URL of the request|--|
|403|Forbidden: You are not the source of the work, so you are not allowed to update it|This is because you are trying to modify something that your API client did not put there in the first place ( if the user added an address for example you wouldn't be able to update it)|--|
|403|If you get this  an error with HTML tags and 'Oops an error happened!'. This is because you are using the Web URL not the api url. |Make sure your URL has 'api' at the start|
|406|	The resource identified by this request is only capable of generating responses with characteristics not acceptable according to the request "accept" headers.|	Check the header you are using. It should be either 'Accept: application/xml' or 'Accept: application/json' It also must match the content type header if provided.|--|
|409|	Conflict: The ORCID record is locked and cannot be edited. ORCID https://orcid.org/xxxx-xxxx-xxxx-xxxx|	This record was flagged as violating ORCID's Terms of Use and has been hidden from public view.|--|
|409|Conflict: You have already added this activity (matched by external identifiers.)|This one is quite explicit, the activity you are trying to add is already on the record|--|
|413|	Request Entity Too Large|Bulk work posts are limited to 100 items check your XML does not have too many items|--|
|415|	Unsupported Media Type|	Check your call you may be missing a header or another command that is causing the file to be misinterpreted.|--|
|500|	Invalid authorization code	|Ensure that your authorization code is accurate and not expired|--|
|500|	An authorization code must be supplied|	Ensure that your authorization code is included in the call|--|
|500|	Internal Server Error|	Ensure that that your XML is valid and that any ORCID records you reference in the file are valid|--|
|500|	Redirect URI mismatch.	|Check that the redirect_uri in the request for the authorization code matches the redirect_uri used when exchanging the authorization code for an access token|--|
|500|	Invalid authorization code	|Check that the authorization code has not already been exchanged for an access token, authorization codes can only be used once|--|
|500|	Invalid scope: /webhook	|Your credentials are not authorized to create webhooks. Webhooks are available only to premium members, if you are a premium member contact https://orcid.org/help/contact-us to correct this problem
|500|	org.hibernate.exception.DataException: could not execute statement	|Something that you are posting doesn't comply with field restrictions, check that fields don't exceed character limits, urls are properly formatted, etc.|--|
|***|url: (3) [globbing] bad range in column 32|Check that you have filled in all the relevant information, this error came from a missing ORCID|--|
|***|API 1.2 is disabled, please upgrade to the 2.0 API https://members.orcid.org/api/news/xsd-20-update</error-desc>|You are using API version 1 please use v3.0|--|
|***|{"error":"invalid_token","error_description":"Invalid access token: xxxxxx-xxxx--xxxx-xxxx-x-xxxx-xxxx"}|Your access token is wrong or misstyped|--|



**A note about 400 Bad requests**

You can get the is error for many reasons. It is usually accompanied by a helpful error message that may look like the following:

```[org.xml.sax.SAXParseException; lineNumber: 34; columnNumber: 81; cvc-enumeration-valid: Value 'version-of' is not facet-valid with respect to enumeration '[self, part-of]'. It must be a value from the enumeration.] (cvc-enumeration-valid: Value 'version-of' is not facet-valid with respect to enumeration '[self, part-of]'. It must be a value from the enumeration.)</developer-message>```


This message points to where in the XML body that the error lies. Simply going to that line and correcting the error to a valid value will fix the error. [Referring to our examples ](https://github.com/ORCID/ORCID-Source/tree/master/orcid-model/src/main/resources/record_3.0/samples) may help you here. Be aware though, that the error might be because the XML is for a different API. Because of changes between API 2.0 and API 3.0 the same work XML may not work for both API versions.
