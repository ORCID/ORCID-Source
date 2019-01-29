## Test Self Service

### Before you start

For this test there is a fair amount of set up. You need to have a Consortium Lead account on QA and a Consortium Member account . For the purposes of testing you can use the accounts 0000-0003-0641-4661 for the consortium lead account and 0000-0001-5870-8499 for the consortium member account. For additional accounts use the MA testing accounts you created when running through the MA tests.


0. Find and Replace [DD][month][YYYY] in this document with the current day, written month, and four digit year for example 24feb2016

### Check Consortium Lead Functionality
1. Go to https://qa.orcid.org/signin and sign in with

		0000-0003-0641-4661
		Password: test1234. Check that there is a tab 'MEMBER TOOLS'
		
2.  Click on the tab
3.  Check the page loads

### Check adding a consortium member

4. Scroll to the bottom of the page to the Add a new consortium member section:

			Organization Name: [DD][month][YYYY]
			Website : http://www.[DD][month][YYYY].com
			**Contact Information**
			First Name : Self_service
			Last Name: [DD][month][YYYY]
			Email: [DD][month][YYYY]@mailinator.com

**Check the Saleforce staff email to check there is a notification**

5. Visit mailinator.com and enter:	

			selfservicesalesforcetest@mailinator.com
			
6. Check that there is an email titled "Consortium member addition requested - [DD][month][YYYY]"

7.  Visit www.qa.orcid.org/signout


### Check that consortium member can  add, edit and delete  a contact and it updates in salesforce


8. Visit https://qa.orcid.org/signin

		Sign in with
		0000-0001-5870-8499
		test1234
		
9. Go to https://qa.orcid.org/self-service

10. Check the page loads

11.  Add a contact in the Add Member Contacts email field
		Email: pub_ma_test_[DD][month][YYYY]@mailinator (This is the same account as the second account created in the MA tests.)

		Click add

12. Visit https://qa.orcid.org/signout

13. Visit https://qa.orcid.org/signin

14. Sign back in to the Consortium lead account with

		0000-0003-0641-4661
		test1234

15. Click on 'Member Tools'

16. Scroll to 'Contacts for all consortium members'
	 click on 'Click here to view all consortium member contacts'

17. When the page loads check that the  contact from today has been added to New Org 1 in the list of member contacts.

18. Visit https://qa.orcid.org/signout
