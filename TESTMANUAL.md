# Manual Testing
Steps to be completed before each release

##User Interface

###Register/Verify/Signin
1. Visit https://qa.orcid.org/register
2. Create new account:
    * First name: ma_test
    * Last name: [DD][month][YYYY] (ex: 24feb2016)
    * Email: ma_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Password: generate random password using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
    * Default privacy for new activities: Public
    * Email frequency: Weekly summary
3. Visit https://qa.orcid.org/signout
4. Visit http://mailinator.com and check ma_test_[DD][month][YYYY]@mailinator.com
5. Open message from support@verify.orcid.org with subject [ORCID] Thanks for creating an ORCID iD 
6. Click (or copy/paste) email verification link
7. When redirected to https://qa.orcid.org/signin, sign in using ma_test credentials created in previous steps

###Account settings
1. Visit https://qa.orcid.org/account
2. Ensure email ma_test_[DD][month][YYYY]@mailinator.com is marked as private
3. Add new email ma_test_[DD][month][YYYY]_01@mailinator.com and mark as public
4. Visit public page (http://qa.orcid.org/[XXXX-XXXX-XXXX-XXXX])
      * Verify ma_test_[DD][month][YYYY]@mailinator.com is not visible
      * Verify ma_test_[DD][month][YYYY]_01@mailinator.com is visible
5. Visit https://qa.orcid.org/signout

###Reset password
1. Visit https://qa.orcid.org/reset-password and reset the password for ma_test_[DD][month][YYYY]@mailinator.com
2. Visit http://mailinator.com and check ma_test_[DD][month][YYYY]@mailinator.com
3. Open message from reset@notify.orcid.org with subject [ORCID] About your password reset request 
4. Click (or copy/paste) password reset link
5. Reset password with random value generated using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
6. Visit https://qa.orcid.org/signin and sign in using the new password created in the previous steps

###Create Public API Client
1. Visit https://qa.orcid.org/developer-tools
2. Create a new public client app:
    * Name: ma_test_03mar2016
    * Website: http://qa.orcid.org
    * Description: ma_test_03mar2016
    * Redirect URI: https://developers.google.com/oauthplayground

Use this public client ID/secret for the Public API tests below.

###Manage Members
1. Visit https://qa.orcid.org/manage-members and sign in with admin account:
    * adminemail@domain.org
    * XXXXXXXX
2. Create a new client group:
    * Member name: ma_test_group_[DD][month][YYYY] (ex: ma_test_group_24feb2016)
    * Client group email: ma_test_group_[DD][month][YYYY]@mailinator.com (ex: ma_test_group_24feb2016@mailinator.com)
    * Salesforce ID: XXXXXXXXXXXXXXX (15 characters)
    * Member type: Premium institution
3. Switch accounts to the group that you just created
4. Add a new member API client to the group
    * Display name: ma_test_client_[DD][month][YYYY] (ex: ma_test_client24feb2016)
    * Website: http://qa.orcid.org/
    * Description: ma_test_client
    * Redirect URI: https://developers.google.com/oauthplayground

Use this member client ID/secret for the Member API tests below.

##API
###Public Authenticate
1. Go to https://qa.orcid.org/oauth/authorize?client_id=[public client id]&response_type=code&scope=/authenticate&redirect_uri=https://developers.google.com/oauthplayground
2. Register for a new account and grant authorization
    * First name: ma_public_test
    * Last name: [DD][month][YYYY] (ex: 24feb2016)
    * Email: ma_public_test_[DD][month][YYYY]@mailinator.com (ex: ma_test_24feb2016@mailinator.com)
    * Password: generate random password using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
    * Default privacy for new activities: Private
    * Email frequency: Never
3. Exchange the authorization code with curl -i -L -H 'Accept: application/json' --data 'client_id=[public client id]&client_secret=[public client secret]&grant_type=authorization_code&code=[code]&redirect_uri=https://developers.google.com/oauthplayground' 'https://qa.orcid.org/oauth/token' -k

### Public Read/Search 1.2
1. Generate a read-public token with curl -i -L -H 'Accept: application/json' -d 'client_id=[public client id]' -d 'client_secret=[public client secret]' -d 'scope=/read-public' -d 'grant_type=client_credentials' 'http://pub.qa.orcid.org/oauth/token'
2. Search for the new record you created with curl -H 'Content-Type: application/orcid+xml' -H 'Authorization: Bearer [public token]' 'Accept: application/xml' 'https://api.qa.orcid.org/v1.2/search/orcid-bio/?q=family-name:[DD][month][YYYY]'
3. Read the record with: curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://api.qa.orcid.org/v1.2/[ma id 2]/orcid-profile' -L -i
4. Read the record without a version: curl -H 'Content-Type: application/xml' -H 'Authorization: Bearer [public token]' -X GET 'http://api.qa.orcid.org/[ma id 2]/orcid-profile' -L -i
5. Read the record without an access token: curl -H 'Content-Type: application/xml' 'http://api.qa.orcid.org/v1.2/[ma id 2]/orcid-profile' -L -i
###Member


* Finally help out by improving these instructions!    
   
