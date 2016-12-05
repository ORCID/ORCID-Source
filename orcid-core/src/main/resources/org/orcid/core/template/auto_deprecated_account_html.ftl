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
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
		<title>${subject}</title>
		<!--  Do not remove -->
		<script type="text/javascript" src="${baseUri}/static/javascript/iframeResizer.contentWindow.min.js"></script>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="http://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666; font-weight: bold;">
			    <@emailMacros.msg "email.common.dear" /><@emailMacros.space />${name}<@emailMacros.msg "email.common.dear.comma" />
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.auto_deprecate.1" />
			</p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;">
				<@emailMacros.msg "email.auto_deprecate.2" /><@emailMacros.space />${clientName}<@emailMacros.space /><@emailMacros.msg "email.auto_deprecate.3" /><@emailMacros.space />${deprecatedAccountCreationDate}<@emailMacros.space /><@emailMacros.msg "email.auto_deprecate.4" />
			</p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;"> 
				<@emailMacros.msg "email.auto_deprecate.5" />${deprecatedId}<@emailMacros.msg "email.auto_deprecate.6" />${primaryId}<@emailMacros.msg "email.auto_deprecate.7" />
			</p>
			<p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #666666;"> 
				<@emailMacros.msg "email.auto_deprecate.8" />             
		    </p>
			<hr />			
		 </div>
	 </body>
 </html>
 </#escape>