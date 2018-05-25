# ORCID Public API Quick Start

A guide to help you quickly get over any Public API foibles


## Part 1 - Getting the public record.
Part 1 will walk you through the steps needed to pull a public record for an ORCID.
Assumptions -  Familiar with using a bash shell like 
[git bash](http://msysgit.github.io/index.html) and 
[curl](http://curl.haxx.se/docs/httpscripting.html).

 **Fun fact**: even though the API says profile, we like to think
of it as a record.


### Start with an ORCID iD

http://orcid.org/0000-0002-0036-9460

An ORCID iD is a full URL. However the numeric portion broken out and we refer to it as
the **ORCID iD path**.

        <orcid-identifier>
            <uri>http://orcid.org/0000-0002-0036-9460</uri>
            <path>0000-0002-0036-9460</path>
            <host>orcid.org</host>
        </orcid-identifier> 


### Make it secure
Security is fun! And it pays off later when you start passing around tokens.

Turn "**http:**" into "**https:**":   
https://orcid.org/0000-0002-0036-9460


### Next change it to point to Public API

Add "**pub**" subdomian:  
https://pub.orcid.org/0000-0002-0036-9460


### Do a request while specifying the Accept header

Make a request with **application/xml** or **application/json**

     curl  -H "Accept: application/xml" \
        https://pub.orcid.org/0000-0002-0036-9460 


###  But activities are empty? Add **/orcid-profile**


By default only biographic information is supplied. To see the complete
record add **/orcid-profile** to the path

     curl  -H "Accept: application/xml" \
        https://pub.orcid.org/0000-0002-0036-9460/orcid-profile 


### But I don't see X (for instance, funding) section? Use a version number.

New functionality is often only in API release 
candidates. To access release candidates specify a version number before the numeric ORCID id path.

pub.orcid.org/**v1.2_rc4**/0000-0002-0036-9460/orcid-profile

     curl  -H "Accept: application/xml" \
        https://pub.orcid.org/v1.2_rc4/0000-0002-0036-9460/orcid-profile 


### Part 1 - Summary   

* Start with a ORCID iD

* Make it secure

* Next change to point to Public API

* Specify the Accept header

* But activities are empty? Add /orcid-profile

* But I don't see X (for instance, funding) section? Use a version number.

* Doing all of the above you end up with

        curl  -H "Accept: application/xml" \
           https://pub.orcid.org/v1.2_rc4/0000-0002-0036-9460/orcid-profile 


## Part 2 - Authenticate an ORCID iD 
To get a researcher's authenticated ORCID iDs 
We are going to use 
[OAuth2](https://aaronparecki.com/articles/2012/07/29/1/oauth2-simplified) 
to retrieve a users ORCID iD. This can be used for auto filling a form or 
[SSO](http://en.wikipedia.org/wiki/Single_sign-on).  


### Signup for Public Client Beta Test
Goto [https://orcid.org/content/beta-tester-request](https://orcid.org/content/beta-tester-request)
You will be provided instructions.


### Enable developer tool

Log into your ORCID account. Follow the instructions provided by beta signup. Under 
**Account Settings** click "**Enable Developers Tools**"

You'll be taken to the newly enabled "**developer tools**" page.


### Register your client

Click "**Register Now**" Fill out your client details. Click Save.


### Send user to Authorize URL
The Authorize URL is where user (or also known as Resource Owner) are sent to authorize 
access to a resource. You can find the authorize URL under the client details.


### Handle User redirect to specified client endpoint

After clicking Authorize the user is redirected to the specified endpoint with a Auth 
Code as one of the url parameters.

Sample:
  
    https://testapp.com/prod_test?code=IE326S  


### Exchange Auth Code for for an access token.
Using the auth code, your client details id and client secret exchange the access code for
a authorization token:

Sample curl:  

     curl -i -L -k -H 'Accept: application/json' \
     --data 'client_id=0000-0002-0036-9460' \
     --data 'client_secret=14d7b9e7-cfa3-474a-XXXX-XXXXXXXXXXX' \
     --data 'grant_type=authorization_code' \
     --data 'redirect_uri=https://rcpeters.com/prod_test' \
     --data 'code=XIGu9O' \
     https://pub.orcid.org/oauth/token
     

### Parse the response for the ORCID iD
Parse the response for the ORCID iD path.

Sample response:

     {
        "access_token":"76d87bb9-98b5-497e-92f2-a88100340cd7",
        "token_type":"bearer","refresh_token":"0e8bf783-ba2d-4ceb-ba2e-3bd6b3e00ccb",
        "expires_in":631138518,
        "scope":"/authenticate",
        "orcid":"0000-0002-0036-9460"
    }

Note: per OAuth2 spec json is always returned.


### Part 2 Summary

* Signup for Public Client Beta Test.

* Enable developer tool.

* Register your client.

* Send user to Authorize url.

* Handle User redirect to specified client endpoint.

* Exchange Auth Code for for an access token.

* Parse the response for the ORCID iD.

