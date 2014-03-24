<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2013 ORCID, Inc.
    Licensed under an MIT-Style License (MIT)
    http://orcid.org/open-source-license

    This copyright and license information (including a link to the full license)
    shall be included in its entirety in all copies or substantial portion of
    the software.

    =============================================================================

-->
<#import "email_macros.ftl" as emailMacros />
Dear ${emailName},

Privacy is a fundamental concern for ORCID. We have completed a rigorous and 
comprehensive privacy certification with TRUSTe. This process includes a check 
of our policies, a review of how we communicate our handling of data and 
privacy, and a review of our dispute procedures.

Not surprisingly, we found out that our privacy policy was in great shape! We 
also took this opportunity to make the policy itself clearer and easier to 
read. You can read more on our blog [1]. Here are some highlights:

* Clearer privacy setting information. 
  We simplified the language to describe the privacy settings that researchers 
  can control, including images of the privacy controls.

* Control the use of your information. 
  We provide more detail about how we use the information we collect, and how 
  to manage your inclusion or exclusion in those uses.

* Details about commercial access. 
  We clarify our strong controls over how commercial entities can use your data 
  and provide more details about how you can control such use.

* Managing your ORCID data even when you no longer can. 
  We now include information about how records are managed once someone is 
  deceased.

* Addressing issues when all else fails. 
  We now provide more detail about what we do when errors are found, including 
  our dispute procedure.
  
  
<#if verificationUrl??>
---
VERIFY YOUR RECORD
You have not yet verified this email with your ORCID Record. Please do so by 
clicking this verification link: 
${verificationUrl}?lang=${locale}
---

</#if>
IF WE HAVEN'T SEEN YOU IN A WHILE AT ORCID.ORG, you may have missed some 
exciting and very useful updates:

* Link to your articles, books, datasets, and identifiers with new Works search 
  & link wizards [2]
* Link to your Education and Employment Affiliations [3]
* Connect your ORCID iD to your Funding, Grants, and Awards [4]
* And many more...

Learn more about these and other updates at our blog [5], or by logging into 
your ORCID Record [6].


Regards,
The ORCID Team
support@orcid.org

You have received this email as a service announcement related to your ORCID 
Account. Your ORCID iD: http://orcid.org/${orcid}


[1] https://orcid.org/blog/2014/03/17/certification-our-privacy-policy
[2] http://orcid.org/blog/2013/12/05/i-claimed-my-orcid-id-now-what
[3] http://orcid.org/blog/2013/12/09/organizational-affiliations-now-part-orcid-record
[4] http://orcid.org/blog/2014/02/19/link-your-orcid-record-your-funding
[5] http://orcid.org/about/news
[6] https://orcid.org/signin
[7] http://rcpeters.asuscomm.com:8080/orcid-web/account
[8] http://rcpeters.asuscomm.com:8080/orcid-web/privacy-policy

---
email preferences [7] | privacy policy [8] | ORCID, Inc. | 
10411 Motor City Drive, Suite 750, Bethesda, MD 20817, USA | http://orcid.org
