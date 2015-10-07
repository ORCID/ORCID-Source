<#--

    =============================================================================

    ORCID (R) Open Source
    http://orcid.org

    Copyright (c) 2012-2014 ORCID, Inc.
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
	<title>Please verify your email - ORCID.org</title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
		    <@emailMacros.msg "email.common.dear" />${emailName},
		    </span>
		    <#if verificationUrl??>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
		    	<@emailMacros.msg "email.service_announcement.verify_account" />
		    </p>
		    <p>
		    	<a href="${verificationUrl}">${verificationUrl}</a><br>
		    	<br>
		    </p>
		    </#if>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.service_announcement.did_you_know_htm" />
			</p>	
			<ul style="font-family: arial, helvetica, sans-serif; font-size: 13px; color: #666666;">
				<li><@emailMacros.msg "email.service_announcement.update_privacy" /></li>
		    	<li><@emailMacros.msg "email.service_announcement.update_emails" /></li>
		    	<li><@emailMacros.msg "email.service_announcement.update_connections" />
		    		<ul style="font-family: arial, helvetica, sans-serif; font-size: 13px; color: #666666;">
		        		<li><@emailMacros.msg "email.service_announcement.update_connections_crossref" /></li>
		           		<li><@emailMacros.msg "email.service_announcement.update_connections_researcher" /></li>
		           		<li><@emailMacros.msg "email.service_announcement.update_connections_knode" /></li>
		        	</ul>
		    	</li>
		  	</ul>
		  	<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
				<@emailMacros.msg "email.service_announcement.read_our_blog" />
				<a href="http://orcid.org/about/news"><@emailMacros.msg "email.service_announcement.blog_link" /></a><br>
				<br>
				<@emailMacros.msg "email.common.kind_regards" />
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #666666;">
				<@emailMacros.msg "email.common.you_have_received_this_email" />
			</p>
			<#include "email_footer.ftl"/>
		 </div>
	 </body>
 </html>