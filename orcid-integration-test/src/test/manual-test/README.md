# Manual Tests

## Register/Verify

0. Find and Replace [DD][month][YYYY] in this document with the current day, written month, and four digit year for example 24feb2016
1. Visit https://qa.orcid.org/register
2. Create new account:
    * First name: ma_test
    * Last name: [DD][month][YYYY]
    * Email: ma_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Second email: 00_ma_test_[DD][month][YYYY]@mailinator.com
    * Password: test1234
    * Default privacy for new activities: Everyone
    * Subscribe to quarterly emails about new features
    * Accept terms and conditions
3. Attempt to edit the biography of the record- check you get a warning message to verify your email address
5. Visit https://qa.orcid.org/signout
6. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com
7. Open message from support@verify.orcid.org with subject [ORCID] Welcome to ORCID
8. Click (or copy/paste) email verification link
9. When redirected to https://qa.orcid.org/signin, sign in using ma_test credentials created in previous steps
10. Replace [orcid id] in this document with the 16 digit iD from the record
11. Visit https://qa.orcid.org/signout

## Reset password

12. Visit https://qa.orcid.org/signout
13. Click the Forgotten Your Password link
14. Enter ma_test_[DD][month][YYYY]@mailinator.com in the form and click Send Reset Link
15. Visit https://www.mailinator.com and check the inbox for ma_test_[DD][month][YYYY]@mailinator.com
16. Open message from reset@notify.orcid.org with subject [ORCID] About your password reset request
17. Click (or copy/paste) password reset link
18. Reset password with [DD][month][YYYY] (No need to fill challenge question)
19. You will be forward to the sign in page, sign in using the new password
22. Visit https://qa.orcid.org/signout

## Sign In
## Testing Institutional Login
23. Create a UnitedID account if you do not already have one at https://app.unitedid.org/signup/ and enable a way to get a security token by going to 'Manage security tokens' after signing up
24. Visit https://qa.orcid.org/signin and use the Institutional account option to sign in using "United ID" as the institution and the UnitedID account you just created. Complete steps to link it to the Individual account the account created in steps 1 and 2.
25. On the notification in the orange box at the top of the page to link the account to State University, click connect, you'll be taken to the OAuth page. Click 'Deny'  and return to the record.
26. Visit https://qa.orcid.org/signout
27. Visit https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground
28. Sign in using a Google account not linked to an existing ORCID record
29. Complete steps to link the Google account to the account created today
30. Check that after linking the account you are taken back to the authorize page not to my-orcid
31. Click Deny on the authorization- check that you are taken to the Google OAuth Playground with a Deny error
32. Visit https://qa.orcid.org/account and follow the steps to enable two factor authentication
33. Visit https://qa.orcid.org/signout
34. Sign in, check that you are asked for a 2FA code
35. Visit https://qa.orcid.org/account disable 2FA and revoke the Google and United ID access

## My-ORCID

36. Visit https://qa.orcid.org/my-orcid
37. Use the language selector to change the language to a different language- check that the page updates to that language
38. Use the language selector to set the page back to English
39. Add a published name: "Pub Name" (Published name can be edited using the pencil icon next to the record name)
40. Add an also know as name: "Other Name"
41. Add a country: "Afghanistan"
42. Add a keyword: "keyword"
43. Add a URL: name:"website" URL https://qa.orcid.org
44. Add a second email address: 01_ma_test_[DD][month][YYYY]@mailinator.com and change the visibility to public
45. Add a biography: "Bio!"
46. Add an education item: 'ORCID' (select from dropdown list) start date '2018'
47. Add a funding item: type 'grant', title 'ma_fund_test', funding agency 'NIH Clinical Center' (select from dropdown list)
48. Add a work: category: "publication', type: 'journal article', title 'ma_test_work', identifier type 'DOI', identifier value “0000” click through warning about the identifier validation
49. Set the work you just added to private
50. Visit public page (https://qa.orcid.org/[orcid id])
    * Verify information added in the steps above is visible, but not the private work or email addresses
51. Visit https://qa.orcid.org/signout

## Public API & Revoke Access check

52. Go to https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground&email=pub_ma_test_[DD][month][YYYY]@mailinator.com&given_names=ma_test&family_names=[DD][month][YYYY]

53. Check that the registration screen displays and first and last names and the email address are prepopulated

54. Complete the registration form & authorize the connection
    * Leave additional email blank
    * Password: [DD][month][YYYY]
    * Default privacy for new activities: Only me

55. Check you are prompted with a window asking if the record you created earlier is you. Click Continue to registration

56. Use curl to exchange the authorization code (the 6 digit code returned with the URI, you do not need to do anything on the Google Playground page). On a Mac you can open a terminal window and run the calls there, if using Windows you will need to install curl and replace single quotes with double quotes in steps 52, 53 and 55 or use an online tool that lets you execute curl commands such as https://onlinecurl.com/:

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=APP-6QJHHJ6OH7I9Z5XO&client_secret=[replace with client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

57. Use curl to read the record with the public access token:

	```
	curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [token]' -X GET 'https://pub.qa.orcid.org/v2.0/[public orcid id]/record' -L -i -k
	```

58. Visit https://qa.orcid.org/account and revoke the public API permission

59. Attempt the call in step 53 again- check that an error is returned

60. On https://qa.orcid.org/account add [orcid id] as a trusted individual


## Run the automated Independent Tests

61. Go to the CI server (https://ci.orcid.org/) and select ORCID-independent-tests-step2

62. Build the ORCID Independent Tests 2 with the following parameters
	* user_login: ma_test_[DD][month][YYYY]
 	* user_pass: [DD][month][YYYY]
 	* orcid_id: [orcid id]
 	* search_value: [DD][month][YYYY]

## Post ORCID Independent Tests

63. Visit https://qa.orcid.org/sign-in

64. Sign into the account created earlier
	* Email: ma_test_[DD][month][YYYY]@mailinator.com
	* Password: [DD][month][YYYY]

65. Check that the information you entered earlier is present and items have been added by the Manual Testing Client
	* 3 other names
	* 3 counties
	* 4 keywords
	* 4 websites
	* 3 external id
	* 4 education items
	* 4 funding items
	* 3 works (one being a group of 2)
	* 1 peer-review item (with two reviews)

66. Visit https://qa.orcid.org/[orcid id] Check that the same information displays
	* Check that the group of works with doi:0000 only displays the public version

67. Go to http://qa.orcid.org/inbox check for:
	* the request for access notification
	* notifications of updates to the record

68. Archive the first notification in the list

69. Use the switch user option to change to the record created during the OAuth process

70. Using IE 11 visit the follow pages and make sure everything loads
	* https://qa.orcid.org (check that blog feed loads as well as page contents)
	* https://qa.orcid.org/[orcid id]
	* https://qa.orcid.org/register
	* https://qa.orcid.org/sign-in
	* sign into [orcid id] account and check that it also looks as expected

* Finally help out by improving these instructions!      
