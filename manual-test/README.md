# Manual Tests

* These tests use the public email inbox service https://www.mailinator.com/. Sometimes emails sent by the ORCID Registry don't arrive into mailinator. If that happens, just repeat the step to trigger a new email. Please note you do not need a mailinator account -- just enter the email address you want to check at the top in the mailinator website.

* It is recommended that you use a private browser window to complete these tests (except the axe accessibility tests), or otherwise a browser you don't normally use, to ensure you can complete all the steps.

* If you don't know what the "QA password" used throughout these tests is, please ask a member of the tech team.

## Register/Verify

1. Copy this script into a text document, and then Find and Replace [DD][month][YYYY] with the current day, written month, and four digit year (for example, 24feb2016).
1. Visit https://qa.orcid.org and check that the cookies banner is displayed correctly in home page.
1. Visit https://qa.orcid.org/register
1. Create a new account:
    * First name: ma_test
    * Last name: [DD][month][YYYY]
    * Email: ma_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Second email: 00_ma_test_[DD][month][YYYY]@mailinator.com
    * Password: test1234
    * Subscribe to quarterly emails about new features
    * Default privacy for new activities: Everyone
    * accept the Terms of Use and complete the reCaptcha challenge
1. A modal window is displayed in my-orcid containing an email verification reminder; click the button to resend the verification email.
1. Click the button "Resend verification email" located inside the yellow banner above the biography section. The content of the banner will change to indicate a verification email has been sent.
1. Click the pencil icon in the biography section, then click the button "resend verification email" in the modal window that comes up.
1. Visit https://qa.orcid.org/signout
1. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com.
1. Verify there are four messages -- one with the subject "Welcome to ORCID - verify your email address" and three with the subject "Reminder to verify your primary email address".
1. Open the message from support@verify.orcid.org with subject "Welcome to ORCID - verify your email address" and click the email verification link.
1. When redirected to https://qa.orcid.org/signin, ensure there is a banner confirming the email address was verified successfully, and sign in using the credentials created at the start (ma_test_[DD][month][YYYY]@mailinator.com).
1. Replace [orcid id] in this document with the 16 digit iD from the record.
14. Visit https://qa.orcid.org/signout

## Reset password and ORCID iD recovery

1. Click the link "Forgot your password or ORCID iD?".
1. Enter MA_test_[DD][month][YYYY]@mailinator.com (uppercase is intentional) in the form and click "Recover account details".
1. Reload the page.
1. Click the button "ORCID iD".
1. Enter MA_test_[DD][month][YYYY]@mailinator.com (uppercase is intentional) in the form and click "Recover account details".
1. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com.
1. Verify there is a message from reset@notify.orcid.org with subject "[ORCID] Your ORCID iD".
1. Open message from reset@notify.orcid.org with subject "[ORCID] About your password reset request" and click the password reset link.
1. Reset password with [DD][month][YYYY].
1. You will be forward to the sign in page -- sign in with:
	* Email: MA_TEST_[DD][month][YYYY]@mailinator.com (uppercase is intentional)
	* Password: [DD][month][YYYY]
1. Visit https://qa.orcid.org/signout

## Institutional Login

1. Visit https://qa.orcid.org/signin and select the option "Access through your institution".
1. Select "SAMLtest IdP" from the list of organizations. You will then be taken to https://samltest.id/
1. Sign in using the SAMLtest account with username "sheldon" and password "bazinga", after which you'll be taken to a page where you can select which "Information to be Provided to Service".
1. Select the default option "ask me again if information to be provided to this service changes" and click the "accept" button.
1. Sign in with the ORCID credentials created at the start (ma_test_[DD][month][YYYY]@mailinator.com).
1. Visit https://qa.orcid.org/signout
1. Visit https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground
1. Click the button "Sign in with Google" and use a Google account that's not linked to an existing ORCID record.
1. Complete the steps to link the Google account to the ORCID account created at the start (ma_test_[DD][month][YYYY]@mailinator.com).
1. Check that after linking the accounts you are taken back to the authorize page, not to https://qa.orcid.org/my-orcid.
1. Click "Deny" on the authorization page -- check that you are taken to the Google OAuth Playground with a deny error (expect to see "?error=access_denied&error_description=User denied access" appended in the browser address bar).
1. Visit https://qa.orcid.org/account and follow the steps to enable two-factor authentication.
1. Visit https://qa.orcid.org/signout
1. Sign in and check that you are asked for a 2FA code.
1. Visit https://qa.orcid.org/account, disable 2FA, and remove the Google and SAMLtest entries from the "Alternate sign in accounts" section.
16. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com. Ensure there's an email with the subject line "[ORCID] Two-factor authentication disabled on your account".

