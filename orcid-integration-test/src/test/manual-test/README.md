# Manual Test

Run ORCID-Independent-Tests-1 first, to check for basic API errors.

## Register/Verify

0. Find and Replace [DD][month][YYYY] in this document with the current day, written month, and four digit year for example 24feb2016
1. Visit https://qa.orcid.org/register
2. Create new account:
    * First name: ma_test
    * Last name: [DD][month][YYYY]
    * Email: ma_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Password: test1234
    * Default privacy for new activities: Public
    * Email frequency: Weekly summary
3. Attempt to edit the biography of the record- check you get a warning message to verify your email address
5. Visit https://qa.orcid.org/signout
6. Visit https://www.mailinator.com/inbox2.jsp?public_to=ma_test_[DD][month][YYYY]#/#public_maildirdiv
7. Open message from support@verify.orcid.org with subject [ORCID] Thanks for creating an ORCID iD 
8. Click (or copy/paste) email verification link
9. When redirected to https://qa.orcid.org/signin, sign in using ma_test credentials created in previous steps
10. Replace [orcid id] in this document with the 16 digit iD from the record
11. Visit https://qa.orcid.org/signout

## Reset password

12. Visit https://qa.orcid.org/oauth/authorize?client_id=APP-AJPEHIAZIRSSY5UO&response_type=code&scope=/read-limited&redirect_uri=https://developers.google.com/oauthplayground
13. Click the Forgotten Your Password link
14. Enter ma_test_[DD][month][YYYY]@mailinator.com in the form and click Send Reset Link
15. Visit https://www.mailinator.com/inbox2.jsp?public_to=ma_test_[DD][month][YYYY]#/#public_maildirdiv
16. Open message from reset@notify.orcid.org with subject [ORCID] About your password reset request 
17. Click (or copy/paste) password reset link
18. Reset password with [DD][month][YYYY]
19. You will be forward to the sign in page, sign in using the new password
20. Check that you are returned to the Authorization Page
21. Grant authorization
22. Visit https://qa.orcid.org/signout

## Sign In

23. Create a UnitedID account if you do not already have one at https://app.unitedid.org/signup/ and enable a way to get a security token by going to 'Manage security tokens' after signing up
24. Visit https://qa.orcid.org/signin and sign in using a UnitedID account and complete steps to link it to the account created today.
25. Check that the notification to link the account to State University displays on the record
26. Visit https://qa.orcid.org/signout
27. Visit https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground
28. Sign in using a Google account not linked to an existing ORCID record
29. Complete steps to link the Google account to the account created today
30. Check that after linking the account you are taken back to the authorize page not to my-orcid
31. Click Deny on the authorization- check that you are taken to the Google OAuth Playground with a Deny error
32. Visit https://qa.orcid.org/account and revoke Google and UnitedID account access

## My-ORCID

33. Visit https://qa.orcid.org/my-orcid
34. Use the language selector to change the language to a different language- check that the page updates to that language
35. Use the language selector to set the page back to English
36. Add a published name: Pub Name (Published name can be edited using the pencil icon next to the record name)
37. Add an also know as name: Other Name
38. Add a country: Afghanistan
39. Add a keyword: keyword
40. Add a URL: website/https://qa.orcid.org
41. Add a biography: Bio!
42. Add an education item: 'ORCID' (select from dropdown list)
43. Add a funding item: type 'grant', title 'ma_fund_test', funding agency 'Wellcome Trust' (select from dropdown list)
44. Add a work: category: "publication', type: 'journal article', title 'ma_test_work', identifier type 'DOI', identifier value “9999”
45. Add a second email address: 01_ma_test_[DD][month][YYYY]@mailinator.com and change the visibility to public
46. Visit public page (https://qa.orcid.org/[orcid-id])
    * Verify information added in the steps above is visible
    * Verify email address ma_test_[DD][month][YYYY]@mailinator.com is not visible
47. Visit https://qa.orcid.org/signout

## Public API & Revoke Access check

48. Go to https://qa.orcid.org/oauth/authorize?client_id=APP-6QJHHJ6OH7I9Z5XO&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground&email=pub_ma_test_[DD][month][YYYY]@mailinator.com&given_names=ma_pub_test&family_names=[DD][month][YYYY]

49. Check that the registration screen displays and first and last names and the email address are prepopulated

50. Complete the registration form
    * Password: [DD][month][YYYY]
    * Default privacy for new activities: Private
    * Email frequency: Never
    
51. Authorize the connection

52. Exchange the authorization code (the 6 digit code returned with the URI, you do not need to do anything on the Google Playground page): 

    ```
    curl -i -L -H 'Accept: application/json' --data 'client_id=APP-6QJHHJ6OH7I9Z5XO&client_secret=[Client Secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k
    ```

53. Read the record with the public access token:

	```
	curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [token]' -X GET 'https://pub.qa.orcid.org/v2.0/[public orcid id]/record' -L -i -k
	``` 

54. Visit https://qa.orcid.org/account and revoke the public API permission

55. Attempt the call in step 53 again- check that an error is returned

## Post ORCID Independent Tests

56. Run the ORCID Independent Tests 2
	* user_login: ma_test_[DD][month][YYYY]	
 	* user_pass: [DD][month][YYYY]
 	* orcid_id: [orcid id]
 	* search_value: [DD][month][YYYY]

57. Open Internet Explorer (version 10+) or Edge 

58. Visit https://qa.orcid.org/register and check the page loads correctly

59. Click the link to go to Sign-in, check that the sign-in page also loads correctly

60. Sign into the account created earlier
	* Email: ma_test_[DD][month][YYYY]@mailinator.com
	* Password: [DD][month][YYYY]

61. Check that the information you entered earlier is present and items have been added by the Manual Testing Client
	* 1 other name
	* 1 county
	* 2 keywords
	* 2 websites
	* 1 external id
	* 2 education items
	* 2 funding items
	* 2 works
	
62. Visit https://qa.orcid.org/[orcid id] Check that the same information displays

63. Go to http://qa.orcid.org/inbox check notifications from the updates to the record have posted and the request for access notification has posted

64. Archive the first notification in the list

* Finally help out by improving these instructions!      
   
