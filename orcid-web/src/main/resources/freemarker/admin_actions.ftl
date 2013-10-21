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
<@public >
<script type="text/ng-template" id="success-modal">
  <div style="padding:20px">
    <a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    <h1><@orcid.msg 'admin.success'/></h1>
    <p id="success-message">{{successMessage}}</p>    
  </div>
</script>

<script type="text/ng-template" id="confirm-deprecation-modal">
  <div style="padding:20px">
    <a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    <h1><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm'/></h1>
    
    <div ng-show="errors.length">
		<span class="orcid-error" ng-repeat='error in errors' ng-bind-html-unsafe="error"></span><br />
	</div>	    	    
    <div class="bottom-margin-small">
	    <p><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm.message.1'/></p>
	    <table border="0">
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.orcid'/></strong></td>
		    	<td>{{deprecatedAccount.orcid}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.name'/></strong></td>
		    	<td>{{deprecatedAccount.givenNames}}&nbsp;{{deprecatedAccount.familyName}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.email'/></strong></td>
		    	<td>{{deprecatedAccount.primaryEmail}}</td>
		    </tr>		    
	    </table>
	</div>
	<div class="bottom-margin-small">	    
	    <p><@orcid.msg 'admin.profile_deprecation.deprecate_account.confirm.message.2'/></p>
	    <table>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.orcid'/></strong></td>
		    	<td>{{primaryAccount.orcid}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.name'/></strong></td>
		    	<td>{{primaryAccount.givenNames}}&nbsp;{{primaryAccount.familyName}}</td>
		    </tr>
		    <tr>
		    	<td><strong><@orcid.msg 'admin.profile_deprecation.email'/></strong></td>
		    	<td>{{primaryAccount.primaryEmail}}</td>
		    </tr>	    
	    </table>
	</div>		   	
    <div class="controls save-btns pull-right bottom-margin-small">
    	<span id="bottom-deprecate-profile" ng-click="deprecateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deprecation.deprecate_account'/></span>
	</div>
  </div>
</script>

<script type="text/ng-template" id="confirm-deactivation-modal">
	<div style="padding:20px">
    	<a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    	<h1><@orcid.msg 'admin.profile_deactivation.confirm'/></h1>
		<div class="bottom-margin-small">
	    	<p><@orcid.msg 'admin.profile_deactivation.confirm.message'/></p>
	    	<table border="0">
		    	<tr>
			    	<td><strong><@orcid.msg 'admin.profile_deprecation.orcid'/></strong></td>
			    	<td>{{deactivatedAccount.orcid}}</td>
			    </tr>
		    	<tr>
			    	<td><strong><@orcid.msg 'admin.profile_deprecation.name'/></strong></td>
			    	<td>{{deactivatedAccount.givenNames}}&nbsp;{{deactivatedAccount.familyName}}</td>
			    </tr>
		    	<tr>
			    	<td><strong><@orcid.msg 'admin.profile_deprecation.email'/></strong></td>
			    	<td>{{deactivatedAccount.email}}</td>
			    </tr>		    
	    	</table>
		</div>
		<div class="controls save-btns pull-right bottom-margin-small">
	    	<span id="bottom-deactivate-profile" ng-click="deactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deactivation.deactivate_account'/></span>
		</div>
	</div>
</script>


<script type="text/ng-template" id="confirm-reactivation-modal">
	<div style="padding:20px">
    	<a id="cboxClose" class="btn pull-right close-button" ng-click="closeModal()">X</a>
    	<h1><@orcid.msg 'admin.profile_reactivation.confirm'/></h1>
		<div class="bottom-margin-small">
	    	<span><@orcid.msg 'admin.profile_reactivation.confirm.message'/></span>
			<br />
			<span>{{orcidToReactivate}}</span>				    	
		</div>
		<div class="controls save-btns pull-right bottom-margin-small">
	    	<span id="bottom-deactivate-profile" ng-click="reactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_reactivation.reactivate_account'/></span>
		</div>
	</div>
</script>


<div class="row">
	<div class="span3 lhs override">
		<ul class="settings-nav">
			<li><a href="#account-settings">${springMacroRequestContext.getMessage("manage.accountsettings")}</a></li>
			<li><a href="#manage-permissions">${springMacroRequestContext.getMessage("manage.managepermission")}</a></li>
			<@security.authorize ifAnyGranted="ROLE_ADMIN">
				<li><a href="<@spring.url "/admin" />"><@orcid.msg 'admin.workspace_link' /></a></li>
			</@security.authorize>				
		</ul>
	</div>
	<div class="span9">
		<div ng-controller="profileDeprecationCtrl" class="workspace-accordion-item" ng-cloak>
			<p>
				<@orcid.msg 'admin.profile_deprecation' />&nbsp;
				<a ng-hide="showModal" ng-click="toggleDeprecationModal()" class="icon-plus-sign blue"></a>
				<a ng-show="showModal" ng-click="toggleDeprecationModal()" class="icon-minus-sign blue"></a>
			</p>		
			<div class="collapsible" id="deprecation_modal" style="display:none;">
		    	<h2><@orcid.msg 'admin.profile_deprecation.deprecate_account.title'/></h2>
		    	<br />
				<div>
					<label for="deprecated_orcid"><@orcid.msg 'admin.profile_deprecation.to_deprecate' /></label>
					<input type="text" id="deprecated_orcid" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.account_to_deprecate' />" class="input-xlarge" ng-model="deprecatedAccount.orcid" ng-change="findAccountDetails('deprecated')">
					<a href class="icon-ok green" ng-show="deprecated_verified"></a>
					<a href class="icon-remove red" ng-show="deprecated_verified == false"></a>
					<div id="invalid-regex-deprecated" ng-show="invalid_regex_deprecated" ng-cloak>
						<span class="orcid-error"><@orcid.msg 'admin.profile_deprecation.errors.invalid_regex' /></span><br />
					</div>
					<div ng-show="deprecatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in deprecatedAccount.errors' ng-bind-html-unsafe="error"></span><br />	
					</div>
				</div>
				<div>
					<label for="deprecated_orcid"><@orcid.msg 'admin.profile_deprecation.primary' /></label>
					<input type="text" id="primary_orcid" placeholder="<@orcid.msg 'admin.profile_deprecation.placeholder.primary_account' />" class="input-xlarge" ng-model="primaryAccount.orcid" ng-change="findAccountDetails('primary')">				
					<a href class="icon-ok green" ng-show="primary_verified"></a>
					<a href class="icon-remove red" ng-show="primary_verified == false"></a>
					<div id="invalid-regex-primary" ng-show="invalid_regex_primary" ng-cloak>
						<span class="orcid-error"><@orcid.msg 'admin.profile_deprecation.errors.invalid_regex' /></span><br />
					</div>
					<div ng-show="primaryAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in primaryAccount.errors' ng-bind-html-unsafe="error"></span><br />
					</div>				
				</div>
				<div class="controls save-btns pull-left bottom-margin-small">
		    		<span id="bottom-confirm-deprecate-profile" ng-click="confirmDeprecateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deprecation.deprecate_account'/></span>
				</div>
			</div>
		</div>		
		<br />			  
		<div ng-controller="profileDeactivationAndReactivationCtrl" class="workspace-accordion-item" ng-cloak>
			<p><@orcid.msg 'admin.profile_deactivation' />&nbsp;<a ng-click="toggleDeactivationModal()" class="icon-plus-sign blue"></a></p>			  	
			<div class="collapsible" id="deactivation_modal" style="display:none;">
				<h2><@orcid.msg 'admin.profile_deactivation.deactivate_account.title'/></h2>
	    		<br />
	    		<div>
					<label for="orcid_to_deactivate"><@orcid.msg 'admin.profile_deactivation.to_deactivate' /></label>
					<input type="text" id="orcid_to_deactivate" ng-model="orcidToDeactivate" placeholder="<@orcid.msg 'admin.profile_deactivation.placeholder.to_deactivate' />" class="input-xlarge" />
					<span id="bottom-confirm-deactivate-profile" ng-click="confirmDeactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_deactivation.deactivate_account'/></span>				
					<div ng-show="deactivatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in deactivatedAccount.errors' ng-bind-html-unsafe="error"></span><br />
					</div>		
				</div>
			</div>
		</div>
		<br />
		<div ng-controller="profileDeactivationAndReactivationCtrl" class="workspace-accordion-item" ng-cloak>
			<p><@orcid.msg 'admin.profile_reactivation' />&nbsp;<a ng-click="toggleReactivationModal()" class="icon-plus-sign blue"></a></p>
			<div class="collapsible" id="reactivation_modal" style="display:none;">
				<h2><@orcid.msg 'admin.profile_reactivation.reactivate_account.title'/></h2>
	    		<br />
	    		<div>
					<label for="orcid_to_reactivate"><@orcid.msg 'admin.profile_reactivation.to_reactivate' /></label>
					<input type="text" id="orcid_to_reactivate" ng-model="orcidToReactivate" placeholder="<@orcid.msg 'admin.profile_reactivation.placeholder.to_reactivate' />" class="input-xlarge" />
					<span id="bottom-confirm-reactivate-profile" ng-click="confirmReactivateAccount()" class="btn btn-primary"><@orcid.msg 'admin.profile_reactivation.reactivate_account'/></span>			
					<div ng-show="reactivatedAccount.errors.length">
						<span class="orcid-error" ng-repeat='error in reactivatedAccount.errors' ng-bind-html-unsafe="error"></span><br />
					</div>		
				</div>
			</div>
		</div>
	</div>
		
</div>
</@public >