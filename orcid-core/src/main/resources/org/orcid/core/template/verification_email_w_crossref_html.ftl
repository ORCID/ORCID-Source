<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
	<title>Please verify your email - ORCID.org</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">
		    Dear ${emailName},
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	At ORCID we care about the accuracy and security of your information. Help us in this effort by verifying
		    	the primary email address associated with your ORCID iD. Please click on the link below (or paste it into
		    	your browser), and then sign into your ORCID account.
		    </p>
		    <p>
		    	<a href="${verificationUrl}">${verificationUrl}</a><br>
		    	<br>
		    </p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
				DID YOU KNOW?<br>
				The ORCID Registry is constantly under development, with new releases often, sometimes weekly! Explore some of our recent updates:
			</p>	
			<ul style="font-family: arial, helvetica, sans-serif; font-size: 13px; color: #494A4C;">
				<li>Easier privacy management</li>
		    	<li>The ability to associate multiple emails with your account</li>
		    	<li>Great new connections and updates with ORCID Member organizations including
		    		<ul style="font-family: arial, helvetica, sans-serif; font-size: 13px; color: #494A4C;">
		        		<li>CrossRef: improved search for adding works to your ORCID record</li>
		           		<li>ResearcherID: more connections between your ResearcherID profile and ORCID record</li>
		           		<li>KNODE: automatic cross-link to your ORCID iD with public data sources</li>
		        	</ul>
		    	</li>
		  	</ul>
		  	<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				See these and other updates by clicking on the link above, or reading our blog 
				<a href="http://orcid.org/about/news">(http://orcid.org/about/news)</a>.<br>
				<br>
				Regards,<br>
				The ORCID Team<br>
				support@orcid.org<br>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				You have received this email as a service announcement related to your ORCID Account.
			</p>
			<#include "email_footer.ftl"/>
		 </div>
	 </body>
 </html>
 </#escape>
