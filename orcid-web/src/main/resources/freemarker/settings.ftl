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
<@protected>

	<div class="row">
		<div class="span3">
			<ul>
				<li><a href="#account-settings">${springMacroRequestContext.getMessage("manage.accountsettings")}</a></li>
				<li><a href="#manage-permissions">${springMacroRequestContext.getMessage("manage.managepermission")}</a></li>
			</ul>
		</div>
		<div class="span9">
			<h1 id="account-settings">${springMacroRequestContext.getMessage("manage.accountsettings")}</h1>
			<table class="table table-bordered table-settings">
				<tbody>
					<tr>
						<th>${springMacroRequestContext.getMessage("manage.emails")}</th>
						<td><a href="#">${springMacroRequestContext.getMessage("settings.tdEdit")}</a></td>
					</tr>
					<tr>
						<th>${springMacroRequestContext.getMessage("claim.password")}</th>
						<td><a href="#">${springMacroRequestContext.getMessage("settings.tdEdit")}</a></td>
					</tr>
					<tr>
						<th>${springMacroRequestContext.getMessage("change_security_question.securityquestion")}</th>
						<td><a href="#">${springMacroRequestContext.getMessage("settings.tdEdit")}</a></td>
					</tr>
					<tr>
						<th>${springMacroRequestContext.getMessage("manage.email_preferences")}</th>
						<td><a href="#">${springMacroRequestContext.getMessage("settings.tdEdit")}</a></td>
					</tr>
					<tr>
						<th>${springMacroRequestContext.getMessage("manage.close_account")}</th>
						<td><a href="#">${springMacroRequestContext.getMessage("settings.tddeactivate")}</a></td>
					</tr>
				</tbody>
			</table>
			<h1 id="manage-permissions">${springMacroRequestContext.getMessage("manage.managepermission")}</h1>
			<h4>${springMacroRequestContext.getMessage("manage.trusted_organisations")}</h4>
			<p>${springMacroRequestContext.getMessage("manage.youcanallowpermission")}<br /> 
			<a href="http://support.orcid.org/knowledgebase/articles/131598">${springMacroRequestContext.getMessage("manage.findoutmore")}</a></p>
			<table class="table table-bordered table-settings">
				<thead>
					<tr>
						<th>${springMacroRequestContext.getMessage("manage.thproxy")}</th>
						<th>${springMacroRequestContext.getMessage("settings.tdSiteURL")}</th>
						<th>${springMacroRequestContext.getMessage("manage.thapprovaldate")}</th>
						<th>${springMacroRequestContext.getMessage("manage.thaccesstype")}</th>
						<td></td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>${springMacroRequestContext.getMessage("settings.tdSCOPUS")}</td>
						<td><a href="http://www.scopus.com">${springMacroRequestContext.getMessage("settings.tdscopuscom")}</a></td>
						<td>${springMacroRequestContext.getMessage("settings.td5aug2012")}</td>
						<td>${springMacroRequestContext.getMessage("settings.tdReadLimited")} <br /> ${springMacroRequestContext.getMessage("settings.tdpersonalinfo")}</td>
						<td><a href="#">${springMacroRequestContext.getMessage("manage.revokeaccess")}</a></td>
					</tr>
					<tr>
						<td>${springMacroRequestContext.getMessage("settings.tdjournalof")}</td>
						<td><a href="http://www.pmts.org">${springMacroRequestContext.getMessage("settings.tdpmtscom")}</a></td>
						<td>${springMacroRequestContext.getMessage("settings.td5aug2012")}</td>
						<td>${springMacroRequestContext.getMessage("settings.tdReadLimited")} <br /> ${springMacroRequestContext.getMessage("settings.tdpersonalinfo")}</td>
						<td><a href="#">${springMacroRequestContext.getMessage("manage.revokeaccess")}</a></td>
					</tr>
				</tbody>
			</table>
			<p><a href="#" class="btn btn-primary">${springMacroRequestContext.getMessage("settings.tdorganisation")}</a></p>
			<h4>${springMacroRequestContext.getMessage("settings.tdtrustindividual")}</h4>
			<p>${springMacroRequestContext.getMessage("settings.tdallowpermission")}<br /> <a href="#">${springMacroRequestContext.getMessage("manage.findoutmore")}</a></p>
			<table class="table table-bordered table-settings">
				<thead>
					<tr>
						<th>${springMacroRequestContext.getMessage("manage.thproxy")}</th>
						<th>${springMacroRequestContext.getMessage("home.ORCID")}</th>
						<th>${springMacroRequestContext.getMessage("manage.thapprovaldate")}</th>
						<td></td>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td>${springMacroRequestContext.getMessage("settings.tdJoSmith")}</td>
						<td>0000-0000-0000-000</td>
						<td>${springMacroRequestContext.getMessage("settings.td5aug2012")}</td>
						<td><a href="#">${springMacroRequestContext.getMessage("manage.revokeaccess")}</a></td>
					</tr>
					<tr>
						<td>${springMacroRequestContext.getMessage("settings.MonicaThompson")}</td>
						<td>0000-0000-0000-000</td>
						<td>${springMacroRequestContext.getMessage("settings.td5aug2012")}</td>
						<td><a href="#">${springMacroRequestContext.getMessage("manage.revokeaccess")}</a></td>
					</tr>
				</tbody>
			</table>
			<p><a href="#" class="btn btn-primary">${springMacroRequestContext.getMessage("settings.Addanorganisation")}</a></p>
		</div>
	</div>

</@protected>