## My-ORCID

1. Visit https://qa.orcid.org/my-orcid
1. Use the language selector to change the language to Spanish -- check that the page updates to that language.
1. Use the language selector to set the page back to English.
1. Add a published name: "Pub Name" (Published name can be edited using the pencil icon in the Names section).
1. Add a name in the Also known as section: "Other Name".
1. Add a country: "Afghanistan".
1. Add a keyword: "keyword".
1. Add an entry in the "Websites & Social Links" section with the name "website" and the URL "https://qa.orcid.org".
1. Add an email address: 01_ma_test_[DD][month][YYYY]@mailinator.com.
1. Change the visibility setting for ma_test_[DD][month][YYYY]@mailinator.com to public (select "everyone").
1. Add a biography: "Bio!".
1. Add an education item: 'ORCID' (select org name from dropdown list) start date '2018'.
1. Add a funding item: type 'grant', title 'ma_fund_test', funding agency 'NASA Exoplanet Science Institute' (select org name from dropdown list).
1. Add a work by DOI: enter DOI "10.1087/20120404" and save without making change on the Add manually screen.
1. Add a work manually: type 'journal article', title 'ma_test_work', identifier type 'DOI', identifier value '10.1087/20120404444444444'. Ignore the warning about the identifier and click the button "Add to list".
1. Set the work you just added to private (select "Only me").
1. Visit public page (https://qa.orcid.org/[orcid id]).
    * Verify information added in the steps above is visible, but not the private work or private email addresses.
	* Click on the public record print view, check that it displays properly.
1. Visit https://qa.orcid.org/signout

## Public API & Revoke Access check

1. Go to https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground&email=pub_ma_test_[DD][month][YYYY]@mailinator.com&given_names=ma_test&family_names=[DD][month][YYYY]

1. Ensure that you're taken to the registration screen, and that the fields first name, last name, and email address are pre-populated.

1. Complete the registration form as follows:
    * Leave additional email blank
    * Password: [DD][month][YYYY]
    * Default privacy for new activities: Only me
    * Do not subscribe to quarterly emails
    * Accept the Terms of use
    * Complete the reCaptcha challenge

1. Check you are prompted with a window "Could this be you?" asking if the record you created earlier is you. Click "Continue to registration".

1. You should now see the authorization screen. Grant the requested permissions by clicking the buttom "Grant access", and save the authorization code (the 6-digit code returned with the URI).

1. Use curl to exchange the authorization code for an access token. On a Mac you can open a terminal window and run the calls there, if using Windows you will need to install curl and replace single quotes with double quotes in steps 52, 53 and 55 or use an online tool that lets you execute curl commands such as https://onlinecurl.com/:

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=APP-6QJHHJ6OH7I9Z5XO&client_secret=[replace with client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

1. Use curl to read the record with the public access token:

	```
	curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [token]' -X GET 'https://pub.qa.orcid.org/v2.0/[public orcid id]/record' -L -i -k
	```
	
	* note: in the curl call above replace [public orcid id] with the ORCID iD you registered in step 57.

1. Visit https://qa.orcid.org/trusted-parties and revoke the public API permission by removing the client from the list of trusted organizations.

1. Attempt the call in step 60 again -- check that an error is returned (expect to see error 401 "invalid_token").

1. On https://qa.orcid.org/trusted-parties add [orcid id] as a trusted individual.

1. Visit https://qa.orcid.org/signout

## Test Self Service

**NOTE** Self-service is currently not working. Skip this section.

For this test you need to have a Consortium Lead account on QA and a Consortium Member account. For the purposes of testing you can use the accounts 0000-0002-3646-4021 for the consortium lead account and 0000-0002-0517-4718 for the consortium member account.

**Check Consortium Lead Functionality**

1. Go to https://qa.orcid.org/signin and sign in with:

		0000-0002-3646-4021
		Password: *QA password* 
		
1. Click on your name on the top-right corner, then click on the tab 'MEMBER TOOLS' and check the page loads.

1. Scroll to the bottom of the page to the Add a new consortium member section:

			Organization Name: [DD][month][YYYY]
			Website : http://www.[DD][month][YYYY].com
			**Contact Information**
			First Name : Self_service
			Last Name: [DD][month][YYYY]
			Email: [DD][month][YYYY]@mailinator.com

**Check the Salesforce staff email to check there is a notification**

1. Visit mailinator.com and enter:	

			sfqaselfservicetest@mailinator.com
			
1. Check that there is an email titled "Consortium member addition requested - [DD][month][YYYY]".

**Check that consortium member can add a contact**

1.  Visit qa.orcid.org/signout

		Sign in with
		0000-0002-0517-4718
		*QA password*
		
1.  Verify the language is set to Russian.

1. Change the language to English.

1.  Go to https://qa.orcid.org/self-service

1.  Add a contact in the Add Member Contacts email field (at the bottom of the page).

		Email: ma_test_[DD][month][YYYY]@mailinator.com
		
1. Change the language to Russian and visit https://qa.orcid.org/signout

## Run the automated Independent Tests

1. Go to https://github.com/ORCID/orcid-independent-tests/actions

1. In the Workflows navigation sidebar on the left, select "Step 2 - Dynamic tests".

1. Click the button "Run workflow" and build the test run with the following parameters:
	* Branch: master
	* Server: qa.orcid.org
	* ORCID: [orcid id]
 	* Username: ma_test_[DD][month][YYYY]
	* Password: [DD][month][YYYY]
	
1. While the above is running, select "Step 1 - Static tests" from the Workflows navigation sidebar.

1. Click the button "Run workflow" and build the test run with the default parameters.

1. Check both Step 2 and Step 1 test runs have completed successfully (the tests will take approx. 5 minutes to complete).

## ORCID Independent Tests

1. Visit https://qa.orcid.org/signin

1. Sign into the account created earlier:
	* ORCID: [orcid id]
	* Password: [DD][month][YYYY]

1. Check that these items have been added by the Manual Testing Client:
	* 3 other names
	* 3 countries
	* 3 keywords
	* 3 websites
	* 3 external id
	* 3 education items - open one to view details
	* 1 qualification
	* 3 funding items
	* 1 research-resource
	* 9 works (2 added via Member OBO) -- open one to view details
	* 2 peer-review items (one with two reviews and one with 1 review) - open one to view details

1. Visit https://qa.orcid.org/[orcid id] and check that the same information displays.

1. Attempt to merge two of the works, make sure they merge as expected.

1. Go to http://qa.orcid.org/inbox check for:
	* the permissions notification
	* notifications of changes made to the record
	* you might need to click on "show archived"

1. Archive the first notification in the list.
	
1. (**NOTE** - Skip this step, Self-service currently not working) Check there is a Member Tools tab and the page loads when you go to it.

1. Use the switch user option to change to the record created during the OAuth process.



1. Sign in to Browserstack.com and using IE 11, visit the following pages and make sure everything loads correctly:
	* https://qa.orcid.org (check the blog feed loads as well as page contents)
	* https://qa.orcid.org/[orcid id]
	* https://qa.orcid.org/register
	* https://qa.orcid.org/sign-in
	* sign into [orcid id] account and check that it also looks as expected

1. Using browserstack check the following pages on one Android and one Apple device to check load times:
	* https://qa.orcid.org (check that blog feed loads as well as page contents)
	* https://qa.orcid.org/[orcid id]
	* https://qa.orcid.org/register
	* https://qa.orcid.org/sign-in

## Accessibility Testing

1. Install Axe browser plugin from https://www.deque.com/axe/ if you don't have it already. 

1. Visit the following pages and check in the AXE console that their errors are less or the same as stated below. If they are more than stated below visit the [QA benchmarks](https://docs.google.com/document/d/1GBd1EqJB5oMYZNeENGvrUlY3NzhplqiD8mwFIjDCTLU/edit?usp=sharing) and compare your result to the results there to see what is causing the error.
1. Visit https://qa.orcid.org and check there are no violations.
1. Visit https://qa.orcid.org/register and check there are no violations (be sure to go through the 3 steps of the register form).
1. Visit https://qa.orcid.org/reset-password and check there are no violations.
1. Visit https://qa.orcid.org/signin and check check there are no violations.
1.	Stay on this page and sign in with:

	```0000-0002-7361-1027```
	```QA password```

1. Visit https://qa.orcid.org/myorcid Check the number of errors in AXE is less than  45.
1. Visit https://qa.orcid.org/signout
1. Upload your document to https://drive.google.com/drive/folders/1yG9ksNIGF9Iy7NU858cmuo7ReUbqSqcO 
