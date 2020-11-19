# Manual Tests

* These tests use the public email inbox service https://www.mailinator.com/. Sometimes emails sent by the ORCID Registry don't arrive into mailinator. If that happens, just repeat the step to trigger a new email. Please note you do not need a mailinator account -- just enter the email address you want to check at the top in the mailinator website.

* It is recommended that you use a private browser window to complete these tests (except the axe accessibility tests), or otherwise a browser you don't normally use, to ensure you can complete all the steps.

* If you don't know what the "QA password" used throughout these tests is, please ask a member of the tech team.

## Register/Verify

1. Copy this script into a text document, and then Find and Replace [DD][month][YYYY] with the current day, written month, and four digit year (for example, 24feb2016).
2. Visit https://qa.orcid.org and check that the cookies banner is displayed correctly in home page.
3. Visit https://qa.orcid.org/register
4. Create a new account:
    * First name: ma_test
    * Last name: [DD][month][YYYY]
    * Email: ma_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Second email: 00_ma_test_[DD][month][YYYY]@mailinator.com
    * Password: test1234
    * Default privacy for new activities: Everyone
    * Subscribe to quarterly emails about new features
    * Accept terms and conditions
5. Click the button "Resend verification email" (located inside the yellow banner above the biography section).
6. Attempt to edit the biography of the record- click the link to resend the verify email in the warning that comes up.
7. Visit https://qa.orcid.org/signout
8. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com.
9. Verify there are three messages -- one with the subject "Welcome to ORCID - verify your email address" and two with the subject "Reminder to verify your primary email address".
10. Open the message from support@verify.orcid.org with subject "Welcome to ORCID - verify your email address" and click the email verification link.
11. When redirected to https://qa.orcid.org/signin, ensure there is a banner confirming the email address was verified successfully, and sign in using the credentials created at the start (ma_test_[DD][month][YYYY]@mailinator.com).
12. Replace [orcid id] in this document with the 16 digit iD from the record.
13. Visit https://qa.orcid.org/signout

## Reset password and ORCID iD recovery

1. Click the link "Forgot your password or ORCID iD?".
2. Enter MA_test_[DD][month][YYYY]@mailinator.com in the form and click Recover account details (uppercase is intentional).
3. Reload the page.
4. Click on the ORCID iD button.
5. Enter MA_test_[DD][month][YYYY]@mailinator.com in the form and click Recover account details (uppercase is intentional).
6. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com.
7. Verify there is a message from reset@notify.orcid.org with subject [ORCID] Your ORCID iD.
8. Open message from reset@notify.orcid.org with subject [ORCID] About your password reset request and click the password reset link.
9. Reset password with [DD][month][YYYY].
10. You will be forward to the sign in page -- sign in with:
	* Email: MA_TEST_[DD][month][YYYY]@mailinator.com (uppercase is intentional)
	* Password: [DD][month][YYYY]
11. Visit https://qa.orcid.org/signout

## Institutional Login

1. Visit https://qa.orcid.org/signin and select the option "Institutional account".
2. Select "SALMtest IdP" from the list of organizations. You will then be taken to https://samltest.id/
3. Sign in using the test account with username "sheldon" and password "bazinga", after which you'll be taken to a page where you can select which "Information to be Provided to Service".
4. Select the default option "ask me again if information to be provided to this service changes" and click the "accept" button.
5. Sign in with the ORCID credentials created at the start (ma_test_[DD][month][YYYY]@mailinator.com).
6. Visit https://qa.orcid.org/signout
7. Visit https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground.
8. Click the button "Sign in with Google" and use a Google account that's not linked to an existing ORCID record.
9. Complete the steps to link the Google account to the ORCID account created at the start (ma_test_[DD][month][YYYY]@mailinator.com).
10. Check that after linking the accounts you are taken back to the authorize page, not to https://qa.orcid.org/my-orcid.
11. Click "Deny" on the authorization page -- check that you are taken to the Google OAuth Playground with a deny error (expect to see "?error=access_denied&error_description=User denied access" appended in the browser address bar).
12. Visit https://qa.orcid.org/account and follow the steps to enable two factor authentication.
13. Visit https://qa.orcid.org/signout
14. Sign in, check that you are asked for a 2FA code.
15. Visit https://qa.orcid.org/account, disable 2FA, and remove the Google and SAMLtest entries from the "alternate signin accounts" section.
16. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com. Ensure there's an email with the subject line "[ORCID] Two-factor authentication disabled on your account"

