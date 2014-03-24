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
<!DOCTYPE html>
<html>
	<head>
	<title>ORCID - Privacy Policy Updates</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="http://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
		    Dear ${emailName},
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		  
				Privacy is a fundamental concern for ORCID. We have completed a rigorous and comprehensive 
                privacy certification with TRUSTe. This process includes a check of our policies, a review 
                of how we communicate our handling of data and privacy, and a review of our dispute 
                procedures.
		
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		  
               Not surprisingly, we found out that our privacy policy was in great shape! We also took 
               this opportunity to make the policy itself clearer and easier to read. You can read more 
               on our <a href="https://orcid.org/blog/2014/03/17/certification-our-privacy-policy">blog</a>. Here are some highlights:
              <ul>
                 <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                    <b>Clearer privacy setting information.</b> We simplified the language to describe the 
                    privacy settings that researchers can control, including images of the privacy 
                    controls.
                 </li>
                 <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                    <b>Control the use of your information.</b> We provide more detail about how we use the 
                    information we collect, and how to manage your inclusion or exclusion in those uses.
                 </li>
                 <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                    <b>Details about commercial access.</b> We clarify our strong controls over how commercial 
                    entities can use your data and provide more details about how you can control 
                    such use.
                 </li>
                 <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                   <b>Managing your ORCID data even when you no longer can.</b> We now include information about how 
                   records are managed once someone is deceased.
                 </li>
                 <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                   <b>Addressing issues when all else fails.</b> We now provide more detail about what we do when 
                   errors are found, including our dispute procedure.
                 </li>
		      </ul>
		    </p>
			<#if verificationUrl??>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		  
               You have not yet verified this email with your ORCID Record. Please do so by clicking this 
               verification link:<br />
               <a href="${verificationUrl}?lang=${locale}">${verificationUrl}?lang=${locale}</a>
		
		    </p>
		  	</#if>  
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		  
               IF WE HAVEN'T SEEN YOU IN A WHILE AT ORCID.ORG, you may have missed some exciting and very 
               useful updates:
               <ul>
                  <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                     <a href="http://orcid.org/blog/2013/12/05/i-claimed-my-orcid-id-now-what">Link to your articles, books, datasets, and identifiers with new Works search & link 
                     wizards</a>
                 </li>
                 <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                     <a href="http://orcid.org/blog/2013/12/09/organizational-affiliations-now-part-orcid-record">Link to your Education and Employment Affiliations</a>
                 </li>
                 <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                    <a href="http://orcid.org/blog/2014/02/19/link-your-orcid-record-your-funding">Connect your ORCID iD to your Funding, Grants, and Awards</a>
                 </li>
                 <li style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
                    <a href="http://orcid.org/about/news">And many more...</a>
                 </li>
		       </ul>
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		  
               Learn more about these and other updates at our <a href="http://orcid.org/about/news">blog</a>, or by 
               logging into your <a href="https://orcid.org/signin">ORCID Record</a>.
		
		    </p>		    
		  	<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
			   <br>
			   Regards,<br>
			   The ORCID Team<br>
			   support@orcid.org<br>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
               You have received this email as a service announcement related to your ORCID Account. 
               Your ORCID iD: <a href="http://orcid.org/${orcid}">http://orcid.org/${orcid}</a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
			   <#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>