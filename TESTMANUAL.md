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
      * Verify ma_test_email[highest n+1]@mailinoator.com is not visible
      * Verify ma_test_email[highest n]@mailinoator.org is not visible
5. Visit https://qa.orcid.org/signout

###Reset password
1. Visit https://qa.orcid.org/reset-password and reset the password for ma_test_[DD][month][YYYY]@mailinator.com
2. Visit http://mailinator.com and check ma_test_[DD][month][YYYY]@mailinator.com
3. Open message from reset@notify.orcid.org with subject [ORCID] About your password reset request 
4. Click (or copy/paste) password reset link
5. Reset password with random value generated using [LastPass generator](https://lastpass.com/generatepassword.php) or similar
6. Visit https://qa.orcid.org/signin and sign in using the new password created in the previous steps


##API
###Public
###Member


* Finally help out by improving these instructions!    
   
