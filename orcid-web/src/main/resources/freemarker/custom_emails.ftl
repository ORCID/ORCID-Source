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

<@public nav="custom-mail">
<div class="row">
	<div class="col-md-3 lhs col-sm-12 col-xs-12 padding-fix">
		<#include "includes/id_banner.ftl"/>
	</div>
	<div class="col-md-9 col-sm-12 col-xs-12 developer-tools">
		<!-- Custom emails -->
		<div ng-controller="CustomEmailCtrl" class="sso-api">	
			<!-- Top content, instructions -->
			<div class="row">				
				<div class="col-md-10 col-sm-10 col-xs-8">
					<div class="inner-row">
						<h2><@orcid.msg 'custom_email.template.title' /></h2>
					</div>					
				</div>
				<div class="col-md-2 col-sm-2 col-xs-4" ng-show="showCreateButton" ng-cloak>
					<a ng-click="showCreateForm()" class="pull-right"><span class="label btn-primary"><@orcid.msg 'custom_email.template.create_button' /></span></a>
				</div>	
			</div>
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">				
					<p class="developer-tools-instructions"><@orcid.msg 'custom_email.template.description.1'/></p>
					<p class="developer-tools-instructions"><@orcid.msg 'custom_email.template.description.2'/></p>
					<p class="developer-tools-instructions"><@orcid.msg 'custom_email.template.description.3'/></p>
					<p>
						<ul>
							<li><a href="<@orcid.msg 'custom_email.template.description.email_type.claim.url' />"><@orcid.msg 'custom_email.template.description.email_type.claim.text' /></a></li>
						</ul>
					</p>
				</div>
				<div class="col-md-12 col-sm-12 col-xs-12" ng-show="customEmailList.length > 0">	
					<p><@orcid.msg 'custom_email.template.existing_custom_emails.title'></p>
					<ul>
						<li ng-repeat="customEmail in customEmailList">  
							<div>${{customEmail.emailType.value}}</div>
							<div><a href ng-click="showEditLayout($index)" class="edit" title="<@orcid.msg 'custom_email.common.edit' />"><span class="glyphicon glyphicon-pencil blue"></span></a></div>
							<div><a href ng-click="showDeleteLayout($index)" class="edit" title="<@orcid.msg 'custom_email.common.remove' />"><span class="glyphicon glyphicon-trash blue"></span></a></div>
						</li>
					</ul>
				</div>
			</div>
			
		</div>
	</div>
</div>