## My-ORCID

1. Visit https://qa.orcid.org/my-orcid
2. Use the language selector to change the language to Spanish -- check that the page updates to that language.
3. Use the language selector to set the page back to English.
4. Add a published name: "Pub Name" (Published name can be edited using the pencil icon next to the record name).
5. Add an also known as name: "Other Name".
6. Add a country: "Afghanistan".
7. Add a keyword: "keyword".
8. Add an entry in the "Websites & Social Links" section with the name "website" and the URL "https://qa.orcid.org".
9. Add an email address: 01_ma_test_[DD][month][YYYY]@mailinator.com.
10. Change the visibility setting for ma_test_[DD][month][YYYY]@mailinator.com to public (select "everyone").
11. Add a biography: "Bio!".
12. Add an education item: 'ORCID' (select from dropdown list) start date '2018'.
13. Add a funding item: type 'grant', title 'ma_fund_test', funding agency 'NASA Exoplanet Science Institute' (select from dropdown list).
14. Add a work by DOI: enter DOI "10.1087/20120404" and save without making change on the add manually screen.
15. Add a work manually: category: "publication', type: 'journal article', title 'ma_test_work', identifier type 'DOI', identifier value '1234'. Ignore the warning about the identifier and click the button "add to list".
16. Set the work you just added to private (select "only me").
17. Visit public page (https://qa.orcid.org/[orcid id]).
    * Verify information added in the steps above is visible, but not the private work or email addresses.
	* Click on the public record print view, check that it displays properly.
18. Visit https://qa.orcid.org/signout

## Public API & Revoke Access check

1. Go to https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground&email=pub_ma_test_[DD][month][YYYY]@mailinator.com&given_names=ma_test&family_names=[DD][month][YYYY]

2. Ensure that you're taken to the registration screen, and that the fields first name, last name, and email address are prepopulated.

3. Complete the registration form as follows:
    * Leave additional email blank
    * Password: [DD][month][YYYY]
    * Default privacy for new activities: Only me
    * Do not subscribe to quarterly emails
    * Accept the terms & conditions
    * Click on the recaptcha

4. Check you are prompted with a window asking if the record you created earlier is you. Click Continue to registration.

5. You should now see the authorization screen. Grant the requested permissions by clicking on the authorize button, and save the authorization code (the 6-digit code returned with the URI).

6. Use curl to exchange the authorization code for an access token. On a Mac you can open a terminal window and run the calls there, if using Windows you will need to install curl and replace single quotes with double quotes in steps 52, 53 and 55 or use an online tool that lets you execute curl commands such as https://onlinecurl.com/:

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=APP-6QJHHJ6OH7I9Z5XO&client_secret=[replace with client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

7. Use curl to read the record with the public access token:

	```
	curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [token]' -X GET 'https://pub.qa.orcid.org/v2.0/[public orcid id]/record' -L -i -k
	```
	
	* note: replace [public orcid id] with the ORCID iD you registered in step 57.

8. Visit https://qa.orcid.org/account and revoke the public API permission by removing the client from the list of trusted organizations.

9. Attempt the call in step 60 again- check that an error is returned (expect to see the error 401 "invalid_token").

10. On https://qa.orcid.org/account add [orcid id] as a trusted individual.

11. Visit https://qa.orcid.org/signout

## Test Self Service

For this test you need to have a Consortium Lead account on QA and a Consortium Member account. For the purposes of testing you can use the accounts 0000-0002-3646-4021 for the consortium lead account and 0000-0002-0517-4718 for the consortium member account.

**Check Consortium Lead Functionality**

1. Go to https://qa.orcid.org/signin and sign in with:

		0000-0002-3646-4021
		Password: *QA password* 
		
2. Click on your name on the top-right corner, then click on the tab 'MEMBER TOOLS' and check the page loads.

3. Scroll to the bottom of the page to the Add a new consortium member section:

			Organization Name: [DD][month][YYYY]
			Website : http://www.[DD][month][YYYY].com
			**Contact Information**
			First Name : Self_service
			Last Name: [DD][month][YYYY]
			Email: [DD][month][YYYY]@mailinator.com

**Check the Salesforce staff email to check there is a notification**

4. Visit mailinator.com and enter:	

			sfqaselfservicetest@mailinator.com
			
5. Check that there is an email titled "Consortium member addition requested - [DD][month][YYYY]".

**Check that consortium member can add a contact**

6.  Visit qa.orcid.org/signout

		Sign in with
		0000-0002-0517-4718
		*QA password*
		
7.  Verify the language is set to Russian.

8. Change the language to English.

9.  Go to https://qa.orcid.org/self-service

10.  Add a contact in the Add Member Contacts email field (at the bottom of the page).

		Email: ma_test_[DD][month][YYYY]@mailinator.com
		
11. Change the language to Russian and visit https://qa.orcid.org/signout

## Run the automated Independent Tests

1. Go to the CI server (https://ci.orcid.org/) and select ORCID-independent-tests-step2.

2. Build the ORCID Independent Tests 2 with the following parameters:
	* user_login: ma_test_[DD][month][YYYY]
 	* user_pass: [DD][month][YYYY]
 	* orcid_id: [orcid id]
 	* search_value: [DD][month][YYYY]
	
3. Go back to CI Server Dashboard (https://ci.orcid.org/) and select ORCID-independent-tests-step1.

4. Build the Step 1 tests.

## Post ORCID Independent Tests

1. Visit https://qa.orcid.org/signin

2. Sign into the account created earlier:
	* ORCID: [orcid id]
	* Password: [DD][month][YYYY]

3. Check that the information you entered earlier is present and items have been added by the Manual Testing Client:
	* 6 other names
	* 6 countries
	* 6 keywords
	* 6 websites
	* 5 external id
	* 6 education items - open one to view details
	* 3 qualifications
	* 6 funding items
	* 3 research-resource
	* 16 works (8 bulk work items, 2 added via OBO, one work being a group of 2 and the rest being different api versions) - open one to view details
	* 2 peer-review items (one with two reviews and one with three reviews) - open one to view details

4. Visit https://qa.orcid.org/[orcid id] Check that the same information displays.
	* Check that the group of works with doi:1234 only displays the public version.

5. Attempt to merge two of the works, make sure they merge ok.

6. Go to http://qa.orcid.org/inbox check for:
	* the request for access notification
	* notifications of updates to the record
	* you may need to click on "show archived"

7. Archive the first notification in the list.
	
8. Check there is a Member Tools tab and the page loads when you go to it.

9. Use the switch user option to change to the record created during the OAuth process.



10. Sign in to Browserstack.com and using IE 11 visit the follow pages and make sure everything loads:
	* https://qa.orcid.org (check that blog feed loads as well as page contents)
	* https://qa.orcid.org/[orcid id]
	* https://qa.orcid.org/register
	* https://qa.orcid.org/sign-in
	* sign into [orcid id] account and check that it also looks as expected

11. Using browserstack check the following pages on one Android and one Apple device to check load times:
	* https://qa.orcid.org (check that blog feed loads as well as page contents)
	* https://qa.orcid.org/[orcid id]
	* https://qa.orcid.org/register
	* https://qa.orcid.org/sign-in

## Accessibility Testing

1. Install Axe browser plugin from https://www.deque.com/axe/ if you don't have it already. 

2. Visit the following pages and check in the AXE console that their errors are less or the same as stated below. If they are more than stated below visit the [QA benchmarks](https://docs.google.com/document/d/1jSzuH9k5KeX-OqGdltj9dJx5MorczSWhFbcbYgVWWrc/edit?usp=sharing) and compare your result to the results there to see what is causing the error.
3. Visit https://qa.orcid.org and check errors are same or less than 20 errors.
4. Visit https://qa.orcid.org/register and check errors are same or less than 19.
5. Visit https://qa.orcid.org/reset-password and check errors are same or less than 5.
6. Visit https://qa.orcid.org/signin and check errors are same or less than 15.
7.	Stay on this page and sign in with:

	```0000-0002-7361-1027```
	```QA password```

8. Visit https://qa.orcid.org/myorcid Check the number of errors in AXE is less than  60.
9. Visit https://qa.orcid.org/signout
10. Upload your document to https://drive.google.com/drive/folders/1yG9ksNIGF9Iy7NU858cmuo7ReUbqSqcO 

* Finally help out by improving these instructions!  
