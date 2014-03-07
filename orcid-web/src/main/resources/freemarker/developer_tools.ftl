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

<@protected classes=['developer-tools'] nav="developer-tools">
<div class="row">
	<div class="col-md-3 lhs override">
		<ul class="settings-nav">
			<li><a href="#account-settings">${springMacroRequestContext.getMessage("manage.accountsettings")}</a></li>
			<li><a href="#manage-permissions">${springMacroRequestContext.getMessage("manage.managepermission")}</a></li>
			<#if (profile.groupType)?? && ((profile.groupType) = "BASIC" ||
			(profile.groupType) = "PREMIUM" || (profile.groupType) =
			"BASIC_INSTITUTION" || (profile.groupType) = "PREMIUM_INSTITUTION")>
			<li><a href="<@spring.url "/manage-clients" />">${springMacroRequestContext.getMessage("workspace.ManageClientCredentials")}</a></li>
			</#if>
			<li></li>
		</ul>
	</div>
	<div class="col-md-9 developer-tools">
		<!-- Developer public API Applications -->
		<div class="row box">
			<div class="col-md-10">
				<h2>DEVELOPER PUBLIC API APPLICATIONS</h2>
			</div>
			<div class="col-md-2">
				<a href=""><span class="label btn-primary cboxElement">Register New</span></a>
			</div>	
		</div>
		<div class="row">
			<div class="col-md-12">
				<p>Do you want to develop an application that uses the <u>ORCID Public API</u>? <u>Register an application to</u>:</p>
				<ul>
					<li><a href=""><span class="glyphicon glyphicon-link"></span> Search the public data in the ORCID Registry.</a></li>
					<li><a href=""><span class="glyphicon glyphicon-link"></span> Get authenticated ORCID IDs from users.</a></li>
					<li><a href=""><span class="glyphicon glyphicon-link"></span> User ORCID's social login for user authentication.</a></li>
				</ul>
			</div>
		</div>
		<!-- Member API Applications -->
		<div class="row box">
			<div class="col-md-10">
				<h2>MEMBER API APPLICATIONS</h2>
			</div>
			<div class="col-md-2">
				<a href=""><span class="label btn-primary cboxElement">Register New</span></a>
			</div>	
		</div>		
		<div class="row">
			<div class="col-md-12">
				<p>These are the applications you have registered to use the <u>ORCID Member API</u>:</p>		
				<table class="table sub-table">
					<tbody>
						<tr>
							<td colspan="12" class="table-header-dt">
								GROUP ID: 0000-0000-0000-0000 (Premium Creator)
							</td>						
						</tr>	
						<tr>
							<td colspan="8">
								Laura's App (http://about.orcid.org)
							</td>												
							<td colspan="4" class="pull-right">
								<span class="label label-info-green">42 users</span>
							</td>									
						</tr>
						<tr>
							<td colspan="8">
								Fran's App (http://fran.orcid.org)
							</td>												
							<td colspan="4" class="pull-right">
								<span class="label label-info-green">42 users</span>
							</td>									
						</tr>
													
					</tbody>
				</table>	
				
				<table class="table">
					<tbody>
						<tr>
							<td colspan="12" class="table-header-dt">
								GROUP ID: 0000-0000-0000-0000 (Premium Creator)
							</td>						
						</tr>	
						<tr>
							<td colspan="8">
								Laura's App (http://about.orcid.org)
							</td>												
							<td colspan="4" class="pull-right">
								<span class="label label-info-green">42 users</span>
							</td>									
						</tr>
						<tr>
							<td colspan="8">
								Fran's App (http://fran.orcid.org)
							</td>												
							<td colspan="4" class="pull-right">
								<span class="label label-info-green">42 users</span>
							</td>									
						</tr>
													
					</tbody>
				</table>
			</div>			
		</div>							
	</div>
</div>

<script type="text/ng-template" id="">
	
</script>

</@protected>
