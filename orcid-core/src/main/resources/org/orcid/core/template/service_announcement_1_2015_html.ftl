<#import "email_macros.ftl" as emailMacros />
<#escape x as x?html>
<!DOCTYPE html>
<html>
	<head>
	<title><@emailMacros.msg "email.service_announcement.subject.imporant_information" /></title>
	</head>
	<body>
		<div style="padding: 20px; padding-top: 0px;">
			<img src="https://orcid.org/sites/all/themes/orcid/img/orcid-logo.png" alt="ORCID.org"/>
		    <hr />
		  	<span style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C; font-weight: bold;">
		    <@emailMacros.msg "email.service_announcement.dear1" />${emailName}<@emailMacros.msg "email.service_announcement.dear2" />,
		    </span>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<@emailMacros.msg "email.service_announcement.body_intro" />
		    	<a href="https://orcid.org/privacy-policy" target="orcid.blank"><@emailMacros.msg "email.service_announcement.privacy_link" /></a><@emailMacros.msg "email.service_announcement.dot_bottom" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<span style="font-weight:bold"><@emailMacros.msg "email.service_announcement.body_inbox_title" /></span><br>
		    	<@emailMacros.msg "email.service_announcement.body_inbox1" /><a href="http://orcid.org/inbox" target="orcid.blank"><@emailMacros.msg "email.service_announcement.inbox_link" /></a><@emailMacros.msg "email.service_announcement.body_inbox2" /><a href="<@emailMacros.knowledgeBaseUri />360006972953" target="orcid.blank"><@emailMacros.msg "email.service_announcement.inbox_about_link" /></a><@emailMacros.msg "email.service_announcement.body_inbox3" /><@emailMacros.msg "email.service_announcement.body_inbox4" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<span style="font-weight:bold"><@emailMacros.msg "email.service_announcement.body_permission_title" /></span><br><@emailMacros.msg "email.service_announcement.body_permission1" /> <a href="http://www.crossref.org/" target="orcid.blank"><@emailMacros.msg "email.service_announcement.crossref_link" /></a><@emailMacros.msg "email.service_announcement.body_permission2" /><a href="https://www.datacite.org/" target="orcid.blank"><@emailMacros.msg "email.service_announcement.datacite_link" /></a><@emailMacros.msg "email.service_announcement.body_permission3" /><a href="http://orcid.org/blog/2015/01/13/new-webinar-metadata-round-trip" target="orcid.blank"><@emailMacros.msg "email.service_announcement.updates_link" /></a><@emailMacros.msg "email.service_announcement.body_permission4" /><a href="<@emailMacros.knowledgeBaseUri />360006972953" target="orcid.blank"><@emailMacros.msg "email.service_announcement.inbox_about_link" /></a><@emailMacros.msg "email.service_announcement.body_permission5" /><@emailMacros.msg "email.service_announcement.body_permission6" />
		    </p>
		    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
		    	<span style="font-weight:bold"><@emailMacros.msg "email.service_announcement.body_privacy_policy_title" /></span><br>
		    	<@emailMacros.msg "email.service_announcement.body_privacy_policy" />
		    </p>
		    <#if verificationUrl??>
			    <p style="font-family: arial, helvetica, sans-serif; font-size: 15px; color: #494A4C;">
			    	<@emailMacros.msg "email.service_announcement.verify_account" /><br />
			    	<a href="${verificationUrl}">${verificationUrl}</a>
			    	<br>
			    </p>
		    </#if>
		  	<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<@emailMacros.msg "email.service_announcement.body_updates1" />
				<ul style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				   <li>
				     <a href="http://orcid.org/blog/2014/12/11/new-feature-friday-new-orcid-record-interface" target="orcid.blank"><@emailMacros.msg "email.service_announcement.steamlined_link" /></a><@emailMacros.msg "email.service_announcement.body_updates2" />
				   </li>
				   <li>
				      <@emailMacros.msg "email.service_announcement.body_updates3" /><a href="http://orcid.org/blog/2015/06/17/humanists-rejoice-mla-international-bibliography-now-connects-orcid" target="orcid.blank"><@emailMacros.msg "email.service_announcement.wizard_link" /></a><@emailMacros.msg "email.service_announcement.body_updates4" />
				   </li>
				   <li>
				      <a href="http://orcid.org/blog/2014/11/14/new-functionality-friday-orcid-id-qr-codes" target="orcid.blank"><@emailMacros.msg "email.service_announcement.qr_link" /></a>
				   </li>
				   <li>
				      <a href="http://orcid.org/blog/2014/11/28/new-functionality-friday-orcid-site-%D1%80%D1%83%D1%81%D1%81%D0%BA%D0%B8%D0%B9-and-portugu%C3%AAs" target="orcid.blank"><@emailMacros.msg "email.service_announcement.language_link" /></a><@emailMacros.msg "email.service_announcement.body_updates5" />
				    </li>
				    <li>
				    	<@emailMacros.msg "email.service_announcement.body_updates6" />
				    </li>
				    <li>
				    	<@emailMacros.msg "email.service_announcement.body_updates7" /><a href="<@emailMacros.knowledgeBaseUri />360006896894" target="orcid.blank"><@emailMacros.msg "email.service_announcement.here_link" /></a><@emailMacros.msg "email.service_announcement.body_updates8" />
				    </li>
				</ul>
		   </p>
		   <p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<@emailMacros.msg "email.service_announcement.regards" />
				<br><@emailMacros.msg "email.service_announcement.orcid_team" />
				<br><a href="<@orcid.msg "email.service_announcement.support_id" />"><@orcid.msg "email.service_announcement.support_id" /></a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<span style="font-weight:bold"><@emailMacros.msg "email.service_announcement.footer_text1_title" /></span>
				<br><@emailMacros.msg "email.service_announcement.footer_text1" />
				<br>
				<br><@emailMacros.msg "email.service_announcement.footer_text2" />
				<br>
				<br><@emailMacros.msg "email.service_announcement.footer_text_unsubscribe" /><a href="${emailFrequencyUrl}" target="orcid.blank"><@emailMacros.msg "email.service_announcement.footer_frequency_link" /></a>
				<br>
				<br><@emailMacros.msg "email.service_announcement.footer_text3" /><a href="${baseUri}/account" target="orcid.blank"><@emailMacros.msg "email.service_announcement.footer_account_link" /></a><@emailMacros.msg "email.service_announcement.footer_text4" />
				<br>
				<br><@emailMacros.msg "email.service_announcement.footer_orcid_id" /><a href="${baseUri}/${orcid}" target="orcid.blank">${baseUri}/${orcid}</a>
			</p>
			<p style="font-family: arial,  helvetica, sans-serif;font-size: 15px;color: #494A4C;">
				<#include "email_footer_html.ftl"/>
			</p>
		 </div>
	 </body>
 </html>
 </#escape>
