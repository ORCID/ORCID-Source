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
	<div class="col-md-9 col-sm-12 col-xs-12 custom-email">
		<!-- Custom emails -->
		<div ng-controller="CustomEmailCtrl">	
			<!-- Top content, instructions -->
			<div class="row">				
				<div class="col-md-10 col-sm-10 col-xs-8">
					<div>
						<h2><@orcid.msg 'custom_email.template.title' /></h2>
					</div>					
				</div>
				<div class="col-md-2 col-sm-2 col-xs-4" ng-show="showCreateButton" ng-cloak>
					<a ng-click="displayCreateForm()" class="pull-right"><span class="label btn-primary"><@orcid.msg 'custom_email.template.create_button' /></span></a>
				</div>	
			</div>	
			
			<div class="row bottom-line">
				<div class="col-md-12 col-sm-12 col-xs-12 instructions">				
					<p><@orcid.msg 'custom_email.template.description.1'/></p>
					<p><@orcid.msg 'custom_email.template.description.2'/></p>										
				</div>					
			</div>
			
			<!-- Show existing emails -->
			<div class="row bottom-line" ng-show="showEmailList">				
				<div class="col-md-12 col-sm-12 col-xs-12 small-padding">	
					<h3><@orcid.msg 'custom_email.template.existing_custom_emails.title' /></h3>					
				</div>
				
				<div class="row" ng-repeat="existingEmail in customEmailList">	
					<div class="inner-row email-list" ng-show="$first">				
						<div class="col-md-1 col-sm-1 col-xs-1 list-header"><@orcid.msg 'custom_email.custom_emails.header.type' /></div>
						<div class="col-md-9 col-sm-9 col-xs-9 list-header"><@orcid.msg 'custom_email.custom_emails.header.subject' /></div>
						<div class="col-md-1 col-sm-1 col-xs-1 list-header">&nbsp;</div>
						<div class="col-md-1 col-sm-1 col-xs-1 list-header">&nbsp;</div>
					</div>				
					<div class="inner-row email-list">				
						<div class="col-md-1 col-sm-1 col-xs-1">{{existingEmail.emailType.value}}</div>
						<div class="col-md-9 col-sm-9 col-xs-9">{{existingEmail.subject.value}}</div>
						<div class="col-md-1 col-sm-1 col-xs-1"><a href ng-click="showEditLayout($index)" class="edit" title="<@orcid.msg 'custom_email.common.edit' />"><span class="glyphicon glyphicon-pencil blue"></span></a></div>
						<div class="col-md-1 col-sm-1 col-xs-1"><a href ng-click="deleteCustomEmail($index)" class="edit" title="<@orcid.msg 'custom_email.common.remove' />"><span class="glyphicon glyphicon-trash blue"></span></a></div>
					</div>
				</div>
			</div>
			
			<!-- Create form -->
			<div ng-show="showCreateForm" class="bottom-line">			
				<div class="row">
					<!-- Sender -->
					<div class="col-md-10 col-sm-10 col-xs-12">
						<div class="inner-row">
							<span><strong><@orcid.msg 'custom_email.template.create.sender.label'/></strong></span>
							<input type="text" placeholder="<@orcid.msg 'custom_email.template.create.sender.placeholder'/>" class="input-xlarge" ng-model="customEmail.sender.value">
							<span class="orcid-error" ng-show="customEmail.sender.errors.length > 0">
								<div ng-repeat='error in customEmail.sender.errors' ng-bind-html="error"></div>
							</span>
						</div>						
					</div>
					<!-- Subject -->
					<div class="col-md-10 col-sm-10 col-xs-12">
						<div class="inner-row">
							<span><strong><@orcid.msg 'custom_email.template.create.subject.label'/></strong></span>
							<input type="text" placeholder="<@orcid.msg 'custom_email.template.create.subject.placeholder'/>" class="input-xlarge" ng-model="customEmail.subject.value">
							<span class="orcid-error" ng-show="customEmail.subject.errors.length > 0">
								<div ng-repeat='error in customEmail.subject.errors' ng-bind-html="error"></div>
							</span>
						</div>
					</div>
					<!-- Content -->
					<div class="col-md-10 col-sm-10 col-xs-12">
						<div class="inner-row content">
							<span><strong><@orcid.msg 'custom_email.template.create.content.label'/></strong></span>
							<textarea placeholder="<@orcid.msg 'custom_email.template.create.content.placeholder'/>" ng-model="customEmail.content.value"></textarea>
							<span class="orcid-error" ng-show="customEmail.content.errors.length > 0">
								<div ng-repeat='error in customEmail.content.errors' ng-bind-html="error"></div>
							</span>
						</div>
					</div>
					<!-- Actions -->
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="inner-row content">				
							<ul class="pull-right actions">							
								<li><a href ng-click="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>
								<li><a href ng-click="saveCustomEmail()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>							
							</ul>					
						</div>
					</div>	
				</div>
			</div>





			<!-- Edit form -->				
			
			<div ng-show="showEditForm" class="bottom-line">			
				<div class="row">
					<!-- Sender -->
					<div class="col-md-10 col-sm-10 col-xs-12">
						<div class="inner-row">
							<span><strong><@orcid.msg 'custom_email.template.create.sender.label'/></strong></span>
							<input type="text" placeholder="<@orcid.msg 'custom_email.template.create.sender.placeholder'/>" class="input-xlarge" ng-model="editedCustomEmail.sender.value">
							<span class="orcid-error" ng-show="editedCustomEmail.sender.errors.length > 0">
								<div ng-repeat='error in customEmail.sender.errors' ng-bind-html="error"></div>
							</span>
						</div>						
					</div>
					<!-- Subject -->
					<div class="col-md-10 col-sm-10 col-xs-12">
						<div class="inner-row">
							<span><strong><@orcid.msg 'custom_email.template.create.subject.label'/></strong></span>
							<input type="text" placeholder="<@orcid.msg 'custom_email.template.create.subject.placeholder'/>" class="input-xlarge" ng-model="editedCustomEmail.subject.value">
							<span class="orcid-error" ng-show="editedCustomEmail.subject.errors.length > 0">
								<div ng-repeat='error in editedCustomEmail.subject.errors' ng-bind-html="error"></div>
							</span>
						</div>
					</div>
					<!-- Content -->
					<div class="col-md-10 col-sm-10 col-xs-12">
						<div class="inner-row content">
							<span><strong><@orcid.msg 'custom_email.template.create.content.label'/></strong></span>
							<textarea placeholder="<@orcid.msg 'custom_email.template.create.content.placeholder'/>" ng-model="editedCustomEmail.content.value"></textarea>
							<span class="orcid-error" ng-show="editedCustomEmail.content.errors.length > 0">
								<div ng-repeat='error in editedCustomEmail.content.errors' ng-bind-html="error"></div>
							</span>
						</div>
					</div>
					<!-- Actions -->
					<div class="col-md-12 col-sm-12 col-xs-12">
						<div class="inner-row content">				
							<ul class="pull-right actions">							
								<li><a href ng-click="showViewLayout()" class="back" title="<@orcid.msg 'manage.developer_tools.tooltip.back' />"><span class="glyphicon glyphicon-arrow-left"></span></a></li>
								<li><a href ng-click="editCustomEmail()" class="save" title="<@orcid.msg 'manage.developer_tools.tooltip.save' />"><span class="glyphicon glyphicon-floppy-disk"></span></a></li>							
							</ul>					
						</div>
					</div>	
				</div>
			</div>			
			
			
			
			
			
			
			
			
			
			
			
			

			<!-- Learn more -->			
			<div class="row">
				<div class="col-md-12 col-sm-12 col-xs-12">					
					<p>	
						<@orcid.msg 'custom_email.template.description.learn_more.1'/>
						<a href="<@orcid.msg 'custom_email.template.description.learn_more.link.url'/>"><@orcid.msg 'custom_email.template.description.learn_more.link.text'/></a>
						<@orcid.msg 'custom_email.template.description.learn_more.2'/>
					</p>							
				</div>
			</div>
				
									
					
					
		</div>
	</div>
</div>
</@public>